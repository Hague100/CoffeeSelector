package com.example.coffeeselector.ui.basket

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.OrderDataClass
import com.example.coffeeselector.data.repository.OrderRepository
import com.example.coffeeselector.data.repository.UserRepository
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Date

class BasketViewModel: ViewModel() {
    private val auth = FirebaseAuth.getInstance()
    private val database = Firebase.firestore
    private val orderRepository = OrderRepository(database, auth)
    private val userRepository = UserRepository(database, auth)
    private val _incompleteOrder = MutableStateFlow<OrderDataClass?>(null)
    val incompleteOrder: StateFlow<OrderDataClass?> get() = _incompleteOrder.asStateFlow()
    private val _isSyncing = MutableStateFlow(false)
    val isSyncing: StateFlow<Boolean> = _isSyncing.asStateFlow()

    init {
        restoreDraftOrder()
    }

    fun restoreDraftOrder() {
        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val draftId = userRepository.readUser().getOrNull()?.unfinishedOrder
                if (!draftId.isNullOrEmpty()) {
                    orderRepository.readOrderData(draftId).onSuccess { draft ->
                        _incompleteOrder.value = draft
                    }
                }
            } finally {
                _isSyncing.value = false
            }
        }
    }

    private fun checkNeedToStartNewOrder(locationId: String): Boolean {
        val currentOrder = _incompleteOrder.value
        return currentOrder == null || currentOrder.locationId != locationId
    }

    fun addOneItemToBasket(locationId: String, itemId: String, itemPrice: Double) {
        if (_isSyncing.value) return
        val current = _incompleteOrder.value

        if (checkNeedToStartNewOrder(locationId)) {
            val oldDraftId = current?.id.orEmpty()
            val newDraft = OrderDataClass(
                userId = auth.currentUser?.uid.orEmpty(),
                locationId = locationId,
                customerEmail = auth.currentUser?.email.orEmpty(),
                itemIdQuantityObject = mapOf(itemId to 1),
                totalCost = itemPrice
            )

            _incompleteOrder.value = newDraft

            viewModelScope.launch {
                _isSyncing.value = true
                try {
                    if (oldDraftId.isNotEmpty()) {
                        orderRepository.deleteUninitiatedOrder(oldDraftId)
                    }
                    orderRepository.createOrderData(newDraft).onSuccess { generatedDocId ->
                        _incompleteOrder.update { it?.copy(id = generatedDocId) }
                    }
                } finally {
                    _isSyncing.value = false
                }
            }
        } else {
            val existing = current ?: return
            val updatedMap = existing.itemIdQuantityObject.toMutableMap()
            updatedMap[itemId] = (updatedMap[itemId] ?: 0) + 1
            val updatedTotal = existing.totalCost + itemPrice

            _incompleteOrder.value = existing.copy(
                itemIdQuantityObject = updatedMap,
                totalCost = updatedTotal
            )

            if (existing.id.isNotEmpty()) {
                viewModelScope.launch {
                    _isSyncing.value = true
                    try {
                        orderRepository.updateOrderAttribute(existing.id, "itemIdQuantityObject", updatedMap)
                        orderRepository.updateOrderAttribute(existing.id, "totalCost", updatedTotal)
                    } finally {
                        _isSyncing.value = false
                    }
                }
            }
        }
    }

    fun removeOneItemFromBasket(itemId: String, itemPrice: Double) {
        if (_isSyncing.value) return
        val current = _incompleteOrder.value ?: return
        val currentQty = current.itemIdQuantityObject[itemId] ?: return
        val updatedMap = current.itemIdQuantityObject.toMutableMap()

        if (currentQty > 1) {
            updatedMap[itemId] = currentQty - 1
        } else {
            updatedMap.remove(itemId)
        }

        if (updatedMap.isEmpty()) {
            _incompleteOrder.value = null
            if (current.id.isNotEmpty()) {
                viewModelScope.launch {
                    _isSyncing.value = true
                    try {
                        orderRepository.deleteUninitiatedOrder(current.id)
                    } finally {
                        _isSyncing.value = false
                    }
                }
            }
        } else {
            val updatedTotal = (current.totalCost - itemPrice).coerceAtLeast(0.0)
            _incompleteOrder.value = current.copy(
                itemIdQuantityObject = updatedMap,
                totalCost = updatedTotal
            )

            if (current.id.isNotEmpty()) {
                viewModelScope.launch {
                    _isSyncing.value = true
                    try {
                        orderRepository.updateOrderAttribute(current.id, "itemIdQuantityObject", updatedMap)
                        orderRepository.updateOrderAttribute(current.id, "totalCost", updatedTotal)
                    } finally {
                        _isSyncing.value = false
                    }
                }
            }
        }
    }

    fun clearBasket() {
        if (_isSyncing.value) return
        val currentDraftId = _incompleteOrder.value?.id.orEmpty()
        _incompleteOrder.value = null

        if (currentDraftId.isNotEmpty()) {
            viewModelScope.launch {
                _isSyncing.value = true
                try {
                    orderRepository.deleteUninitiatedOrder(currentDraftId)
                } finally {
                    _isSyncing.value = false
                }
            }
        }
    }

    fun submitOrder(onResult: (Result<Unit>) -> Unit) {
        if (_isSyncing.value) return
        val orderToSubmit = _incompleteOrder.value ?: return

        viewModelScope.launch {
            _isSyncing.value = true
            try {
                val result = if (orderToSubmit.id.isNotEmpty()) {
                    orderRepository.initiateOrder(orderToSubmit.id)
                } else {
                    val finalizedOrder = orderToSubmit.copy(dateTimeOrderInitiated = Date())
                    orderRepository.createOrderData(finalizedOrder).map { }
                }
                if (result.isSuccess) _incompleteOrder.value = null
                onResult(result)
            } finally {
                _isSyncing.value = false
            }
        }
    }
}