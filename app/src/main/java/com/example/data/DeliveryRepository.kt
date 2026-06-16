package com.example.data

import kotlinx.coroutines.flow.Flow

class DeliveryRepository(private val dao: DeliveryDao) {
    val businessProfile: Flow<BusinessProfile?> = dao.getBusinessProfile()
    val riderProfile: Flow<RiderProfile?> = dao.getRiderProfile()
    val allRiders: Flow<List<RiderProfile>> = dao.getAllRiders()
    val allOrders: Flow<List<DeliveryOrder>> = dao.getAllOrders()
    val availableOrders: Flow<List<DeliveryOrder>> = dao.getAvailableOrders()
    val allRiderAccounts: Flow<List<UserAccount>> = dao.getAllRiderAccounts()

    fun getOrdersByRider(riderId: Int): Flow<List<DeliveryOrder>> = dao.getOrdersByRider(riderId)

    suspend fun getOrderById(orderId: Int): DeliveryOrder? = dao.getOrderById(orderId)

    suspend fun saveBusinessProfile(profile: BusinessProfile) = dao.insertBusinessProfile(profile)

    suspend fun saveRiderProfile(profile: RiderProfile) = dao.insertRiderProfile(profile)

    suspend fun createOrder(order: DeliveryOrder) = dao.insertOrder(order)

    suspend fun assignRider(orderId: Int, riderId: Int, riderName: String, riderPhone: String) {
        dao.assignRiderToOrder(orderId, "ASSIGNED", riderId, riderName, riderPhone)
    }

    suspend fun updateOrderStatus(orderId: Int, status: String) {
        dao.updateOrderStatus(orderId, status)
    }

    suspend fun deleteOrder(orderId: Int) = dao.deleteOrder(orderId)

    // Account methods
    suspend fun registerUserAccount(user: UserAccount): Boolean {
        return try {
            dao.insertUserAccount(user)
            true
        } catch (e: Exception) {
            false
        }
    }

    suspend fun updateUserAccount(user: UserAccount) {
        dao.updateUserAccount(user)
    }

    suspend fun getUserByUsername(username: String): UserAccount? {
        return dao.getUserByUsername(username)
    }
}
