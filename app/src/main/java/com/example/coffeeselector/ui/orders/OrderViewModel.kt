package com.example.coffeeselector.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.coffeeselector.data.model.ItemDataClass
import com.example.coffeeselector.data.model.NotificationDataClass
import com.example.coffeeselector.data.model.OrderDataClass
import com.example.coffeeselector.data.repository.ItemRepository
import com.example.coffeeselector.data.repository.NotificationRepository
import com.example.coffeeselector.data.repository.OrderRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class OrderViewModel(
    private val orderRepository: OrderRepository,
    private val itemRepository: ItemRepository,
    private val notificationRepository: NotificationRepository
) : ViewModel() {
    private val _orderData = MutableStateFlow<OrderDataClass?>(null)
    val orderData: StateFlow<OrderDataClass?> = _orderData.asStateFlow()
    private val _itemsMap = MutableStateFlow<Map<String, ItemDataClass>>(emptyMap())
    val itemsMap: StateFlow<Map<String, ItemDataClass>> = _itemsMap.asStateFlow()
    private val _statusMessage = MutableStateFlow<String?>(null)
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    private val _pastOrdersMap = MutableStateFlow<Map<String, OrderDataClass>>(emptyMap())
    val pastOrdersMap: StateFlow<Map<String, OrderDataClass>> = _pastOrdersMap.asStateFlow()
    private val _ordersMap = MutableStateFlow<Map<String, OrderDataClass>>(emptyMap())
    val ordersMap: StateFlow<Map<String, OrderDataClass>> = _ordersMap.asStateFlow()
    private val activeSimulations = mutableSetOf<String>()
    fun simulateOrderLifecycle(orderId: String, alreadyElapsedMs: Long = 0L) {
        if (!activeSimulations.add(orderId)) return

        viewModelScope.launch {
            try {
                val waitToReady = (PROCESSING_TO_READY_MS - alreadyElapsedMs).coerceAtLeast(0L)
                if (waitToReady > 0L) delay(waitToReady.milliseconds)

                if (_ordersMap.value[orderId]?.dateTimeOrderFinished == null) {
                    orderRepository.updateOrderAsFinished(orderId).onSuccess { finishedTime ->
                        updateLocalOrder(orderId) { it.copy(dateTimeOrderFinished = finishedTime) }
                        sendOrderStatusNotification(
                            message = "Order #$orderId is ready to collect!"
                        )
                    }
                }
                val readyTargetMs = PROCESSING_TO_READY_MS + READY_TO_COLLECTED_MS
                val waitToCollected = (readyTargetMs - maxOf(alreadyElapsedMs, PROCESSING_TO_READY_MS))
                    .coerceAtLeast(0L)
                if (waitToCollected > 0L) delay(waitToCollected.milliseconds)

                if (_ordersMap.value[orderId]?.dateTimeOrderCollected == null) {
                    orderRepository.updateOrderAsCollected(orderId).onSuccess { collectedTime ->
                        updateLocalOrder(orderId) { it.copy(dateTimeOrderCollected = collectedTime) }
                        sendOrderStatusNotification(
                            message = "Order #$orderId has been collected. Enjoy your coffee!"
                        )
                    }
                }
                val waitToExpire = (RECENT_ORDER_EXPIRY_MS - maxOf(alreadyElapsedMs, readyTargetMs))
                    .coerceAtLeast(0L)
                if (waitToExpire > 0L) delay(waitToExpire.milliseconds)
                orderRepository.removeFromRecentOrders(listOf(orderId)).onSuccess {
                    _ordersMap.value -= orderId
                }
            } finally {
                activeSimulations.remove(orderId)
            }
        }
    }

    private suspend fun sendOrderStatusNotification(message: String) {
        notificationRepository.createNotificationForUser(
            NotificationDataClass(
                message = message,
                isRead = false
            )
        )

    }

    private fun updateLocalOrder(orderId: String, transform: (OrderDataClass) -> OrderDataClass) {
        _ordersMap.value[orderId]?.let { existing ->
            _ordersMap.value += (orderId to transform(existing))
        }
        if (_orderData.value?.id == orderId) {
            _orderData.value = _orderData.value?.let(transform)
        }
    }
    fun loadOrdersByIds(orderIds: List<String>) {
        if (orderIds.isEmpty()) {
            _ordersMap.value = emptyMap()
            return
        }
        viewModelScope.launch {
            _isLoading.value = true
            val now = System.currentTimeMillis()
            val expiredIds = mutableListOf<String>()
            val activeOrders = mutableMapOf<String, OrderDataClass>()

            coroutineScope {
                orderIds.distinct().map { id ->
                    async { id to orderRepository.readOrderData(id).getOrNull() }
                }.awaitAll().forEach { (id, order) ->
                    if (order != null && !order.canceled) {
                        val initiatedMs = order.dateTimeOrderInitiated?.time
                        if (initiatedMs != null && (now - initiatedMs) >= RECENT_ORDER_EXPIRY_MS) {
                            if (order.dateTimeOrderFinished == null) {
                                orderRepository.updateOrderAsFinished(id)
                            }
                            if (order.dateTimeOrderCollected == null) {
                                orderRepository.updateOrderAsCollected(id).onSuccess {
                                    sendOrderStatusNotification("Order #$id has been collected. Enjoy your coffee!")
                                }
                            }
                            expiredIds.add(id)
                        } else {
                            activeOrders[id] = order
                            val elapsedMs = if (initiatedMs != null) (now - initiatedMs).coerceAtLeast(0L) else 0L
                            if (id !in activeSimulations) {
                                simulateOrderLifecycle(id, alreadyElapsedMs = elapsedMs)
                            }
                        }
                    }
                }
            }

            if (expiredIds.isNotEmpty()) {
                orderRepository.removeFromRecentOrders(expiredIds)
            }

            _ordersMap.value = activeOrders
            _isLoading.value = false
        }
    }
    fun loadPastOrders() {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null
            orderRepository.readUsersPastOrders()
                .onSuccess { orders ->
                    _pastOrdersMap.value = orders
                        .sortedByDescending { it.dateTimeOrderInitiated }
                        .associateBy { it.id }
                }
                .onFailure { exception ->
                    if (exception is NoSuchElementException) {
                        _pastOrdersMap.value = emptyMap()
                    } else {
                        _statusMessage.value = exception.message ?: "Failed to load orders"
                    }
                }
            _isLoading.value = false
        }
    }
    fun loadOrder(orderId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _statusMessage.value = null

            orderRepository.readOrderData(orderId)
                .onSuccess { order ->
                    _orderData.value = order
                    loadItemsForOrder(order.itemIdQuantityObject.keys)
                }
                .onFailure { exception ->
                    _statusMessage.value = exception.message ?: "Failed to load order"
                }

            _isLoading.value = false
        }
    }

    private suspend fun loadItemsForOrder(itemIds: Set<String>) = coroutineScope {
        val loadedItems = itemIds.map { id ->
            async {
                itemRepository.readItem(id).getOrNull()?.let { id to it }
            }
        }.awaitAll().filterNotNull().toMap()

        _itemsMap.value = loadedItems
    }

    fun updateRating(orderId: String, rating: Int) {
        if (rating !in 1..5) {
            _statusMessage.value = "Please enter a rating between 1 and 5"
            return
        }

        viewModelScope.launch {
            orderRepository.updateOrderAttribute(orderId, "rating", rating)
                .onSuccess {
                    _orderData.value = _orderData.value?.copy(rating = rating)
                    _statusMessage.value = "Rating submitted!"
                }
                .onFailure { exception ->
                    _statusMessage.value = exception.message ?: "Failed to submit rating"
                }
        }
    }
//    fun clearStatusMessage() { _statusMessage.value = null }


    companion object {
        const val PROCESSING_TO_READY_MS = 15_000L
        const val READY_TO_COLLECTED_MS = 30_000L
        const val RECENT_ORDER_EXPIRY_MS = 5 * 60_000L

        fun provideFactory(
            orderRepository: OrderRepository,
            itemRepository: ItemRepository,
            notificationRepository: NotificationRepository
        ): ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return OrderViewModel(
                    orderRepository,
                    itemRepository,
                    notificationRepository
                ) as T
            }
        }
    }
}