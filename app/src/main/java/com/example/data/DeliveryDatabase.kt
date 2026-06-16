package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "user_accounts")
data class UserAccount(
    @PrimaryKey val username: String,
    val password: String,
    val role: String, // "BUSINESS" or "RIDER"
    val fullName: String,
    val phone: String,
    
    // Business specific fields
    val city: String = "",
    val address: String = "",
    
    // Rider specific fields
    val vehicleType: String = "", // "Bicycle", "Motorcycle", "Scooter", "Car"
    val vehicleNumber: String = "",
    val isAvailable: Boolean = true
)

@Entity(tableName = "business_profile")
data class BusinessProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String,
    val city: String,
    val address: String
)

@Entity(tableName = "rider_profile")
data class RiderProfile(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val phone: String,
    val vehicleType: String, // "Bicycle", "Motorcycle", "Scooter", "Car"
    val vehicleNumber: String,
    val isAvailable: Boolean = true
)

@Entity(tableName = "delivery_orders")
data class DeliveryOrder(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val businessName: String,
    val businessPhone: String,
    val customerName: String,
    val customerPhone: String,
    val deliveryAddress: String,
    val itemName: String,
    val price: Double,
    val deliveryFee: Double,
    val status: String, // "PENDING", "ASSIGNED", "PICKED_UP", "DELIVERED"
    val riderId: Int? = null,
    val riderName: String? = null,
    val riderPhone: String? = null,
    val createdAt: Long = System.currentTimeMillis()
)

@Dao
interface DeliveryDao {
    // User Account registration and login queries
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertUserAccount(user: UserAccount)

    @Update
    suspend fun updateUserAccount(user: UserAccount)

    @Query("SELECT * FROM user_accounts WHERE username = :username")
    suspend fun getUserByUsername(username: String): UserAccount?

    @Query("SELECT * FROM user_accounts WHERE role = 'RIDER'")
    fun getAllRiderAccounts(): Flow<List<UserAccount>>

    // Business Queries
    @Query("SELECT * FROM business_profile WHERE id = 1")
    fun getBusinessProfile(): Flow<BusinessProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBusinessProfile(profile: BusinessProfile)

    // Rider Queries
    @Query("SELECT * FROM rider_profile WHERE id = 1")
    fun getRiderProfile(): Flow<RiderProfile?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRiderProfile(profile: RiderProfile)

    @Query("SELECT * FROM rider_profile")
    fun getAllRiders(): Flow<List<RiderProfile>>

    // Order Queries
    @Query("SELECT * FROM delivery_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<DeliveryOrder>>

    @Query("SELECT * FROM delivery_orders WHERE status = 'PENDING' ORDER BY createdAt DESC")
    fun getAvailableOrders(): Flow<List<DeliveryOrder>>

    @Query("SELECT * FROM delivery_orders WHERE riderId = :riderId ORDER BY createdAt DESC")
    fun getOrdersByRider(riderId: Int): Flow<List<DeliveryOrder>>

    @Query("SELECT * FROM delivery_orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: Int): DeliveryOrder?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: DeliveryOrder)

    @Query("UPDATE delivery_orders SET status = :status, riderId = :riderId, riderName = :riderName, riderPhone = :riderPhone WHERE id = :orderId")
    suspend fun assignRiderToOrder(orderId: Int, status: String, riderId: Int, riderName: String, riderPhone: String)

    @Query("UPDATE delivery_orders SET status = :status WHERE id = :orderId")
    suspend fun updateOrderStatus(orderId: Int, status: String)

    @Query("DELETE FROM delivery_orders WHERE id = :orderId")
    suspend fun deleteOrder(orderId: Int)
}

@Database(entities = [UserAccount::class, BusinessProfile::class, RiderProfile::class, DeliveryOrder::class], version = 2, exportSchema = false)
abstract class DeliveryDatabase : RoomDatabase() {
    abstract val deliveryDao: DeliveryDao
}
