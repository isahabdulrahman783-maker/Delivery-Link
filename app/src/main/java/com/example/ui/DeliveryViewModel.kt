package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.*
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class DeliveryViewModel(private val repository: DeliveryRepository) : ViewModel() {

    private val _currentUser = MutableStateFlow<UserAccount?>(null)
    val currentUser: StateFlow<UserAccount?> = _currentUser.asStateFlow()

    val businessProfile: StateFlow<BusinessProfile?> = repository.businessProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val riderProfile: StateFlow<RiderProfile?> = repository.riderProfile
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allOrders: StateFlow<List<DeliveryOrder>> = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val availableOrders: StateFlow<List<DeliveryOrder>> = repository.availableOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val allRiders: StateFlow<List<RiderProfile>> = repository.allRiders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    init {
        // Prepopulate with a few available riders and some orders if empty
        viewModelScope.launch {
            // Prepopulate user accounts for immediate login convenience
            val existingMerchant = repository.getUserByUsername("merchant")
            if (existingMerchant == null) {
                repository.registerUserAccount(
                    UserAccount(
                        username = "merchant",
                        password = "password",
                        role = "BUSINESS",
                        fullName = "Flour Garden Bakery",
                        phone = "+1 (555) 777-8899",
                        city = "Springfield",
                        address = "450 Maple Avenue, Downtown"
                    )
                )
            }

            val existingRider = repository.getUserByUsername("rider")
            if (existingRider == null) {
                repository.registerUserAccount(
                    UserAccount(
                        username = "rider",
                        password = "password",
                        role = "RIDER",
                        fullName = "Alex Johnson",
                        phone = "+1 (555) 123-4567",
                        vehicleType = "Motorcycle",
                        vehicleNumber = "MOTO-7749",
                        isAvailable = true
                    )
                )
            }

            repository.allRiders.first().let { riders ->
                if (riders.isEmpty()) {
                    val sampleRiders = listOf(
                        RiderProfile(id = 1, name = "Alex Johnson", phone = "+1 (555) 123-4567", vehicleType = "Motorcycle", vehicleNumber = "MOTO-7749", isAvailable = true),
                        RiderProfile(id = 2, name = "Sara Miller", phone = "+1 (555) 987-6543", vehicleType = "Bicycle", vehicleNumber = "BIKE-2022", isAvailable = true),
                        RiderProfile(id = 3, name = "Carlos Gomez", phone = "+1 (555) 443-1122", vehicleType = "Scooter", vehicleNumber = "SCOOT-091", isAvailable = true)
                    )
                    sampleRiders.forEach { repository.saveRiderProfile(it) }
                }
            }

            repository.allOrders.first().let { orders ->
                if (orders.isEmpty()) {
                    val sampleOrders = listOf(
                        DeliveryOrder(
                            businessName = "Flour Garden Bakery",
                            businessPhone = "+1 (555) 777-8899",
                            customerName = "David Green",
                            customerPhone = "+1 (555) 111-2222",
                            deliveryAddress = "742 Evergreen Terrace, Springfield",
                            itemName = "Box of Cream Donuts & Sourdough",
                            price = 22.50,
                            deliveryFee = 4.50,
                            status = "PENDING"
                        ),
                        DeliveryOrder(
                            businessName = "Gourmet Burger Hub",
                            businessPhone = "+1 (555) 222-3344",
                            customerName = "Jessica Taylor",
                            customerPhone = "+1 (555) 441-2921",
                            deliveryAddress = "104 Baker Street, Westside",
                            itemName = "Double Truffle Burger & Fries Combo",
                            price = 34.00,
                            deliveryFee = 5.90,
                            status = "PENDING"
                        ),
                        DeliveryOrder(
                            businessName = "Flour Garden Bakery",
                            businessPhone = "+1 (555) 777-8899",
                            customerName = "Robert Chen",
                            customerPhone = "+1 (555) 902-1249",
                            deliveryAddress = "12 Ocean Drive, Apt 4B",
                            itemName = "Strawberry Topped Premium Gateau",
                            price = 45.00,
                            deliveryFee = 6.00,
                            status = "ASSIGNED",
                            riderId = 1,
                            riderName = "Alex Johnson",
                            riderPhone = "+1 (555) 123-4567"
                        )
                    )
                    sampleOrders.forEach { repository.createOrder(it) }
                }
            }

            repository.businessProfile.first().let { profile ->
                if (profile == null) {
                    val sampleBusiness = BusinessProfile(
                        id = 1,
                        name = "Flour Garden Bakery",
                        phone = "+1 (555) 777-8899",
                        city = "Springfield",
                        address = "450 Maple Avenue, Downtown"
                    )
                    repository.saveBusinessProfile(sampleBusiness)
                }
            }
        }
    }

    // Auth Actions
    fun register(
        username: String,
        password: String,
        role: String,
        fullName: String,
        phone: String,
        city: String = "",
        address: String = "",
        vehicleType: String = "",
        vehicleNumber: String = "",
        onResult: (Boolean, String) -> Unit
    ) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank() || fullName.isBlank() || phone.isBlank()) {
                onResult(false, "Please fill in all mandatory fields.")
                return@launch
            }
            val existing = repository.getUserByUsername(username)
            if (existing != null) {
                onResult(false, "Username is already taken.")
                return@launch
            }
            val account = UserAccount(
                username = username,
                password = password,
                role = role,
                fullName = fullName,
                phone = phone,
                city = city,
                address = address,
                vehicleType = vehicleType,
                vehicleNumber = vehicleNumber,
                isAvailable = true
            )
            val success = repository.registerUserAccount(account)
            if (success) {
                if (role == "BUSINESS") {
                    repository.saveBusinessProfile(
                        BusinessProfile(
                            id = 1,
                            name = fullName,
                            phone = phone,
                            city = city,
                            address = address
                        )
                    )
                } else if (role == "RIDER") {
                    repository.saveRiderProfile(
                        RiderProfile(
                            id = 1,
                            name = fullName,
                            phone = phone,
                            vehicleType = vehicleType,
                            vehicleNumber = vehicleNumber,
                            isAvailable = true
                        )
                    )
                }
                onResult(true, "Registration successful!")
            } else {
                onResult(false, "Unknown database error.")
            }
        }
    }

    fun login(username: String, password: String, onResult: (Boolean, String) -> Unit) {
        viewModelScope.launch {
            if (username.isBlank() || password.isBlank()) {
                onResult(false, "Please enter both username and password.")
                return@launch
            }
            val existing = repository.getUserByUsername(username)
            if (existing == null) {
                onResult(false, "Account does not exist.")
            } else if (existing.password != password) {
                onResult(false, "Incorrect password.")
            } else {
                _currentUser.value = existing
                // Sync session variables to profiles for legacy screen bindings
                if (existing.role == "BUSINESS") {
                    repository.saveBusinessProfile(
                        BusinessProfile(
                            id = 1,
                            name = existing.fullName,
                            phone = existing.phone,
                            city = existing.city,
                            address = existing.address
                        )
                    )
                } else {
                    repository.saveRiderProfile(
                        RiderProfile(
                            id = 1,
                            name = existing.fullName,
                            phone = existing.phone,
                            vehicleType = existing.vehicleType,
                            vehicleNumber = existing.vehicleNumber,
                            isAvailable = existing.isAvailable
                        )
                    )
                }
                onResult(true, "Welcome back, ${existing.fullName}!")
            }
        }
    }

    fun logout() {
        _currentUser.value = null
    }

    // Business Actions
    fun saveBusinessProfile(name: String, phone: String, city: String, address: String) {
        viewModelScope.launch {
            val bp = BusinessProfile(
                id = 1,
                name = name,
                phone = phone,
                city = city,
                address = address
            )
            repository.saveBusinessProfile(bp)
            // also sync updated details to currentUser account if logged in
            currentUser.value?.let { current ->
                if (current.role == "BUSINESS") {
                    val updatedAccount = current.copy(
                        fullName = name,
                        phone = phone,
                        city = city,
                        address = address
                    )
                    _currentUser.value = updatedAccount
                    repository.updateUserAccount(updatedAccount)
                }
            }
        }
    }

    fun createOrder(customerName: String, customerPhone: String, deliveryAddress: String, itemName: String, price: Double, deliveryFee: Double) {
        viewModelScope.launch {
            val business = repository.businessProfile.first() ?: BusinessProfile(
                name = "My Small Business",
                phone = "+1 (555) 000-0000",
                city = "Local City",
                address = "Local Shop Road"
            )
            val newOrder = DeliveryOrder(
                businessName = business.name,
                businessPhone = business.phone,
                customerName = customerName,
                customerPhone = customerPhone,
                deliveryAddress = deliveryAddress,
                itemName = itemName,
                price = price,
                deliveryFee = deliveryFee,
                status = "PENDING"
            )
            repository.createOrder(newOrder)
        }
    }

    // Rider Actions
    fun saveRiderProfile(name: String, phone: String, vehicleType: String, vehicleNumber: String, isAvailable: Boolean) {
        viewModelScope.launch {
            val rp = RiderProfile(
                id = 1, // Single profile for local demo
                name = name,
                phone = phone,
                vehicleType = vehicleType,
                vehicleNumber = vehicleNumber,
                isAvailable = isAvailable
            )
            repository.saveRiderProfile(rp)
            // also sync updated details to currentUser account if logged in
            currentUser.value?.let { current ->
                if (current.role == "RIDER") {
                    val updatedAccount = current.copy(
                        fullName = name,
                        phone = phone,
                        vehicleType = vehicleType,
                        vehicleNumber = vehicleNumber,
                        isAvailable = isAvailable
                    )
                    _currentUser.value = updatedAccount
                    repository.updateUserAccount(updatedAccount)
                }
            }
        }
    }

    fun acceptOrder(orderId: Int) {
        viewModelScope.launch {
            val currentRider = repository.riderProfile.first() ?: RiderProfile(
                id = 100, // Default virtual rider ID if profile unconfigured
                name = "Alex Johnson",
                phone = "+1 (555) 123-4567",
                vehicleType = "Motorcycle",
                vehicleNumber = "MOTO-7749"
            )
            repository.assignRider(
                orderId = orderId,
                riderId = currentRider.id,
                riderName = currentRider.name,
                riderPhone = currentRider.phone
            )
        }
    }

    fun updateOrderStatus(orderId: Int, status: String) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, status)
        }
    }

    fun deleteOrder(orderId: Int) {
        viewModelScope.launch {
            repository.deleteOrder(orderId)
        }
    }
}
