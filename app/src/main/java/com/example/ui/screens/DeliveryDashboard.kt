package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.BusinessProfile
import com.example.data.DeliveryOrder
import com.example.data.RiderProfile
import com.example.ui.DeliveryViewModel

enum class UserRole {
    WELCOME,
    BUSINESS,
    RIDER
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeliveryDashboard(
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    val currentUser by viewModel.currentUser.collectAsStateWithLifecycle()
    val businessProfile by viewModel.businessProfile.collectAsStateWithLifecycle()
    val riderProfile by viewModel.riderProfile.collectAsStateWithLifecycle()
    val allOrders by viewModel.allOrders.collectAsStateWithLifecycle()
    val availableOrders by viewModel.availableOrders.collectAsStateWithLifecycle()
    val allRiders by viewModel.allRiders.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.LocalShipping,
                            contentDescription = "Delivery App Icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text(
                            text = "Delivery Link",
                            fontWeight = FontWeight.Bold,
                            fontFamily = androidx.compose.ui.text.font.FontFamily.SansSerif
                        )
                    }
                },
                actions = {
                    if (currentUser != null) {
                        IconButton(
                            onClick = { viewModel.logout() },
                            modifier = Modifier.testTag("logout_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Log out",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            color = MaterialTheme.colorScheme.background
        ) {
            AnimatedContent(
                targetState = currentUser,
                transitionSpec = {
                    fadeIn() togetherWith fadeOut()
                },
                label = "RoleTransition"
            ) { activeUser ->
                if (activeUser == null) {
                    AuthScreen(viewModel = viewModel)
                } else if (activeUser.role == "BUSINESS") {
                    BusinessDashboard(
                        viewModel = viewModel,
                        businessProfile = businessProfile,
                        allOrders = allOrders,
                        allRiders = allRiders
                    )
                } else {
                    RiderDashboard(
                        viewModel = viewModel,
                        riderProfile = riderProfile,
                        availableOrders = availableOrders,
                        allOrders = allOrders
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeScreen(
    businessProfile: BusinessProfile?,
    riderProfile: RiderProfile?,
    onRoleSelect: (UserRole) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.img_delivery_banner_1781618845906),
                    contentDescription = "Rider delivering package",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Add a beautiful dark gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.5f))
                            )
                        )
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    contentAlignment = Alignment.BottomStart
                ) {
                    Text(
                        text = "Delivery Link Hub",
                        style = MaterialTheme.typography.titleLarge,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Pick Your Dashboard",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = (-0.5).sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    text = "Seamless collaboration between local vendors and trusted riders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRoleSelect(UserRole.BUSINESS) }
                    .testTag("welcome_business_card")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = "Store icon",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Small Business Owner",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (businessProfile != null) "Manage '${businessProfile.name}' & orders" else "Post delivery requests, manage catalog, track deliveries",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onRoleSelect(UserRole.RIDER) }
                    .testTag("welcome_rider_card")
            ) {
                Row(
                    modifier = Modifier.padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.secondary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.DirectionsBike,
                            contentDescription = "Rider icon",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Professional Rider",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (riderProfile != null) "Manage deliveries as '${riderProfile.name}'" else "Browse available delivery jobs, accept pick-ups, update transit details",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Navigate",
                        tint = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)
                ),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "Information icon",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Real-time Interaction Simulator",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = "Because this runs in a sandbox env, both dashboards are linked to the same local Room Database. You can create a request as a vendor, switch to Rider Mode, accept it, mark it picked up/delivered, and switch back to watch the order progress instantly!",
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

// ==================== BUSINESS REGION ====================

@Composable
fun BusinessDashboard(
    viewModel: DeliveryViewModel,
    businessProfile: BusinessProfile?,
    allOrders: List<DeliveryOrder>,
    allRiders: List<RiderProfile>
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("My Orders", "Post Job", "Active Riders", "My Shop")

    Column(modifier = Modifier.fillMaxSize()) {
        // Business top micro header
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = businessProfile?.name ?: "Configure Your Shop",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = businessProfile?.address ?: "Click 'My Shop' tab below to set up profile",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.widthIn(max = 240.dp)
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "VENDORS",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("business_tab_$index")
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
            ) {
            when (selectedTab) {
                0 -> BusinessOrdersTab(
                    allOrders = allOrders,
                    onDeleteOrder = { viewModel.deleteOrder(it) }
                )
                1 -> BusinessCreateOrderTab(
                    businessProfile = businessProfile,
                    onCreateOrder = { customerName, customerPhone, deliveryAddress, itemName, price, deliveryFee ->
                        viewModel.createOrder(customerName, customerPhone, deliveryAddress, itemName, price, deliveryFee)
                        selectedTab = 0 // Switch to orders list
                    }
                )
                2 -> BusinessRidersPoolTab(allRiders = allRiders)
                3 -> BusinessProfileTab(
                    businessProfile = businessProfile,
                    onSaveProfile = { name, phone, city, address ->
                        viewModel.saveBusinessProfile(name, phone, city, address)
                    }
                )
            }
        }
    }
}

@Composable
fun BusinessOrdersTab(
    allOrders: List<DeliveryOrder>,
    onDeleteOrder: (Int) -> Unit
) {
    if (allOrders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Inbox,
                contentDescription = "Empty orders",
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Orders Posted Yet",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Navigate to 'Post Job' tab to publish a delivery request for nearby riders.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(allOrders, key = { it.id }) { order ->
                OrderCard(order = order, isRiderSide = false, onAction = { onDeleteOrder(order.id) })
            }
        }
    }
}

@Composable
fun BusinessCreateOrderTab(
    businessProfile: BusinessProfile?,
    onCreateOrder: (String, String, String, String, Double, Double) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var customerPhone by remember { mutableStateOf("") }
    var deliveryAddress by remember { mutableStateOf("") }
    var itemName by remember { mutableStateOf("") }
    var itemPrice by remember { mutableStateOf("") }
    var deliveryFee by remember { mutableStateOf("4.50") }

    var showError by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (businessProfile == null) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Warning",
                            tint = MaterialTheme.colorScheme.onErrorContainer
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                            text = "Please set up your Shop Details in the 'My Shop' tab first to post delivery requests.",
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }

        item {
            Text(
                text = "Create Delivery Job",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            // Quick-template selectors
            Text(
                text = "Load Simulation Templates",
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ElevatedButton(
                    onClick = {
                        customerName = "Clara Adams"
                        customerPhone = "+1 (555) 700-1122"
                        deliveryAddress = "990 Hillview Dr, Highhills"
                        itemName = "Artisan Sourdough & 4 Croissants"
                        itemPrice = "18.50"
                        deliveryFee = "3.80"
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("template_bakery_button")
                ) {
                    Text("🥐 Bakery Order", fontSize = 11.sp)
                }
                ElevatedButton(
                    onClick = {
                        customerName = "Marcus Vance"
                        customerPhone = "+1 (555) 601-5050"
                        deliveryAddress = "332 Pine Boulevard, East End"
                        itemName = "Mega Steak Burger Combo (Lg)"
                        itemPrice = "26.00"
                        deliveryFee = "5.50"
                    },
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                    modifier = Modifier.testTag("template_burger_button")
                ) {
                    Text("🍔 Burger FastFood", fontSize = 11.sp)
                }
            }
        }

        item {
            OutlinedTextField(
                value = itemName,
                onValueChange = { itemName = it; showError = false },
                label = { Text("What are you delivering? (e.g. 2 Box Pizzas)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_item_name"),
                leadingIcon = { Icon(imageVector = Icons.Default.ShoppingBag, contentDescription = null) },
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = deliveryAddress,
                onValueChange = { deliveryAddress = it; showError = false },
                label = { Text("Destination Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_delivery_address"),
                leadingIcon = { Icon(imageVector = Icons.Default.Place, contentDescription = null) },
                singleLine = true
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it; showError = false },
                    label = { Text("Customer Name") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_customer_name"),
                    leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = null) },
                    singleLine = true
                )
                OutlinedTextField(
                    value = customerPhone,
                    onValueChange = { customerPhone = it; showError = false },
                    label = { Text("Customer Phone") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_customer_phone"),
                    leadingIcon = { Icon(imageVector = Icons.Default.Phone, contentDescription = null) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    singleLine = true
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = itemPrice,
                    onValueChange = { itemPrice = it; showError = false },
                    label = { Text("Order Value ($)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_item_price"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
                OutlinedTextField(
                    value = deliveryFee,
                    onValueChange = { deliveryFee = it; showError = false },
                    label = { Text("Delivery Bounty ($)") },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("input_delivery_fee"),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    singleLine = true
                )
            }
        }

        if (showError) {
            item {
                Text(
                    text = "Please fill in all details before posting.",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Button(
                onClick = {
                    val priceVal = itemPrice.toDoubleOrNull() ?: 0.0
                    val feeVal = deliveryFee.toDoubleOrNull() ?: 3.50
                    if (customerName.isNotBlank() && deliveryAddress.isNotBlank() && itemName.isNotBlank()) {
                        onCreateOrder(customerName, customerPhone, deliveryAddress, itemName, priceVal, feeVal)
                        // reset states
                        customerName = ""
                        customerPhone = ""
                        deliveryAddress = ""
                        itemName = ""
                        itemPrice = ""
                        deliveryFee = "4.50"
                        showError = false
                    } else {
                        showError = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("post_job_button"),
                enabled = businessProfile != null
            ) {
                Icon(imageVector = Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Confirm and Post Delivery Job", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun BusinessRidersPoolTab(allRiders: List<RiderProfile>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "Active Riders",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Riders currently online who can take up your delivery requests.",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (allRiders.isEmpty()) {
            Box(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text("No riders registered yet. Switch to Rider Mode to register a Rider Profile.")
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(allRiders) { rider ->
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("rider_pool_profile_${rider.id}")
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(46.dp)
                                    .clip(CircleShape)
                                    .background(MaterialTheme.colorScheme.secondary.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                            ) {
                                val vector = when (rider.vehicleType) {
                                    "Bicycle" -> Icons.Default.DirectionsBike
                                    "Motorcycle" -> Icons.Default.TwoWheeler
                                    "Scooter" -> Icons.Default.ElectricScooter
                                    else -> Icons.Default.DirectionsCar
                                }
                                Icon(
                                    imageVector = vector,
                                    contentDescription = rider.vehicleType,
                                    tint = MaterialTheme.colorScheme.secondary
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = rider.name,
                                    fontWeight = FontWeight.Bold,
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Spacer(modifier = Modifier.height(2.dp))
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    Text(
                                        text = "${rider.vehicleType} • ${rider.vehicleNumber}",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Text(
                                        text = rider.phone,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                            Surface(
                                shape = RoundedCornerShape(100.dp),
                                color = if (rider.isAvailable) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = if (rider.isAvailable) "ONLINE" else "OFFLINE",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (rider.isAvailable) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BusinessProfileTab(
    businessProfile: BusinessProfile?,
    onSaveProfile: (String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf(businessProfile?.name ?: "") }
    var phone by remember { mutableStateOf(businessProfile?.phone ?: "") }
    var city by remember { mutableStateOf(businessProfile?.city ?: "") }
    var address by remember { mutableStateOf(businessProfile?.address ?: "") }
    var showMessage by remember { mutableStateOf(false) }

    // Synchronize states if profile loads asynchronously
    LaunchedEffect(businessProfile) {
        if (businessProfile != null) {
            name = businessProfile.name
            phone = businessProfile.phone
            city = businessProfile.city
            address = businessProfile.address
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Small Business Shop Profile",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "This details will be automatically attached to delivery requests you publish so riders know where to pick items up.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; showMessage = false },
                label = { Text("Shop or Company Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_business_name"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; showMessage = false },
                label = { Text("Merchant Contact Phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_business_phone"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = city,
                onValueChange = { city = it; showMessage = false },
                label = { Text("City") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_business_city"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = address,
                onValueChange = { address = it; showMessage = false },
                label = { Text("Pick-up Street Address") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_business_address"),
                singleLine = true
            )
        }

        if (showMessage) {
            item {
                Text(
                    text = "Profile Saved Successfully!",
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSaveProfile(name, phone, city, address)
                        showMessage = true
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_business_profile_button")
            ) {
                Text("Save Profile Records", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// ==================== RIDERS REGION ====================

@Composable
fun RiderDashboard(
    viewModel: DeliveryViewModel,
    riderProfile: RiderProfile?,
    availableOrders: List<DeliveryOrder>,
    allOrders: List<DeliveryOrder>
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Open Jobs", "My Assignments", "Duty Settings")

    Column(modifier = Modifier.fillMaxSize()) {
        // Rider top micro header
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.secondary.copy(alpha = 0.08f)
            ),
            shape = RoundedCornerShape(0.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = riderProfile?.name ?: "Configure Rider Account",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.secondary
                    )
                    Text(
                        text = riderProfile?.let { "${it.vehicleType} (${it.vehicleNumber})" } ?: "Click 'Duty Settings' to set up vehicle",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    modifier = Modifier.padding(start = 4.dp)
                ) {
                    Text(
                        text = "RIDER MODE",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        TabRow(selectedTabIndex = selectedTab) {
            tabs.forEachIndexed { index, title ->
                Tab(
                    selected = selectedTab == index,
                    onClick = { selectedTab = index },
                    modifier = Modifier.testTag("rider_tab_$index")
                ) {
                    Text(
                        text = title,
                        modifier = Modifier.padding(vertical = 12.dp),
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                    )
                }
            }
        }

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (selectedTab) {
                0 -> RiderOpenJobsTab(
                    availableOrders = availableOrders,
                    riderProfile = riderProfile,
                    onAcceptJob = { orderId ->
                        viewModel.acceptOrder(orderId)
                        selectedTab = 1 // Switch to active assignments
                    }
                )
                1 -> RiderAssignmentsTab(
                    allOrders = allOrders,
                    riderProfile = riderProfile,
                    onUpdateStatus = { orderId, newStatus ->
                        viewModel.updateOrderStatus(orderId, newStatus)
                    }
                )
                2 -> RiderProfileTab(
                    riderProfile = riderProfile,
                    onSaveProfile = { name, phone, vehicle, plate, active ->
                        viewModel.saveRiderProfile(name, phone, vehicle, plate, active)
                    }
                )
            }
        }
    }
}

@Composable
fun RiderOpenJobsTab(
    availableOrders: List<DeliveryOrder>,
    riderProfile: RiderProfile?,
    onAcceptJob: (Int) -> Unit
) {
    if (riderProfile == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Warning,
                contentDescription = "Warning",
                tint = MaterialTheme.colorScheme.secondary,
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Rider Profile Configured",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Please go to 'Duty Settings' tab to configure your Rider name and vehicle so you can accept orders.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    } else if (availableOrders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.OfflineBolt,
                contentDescription = "No delivery jobs",
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Open Jobs Right Now",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Small businesses are currently preparing deliveries. Switch to Business Owner Mode, post a template job quickly, and come back here to instantly claim that parcel!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = "Open Orders List (${availableOrders.size})",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(6.dp)
            )
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(availableOrders, key = { it.id }) { order ->
                    OrderCard(order = order, isRiderSide = true, onAction = { onAcceptJob(order.id) })
                }
            }
        }
    }
}

@Composable
fun RiderAssignmentsTab(
    allOrders: List<DeliveryOrder>,
    riderProfile: RiderProfile?,
    onUpdateStatus: (Int, String) -> Unit
) {
    if (riderProfile == null) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            contentAlignment = Alignment.Center
        ) {
            Text("Please set up profile in the 'Duty Settings' tab.", textAlign = TextAlign.Center)
        }
        return
    }

    val myAssignedOrders = allOrders.filter { it.riderId == riderProfile.id }

    if (myAssignedOrders.isEmpty()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.Task,
                contentDescription = "Unassigned",
                tint = MaterialTheme.colorScheme.secondary.copy(alpha = 0.5f),
                modifier = Modifier.size(64.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "No Active Assignments",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "Browse the 'Open Jobs' tab to accept a delivery assignment.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp)
            )
        }
    } else {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(myAssignedOrders, key = { it.id }) { order ->
                OrderCard(
                    order = order,
                    isRiderSide = true,
                    onAction = {
                        val nextStatus = when (order.status) {
                            "ASSIGNED" -> "PICKED_UP"
                            "PICKED_UP" -> "DELIVERED"
                            else -> ""
                        }
                        if (nextStatus.isNotEmpty()) {
                            onUpdateStatus(order.id, nextStatus)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun RiderProfileTab(
    riderProfile: RiderProfile?,
    onSaveProfile: (String, String, String, String, Boolean) -> Unit
) {
    var name by remember { mutableStateOf(riderProfile?.name ?: "") }
    var phone by remember { mutableStateOf(riderProfile?.phone ?: "") }
    var vehicleNumber by remember { mutableStateOf(riderProfile?.vehicleNumber ?: "") }
    var vehicleType by remember { mutableStateOf(riderProfile?.vehicleType ?: "Motorcycle") }
    var isAvailable by remember { mutableStateOf(riderProfile?.isAvailable ?: true) }
    var showMessage by remember { mutableStateOf(false) }

    val vehicleOptions = listOf("Bicycle", "Motorcycle", "Scooter", "Car")

    LaunchedEffect(riderProfile) {
        if (riderProfile != null) {
            name = riderProfile.name
            phone = riderProfile.phone
            vehicleNumber = riderProfile.vehicleNumber
            vehicleType = riderProfile.vehicleType
            isAvailable = riderProfile.isAvailable
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Rider Profile Details",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Enter your vehicle type, contact phone and registration number so business merchants seek you out.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it; showMessage = false },
                label = { Text("Rider Full Name") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_rider_name"),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it; showMessage = false },
                label = { Text("Contact Phone") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_rider_phone"),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true
            )
        }

        item {
            OutlinedTextField(
                value = vehicleNumber,
                onValueChange = { vehicleNumber = it; showMessage = false },
                label = { Text("Vehicle License/Registration #") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("profile_rider_vehicle_number"),
                singleLine = true
            )
        }

        item {
            Text(
                text = "Select Transit Vehicle",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Bold
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                vehicleOptions.forEach { type ->
                    FilterChip(
                        selected = vehicleType == type,
                        onClick = { vehicleType = type; showMessage = false },
                        label = { Text(type) },
                        modifier = Modifier.testTag("rider_vehicle_option_$type")
                    )
                }
            }
        }

        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(8.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                    .padding(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(text = "Online Duty Status", fontWeight = FontWeight.Bold)
                    Text(text = "Receive jobs as online rider", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = isAvailable,
                    onCheckedChange = { isAvailable = it; showMessage = false },
                    modifier = Modifier.testTag("rider_availability_switch")
                )
            }
        }

        if (showMessage) {
            item {
                Text(
                    text = "Rider Profile Saved Successfully!",
                    color = MaterialTheme.colorScheme.secondary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        item {
            Button(
                onClick = {
                    if (name.isNotBlank() && phone.isNotBlank()) {
                        onSaveProfile(name, phone, vehicleType, vehicleNumber, isAvailable)
                        showMessage = true
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("save_rider_profile_button")
            ) {
                Text("Save Active Profile", fontWeight = FontWeight.Bold, color = Color.White)
            }
        }
    }
}

// ==================== SHARED ORDER CARD ====================

@Composable
fun OrderCard(
    order: DeliveryOrder,
    isRiderSide: Boolean,
    onAction: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier
            .fillMaxWidth()
            .testTag("order_card_${order.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Header Row: Status & Order Id
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Order #${1000 + order.id}",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Bold
                )
                
                StatusBadge(status = order.status)
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Body Item Name
            Text(
                text = order.itemName,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.ExtraBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Dynamic Addresses (Pickup / Dropoff details)
            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Place,
                    contentDescription = "Pin Vector",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "PICK-UP:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        text = "${order.businessName} — ${order.businessPhone}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = order.deliveryAddress.substringBefore(","), // Pick Address info as simulation
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                Icon(
                    imageVector = Icons.Default.Navigation,
                    contentDescription = "Dropoff Vector",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Column {
                    Text(
                        text = "DROP-OFF DESTINATION:",
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                    Text(
                        text = "Customer: ${order.customerName} (${order.customerPhone})",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = order.deliveryAddress,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier.padding(vertical = 10.dp),
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)
            )

            // Prices and active Riders info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Item Value: $${String.format("%.2f", order.price)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "Delivery Fee: $${String.format("%.2f", order.deliveryFee)}",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                if (order.riderName != null) {
                    Column(
                        horizontalAlignment = Alignment.End,
                        modifier = Modifier.padding(start = 8.dp)
                    ) {
                        Text(
                            text = "Assigned Rider:",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Text(
                            text = order.riderName,
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = order.riderPhone ?: "",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // CTAs depending on Mode
            if (isRiderSide) {
                when (order.status) {
                    "PENDING" -> {
                        Button(
                            onClick = onAction,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .testTag("claim_order_button_${order.id}")
                        ) {
                            Icon(imageVector = Icons.Default.Check, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Claim Delivery Job", fontWeight = FontWeight.Bold)
                        }
                    }
                    "ASSIGNED" -> {
                        Button(
                            onClick = onAction,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .testTag("picked_up_order_button_${order.id}")
                        ) {
                            Icon(imageVector = Icons.Default.DirectionsBike, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Picked Up", fontWeight = FontWeight.Bold)
                        }
                    }
                    "PICKED_UP" -> {
                        Button(
                            onClick = onAction,
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.tertiary),
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 12.dp)
                                .testTag("delivered_order_button_${order.id}")
                        ) {
                            Icon(imageVector = Icons.Default.DoneAll, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Mark as Delivered", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                // Business owner delete order if still pending
                if (order.status == "PENDING") {
                    TextButton(
                        onClick = onAction,
                        colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 8.dp)
                            .testTag("delete_order_button_${order.id}")
                    ) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete Job", modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Delete Request", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

@Composable
fun StatusBadge(status: String) {
    val containerColor = when (status) {
        "PENDING" -> MaterialTheme.colorScheme.errorContainer
        "ASSIGNED" -> MaterialTheme.colorScheme.primaryContainer
        "PICKED_UP" -> MaterialTheme.colorScheme.tertiaryContainer
        "DELIVERED" -> Color(0xFFC8E6C9) // Clean Material Green
        else -> MaterialTheme.colorScheme.surfaceVariant
    }

    val contentColor = when (status) {
        "PENDING" -> MaterialTheme.colorScheme.onErrorContainer
        "ASSIGNED" -> MaterialTheme.colorScheme.onPrimaryContainer
        "PICKED_UP" -> MaterialTheme.colorScheme.onTertiaryContainer
        "DELIVERED" -> Color(0xFF1B5E20) // Deep dark green
        else -> MaterialTheme.colorScheme.onSurface
    }

    val text = when (status) {
        "PENDING" -> "OPEN JOB"
        "ASSIGNED" -> "ASSIGNED"
        "PICKED_UP" -> "IN TRANSIT"
        "DELIVERED" -> "DELIVERED"
        else -> status
    }

    Surface(
        shape = RoundedCornerShape(100.dp),
        color = containerColor
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val icon = when (status) {
                "PENDING" -> Icons.Default.FiberNew
                "ASSIGNED" -> Icons.Default.HourglassEmpty
                "PICKED_UP" -> Icons.Default.DirectionsBike
                "DELIVERED" -> Icons.Default.CheckCircle
                else -> Icons.Default.Info
            }
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = contentColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = text,
                fontSize = 11.sp,
                fontWeight = FontWeight.ExtraBold,
                color = contentColor
            )
        }
    }
}
