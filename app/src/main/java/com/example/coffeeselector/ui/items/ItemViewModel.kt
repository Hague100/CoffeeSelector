package com.example.coffeeselector.ui.items

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.model.OrderDataClass
import com.example.coffeeselector.data.repository.ItemRepository
import com.example.coffeeselector.data.repository.OrderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.Locale

class ItemViewModel(
    private val itemRepository: ItemRepository,
    private val orderRepository: OrderRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(MenuUiState())
    val uiState: StateFlow<MenuUiState> = _uiState.asStateFlow()
    private val orderSyncMutex = Mutex()

    fun initializeMenu(
        locationId: String,
        itemsIdAndAvailability: List<Pair<String, Boolean>>,
        unfinishedOrderId: String? = null
    ) {
        _uiState.update {
            it.copy(
                isLoading = true,
                errorMessage = null,
                orderSubmitted = false,
                submittedOrderId = null,
                locationId = locationId,
                menuItemsIdAndAvailability = itemsIdAndAvailability,
                activeDraftOrderId = unfinishedOrderId
            )
        }
        viewModelScope.launch {
            val itemResults = itemsIdAndAvailability.map { (id, _) ->
                async { id to itemRepository.readItem(id) }
            }.awaitAll()
            val successfulItems = mutableMapOf<String, ItemDataClass>()
            var firstError: String? = null

            itemResults.forEach { (id, result) ->
                result.fold(
                    onSuccess = { item -> successfulItems[id] = item },
                    onFailure = { exception ->
                        if (firstError == null) firstError = exception.message ?: "Failed to load menu items"
                    }
                )
            }
            var restoredQuantities = emptyMap<String, Int>()

            if (unfinishedOrderId != null) {
                orderRepository.readOrderData(unfinishedOrderId).fold(
                    onSuccess = { order ->
                        if (order.locationId == locationId && order.dateTimeOrderInitiated == null) {
                            restoredQuantities = order.itemIdQuantityObject
                        } else _uiState.update { it.copy(activeDraftOrderId = null) }
                    },
                    onFailure = { _uiState.update { it.copy(activeDraftOrderId = null) } }
                )
            }
            val initialCost = calculateTotalCost(restoredQuantities, successfulItems)

            _uiState.update {
                it.copy(
                    isLoading = false,
                    loadedItems = successfulItems,
                    itemQuantities = restoredQuantities,
                    totalCost = initialCost,
                    errorMessage = firstError
                )
            }
        }
    }

    fun addItemToOrder(itemId: String) {
        val currentQty = _uiState.value.itemQuantities[itemId] ?: 0
        val updatedQuantities = _uiState.value.itemQuantities + (itemId to currentQty + 1)
        updateOrderStateAndSync(updatedQuantities)
    }

    fun removeItemFromOrder(itemId: String) {
        val currentQty = _uiState.value.itemQuantities[itemId] ?: 0
        if (currentQty <= 0) return
        val updatedQuantities = if (currentQty == 1) _uiState.value.itemQuantities - itemId
            else _uiState.value.itemQuantities + (itemId to currentQty - 1)
        updateOrderStateAndSync(updatedQuantities)
    }

    fun submitCurrentOrder() {
        val draftId = _uiState.value.activeDraftOrderId ?: return
        if (_uiState.value.itemQuantities.isEmpty()) return
        viewModelScope.launch {
            orderSyncMutex.withLock {
                _uiState.update { it.copy(isSavingOrder = true, errorMessage = null) }
                orderRepository.initiateOrder(draftId).fold(
                    onSuccess = {
                        _uiState.update {
                            it.copy(
                                isSavingOrder = false,
                                orderSubmitted = true,
                                submittedOrderId = draftId,
                                activeDraftOrderId = null,
                                itemQuantities = emptyMap(),
                                totalCost = 0.0
                            )
                        }
                    },
                    onFailure = { exception ->
                        _uiState.update {
                            it.copy(
                                isSavingOrder = false,
                                errorMessage = exception.message ?: "Failed to submit order"
                            )
                        }
                    }
                )
            }
        }
    }

//    fun clearError() = _uiState.update { it.copy(errorMessage = null) }

    private fun updateOrderStateAndSync(updatedQuantities: Map<String, Int>) {
        val updatedTotalCost = calculateTotalCost(updatedQuantities, _uiState.value.loadedItems)
        _uiState.update {
            it.copy(
                itemQuantities = updatedQuantities,
                totalCost = updatedTotalCost
            )
        }

        viewModelScope.launch {
            orderSyncMutex.withLock {
                _uiState.update { it.copy(isSavingOrder = true) }
                val currentState = _uiState.value
                val draftId = currentState.activeDraftOrderId

                when {
                    updatedQuantities.isEmpty() && draftId != null -> {
                        orderRepository.deleteUninitiatedOrder(draftId).fold(
                            onSuccess = { _uiState.update { it.copy(activeDraftOrderId = null) } },
                            onFailure = { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                        )
                    }
                    draftId == null && updatedQuantities.isNotEmpty() -> {
                        val newOrder = OrderDataClass(
                            locationId = currentState.locationId,
                            itemIdQuantityObject = updatedQuantities,
                            totalCost = updatedTotalCost
                        )
                        orderRepository.createOrderData(newOrder).fold(
                            onSuccess = { newId -> _uiState.update { it.copy(activeDraftOrderId = newId) } },
                            onFailure = { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                        )
                    }
                    draftId != null -> {
                        orderRepository.updateOrderItemsAndTotalCost(
                            docId = draftId,
                            items = updatedQuantities,
                            totalCost = updatedTotalCost
                        ).onFailure { e -> _uiState.update { it.copy(errorMessage = e.message) } }
                    }
                }
                _uiState.update { it.copy(isSavingOrder = false) }
            }
        }
    }

    private fun calculateTotalCost(quantities: Map<String, Int>,
                                   items: Map<String, ItemDataClass>): Double {
        return quantities.entries.sumOf { (itemId, qty) ->
            val price = items[itemId]?.itemPrice ?: 0.0
            price * qty
        }
    }

    companion object {
        fun provideFactory(itemRepository: ItemRepository, orderRepository:
        OrderRepository): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return ItemViewModel(itemRepository, orderRepository) as T
            }
        }
    }
}

data class MenuUiState(
    val isLoading: Boolean = false,
    val isSavingOrder: Boolean = false,
    val orderSubmitted: Boolean = false,
    val submittedOrderId: String? = null,
    val errorMessage: String? = null,
    val locationId: String = "",
    val menuItemsIdAndAvailability: List<Pair<String, Boolean>> = emptyList(),
    val loadedItems: Map<String, ItemDataClass> = emptyMap(),
    val itemQuantities: Map<String, Int> = emptyMap(),
    val totalCost: Double = 0.0,
    val activeDraftOrderId: String? = null
) {
    val formattedTotalCost: String
        get() = String.format(Locale.UK, "£%.2f", totalCost)

    val totalItemCount: Int
        get() = itemQuantities.values.sum()
}