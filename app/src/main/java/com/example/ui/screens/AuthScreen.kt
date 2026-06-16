package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.DeliveryViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AuthScreen(
    viewModel: DeliveryViewModel,
    modifier: Modifier = Modifier
) {
    var isLoginMode by remember { mutableStateOf(true) }
    
    // Credentials input states
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var selectedRole by remember { mutableStateOf("BUSINESS") } // "BUSINESS" or "RIDER"

    // Business specific variables
    var city by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Rider specific variables
    var vehicleType by remember { mutableStateOf("Motorcycle") } // Bicycle, Motorcycle, Scooter, Car
    val vehiclesList = listOf("Bicycle", "Motorcycle", "Scooter", "Car")
    var vehicleNumber by remember { mutableStateOf("") }

    var passwordVisible by remember { mutableStateOf(false) }
    var alertMessage by remember { mutableStateOf<String?>(null) }
    var isSuccessMessage by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Screen Header Hero
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                colors = listOf(
                                    MaterialTheme.colorScheme.primary,
                                    MaterialTheme.colorScheme.secondary
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.LocalShipping,
                        contentDescription = "App logo symbol",
                        tint = Color.White,
                        modifier = Modifier.size(36.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = "Delivery Link",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Connect Local Shops with Pick Up Riders",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Quick Login Helper card for testing
            item {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "💡 Sandboxed Environment Try-Outs",
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = {
                                    username = "merchant"
                                    password = "password"
                                    isLoginMode = true
                                    viewModel.login("merchant", "password") { success, msg ->
                                        alertMessage = msg
                                        isSuccessMessage = success
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("quick_login_merchant"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.Storefront, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Load Merchant", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }

                            Button(
                                onClick = {
                                    username = "rider"
                                    password = "password"
                                    isLoginMode = true
                                    viewModel.login("rider", "password") { success, msg ->
                                        alertMessage = msg
                                        isSuccessMessage = success
                                    }
                                },
                                modifier = Modifier.weight(1f).testTag("quick_login_rider"),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.secondary
                                ),
                                contentPadding = PaddingValues(horizontal = 4.dp, vertical = 2.dp)
                            ) {
                                Icon(Icons.Default.DirectionsBike, contentDescription = null, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Load Rider", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Auth Selector Bar
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(30.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .padding(4.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                isLoginMode = true
                                alertMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_select_login"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Login",
                            fontWeight = FontWeight.Bold,
                            color = if (isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(30.dp))
                            .background(if (!isLoginMode) MaterialTheme.colorScheme.primary else Color.Transparent)
                            .clickable {
                                isLoginMode = false
                                alertMessage = null
                            }
                            .padding(vertical = 10.dp)
                            .testTag("tab_select_register"),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Sign Up / Register",
                            fontWeight = FontWeight.Bold,
                            color = if (!isLoginMode) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontSize = 14.sp
                        )
                    }
                }
            }

            // Custom dynamic inputs depending on mode
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    if (!isLoginMode) {
                        // Registration parameters
                        Text(
                            text = "Select Account Role",
                            fontWeight = FontWeight.Bold,
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            ElevatedCard(
                                onClick = { selectedRole = "BUSINESS" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_business_role_btn"),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (selectedRole == "BUSINESS") MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Storefront,
                                        contentDescription = "Business Owner",
                                        tint = if (selectedRole == "BUSINESS") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Business Owner",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }

                            ElevatedCard(
                                onClick = { selectedRole = "RIDER" },
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("select_rider_role_btn"),
                                colors = CardDefaults.elevatedCardColors(
                                    containerColor = if (selectedRole == "RIDER") MaterialTheme.colorScheme.secondaryContainer else MaterialTheme.colorScheme.surface
                                )
                            ) {
                                Column(
                                    modifier = Modifier.padding(12.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.DirectionsBike,
                                        contentDescription = "Rider",
                                        tint = if (selectedRole == "RIDER") MaterialTheme.colorScheme.secondary else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        "Delivery Rider",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }

                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        // Full Name
                        OutlinedTextField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = { Text(if (selectedRole == "BUSINESS") "Merchant or Shop Name" else "Rider's Full Name") },
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) },
                            modifier = Modifier.fillMaxWidth().testTag("auth_fullname_input"),
                            singleLine = true
                        )

                        // Contact Phone
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Contact Phone Number") },
                            leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.fillMaxWidth().testTag("auth_phone_input"),
                            singleLine = true
                        )

                        // Role specific elements
                        AnimatedVisibility(visible = selectedRole == "BUSINESS") {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedTextField(
                                    value = city,
                                    onValueChange = { city = it },
                                    label = { Text("City Location") },
                                    leadingIcon = { Icon(Icons.Default.LocationCity, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_city_input"),
                                    singleLine = true
                                )
                                OutlinedTextField(
                                    value = address,
                                    onValueChange = { address = it },
                                    label = { Text("Shop Street Address") },
                                    leadingIcon = { Icon(Icons.Default.Place, contentDescription = null) },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_address_input"),
                                    singleLine = true
                                )
                            }
                        }

                        AnimatedVisibility(visible = selectedRole == "RIDER") {
                            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                                Text(
                                    text = "Vehicle Specifications",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp,
                                    color = MaterialTheme.colorScheme.secondary
                                )
                                // Vehicle selection row
                                FlowRow(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    vehiclesList.forEach { vType ->
                                        FilterChip(
                                            selected = vehicleType == vType,
                                            onClick = { vehicleType = vType },
                                            label = { Text(vType, fontSize = 11.sp) },
                                            modifier = Modifier.testTag("vehicle_chip_$vType")
                                        )
                                    }
                                }

                                OutlinedTextField(
                                    value = vehicleNumber,
                                    onValueChange = { vehicleNumber = it },
                                    label = { Text("Vehicle Identification Plate") },
                                    leadingIcon = { Icon(Icons.Default.CreditCard, contentDescription = null) },
                                    placeholder = { Text("e.g. MOTO-X091, BIKE-44") },
                                    modifier = Modifier.fillMaxWidth().testTag("auth_vehicle_no_input"),
                                    singleLine = true
                                )
                            }
                        }
                    }

                    // Account Username
                    OutlinedTextField(
                        value = username,
                        onValueChange = { username = it },
                        label = { Text("Account Username") },
                        leadingIcon = { Icon(Icons.Default.AccountCircle, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_username_input"),
                        singleLine = true
                    )

                    // Account Password
                    OutlinedTextField(
                        value = password,
                        onValueChange = { password = it },
                        label = { Text("Account Password") },
                        leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
                        modifier = Modifier.fillMaxWidth().testTag("auth_password_input"),
                        visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                        trailingIcon = {
                            IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                Icon(
                                    imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                    contentDescription = "Toggle password visibility"
                                )
                            }
                        },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                        singleLine = true
                    )
                }
            }

            // Display alert feedbacks clearly
            if (alertMessage != null) {
                item {
                    Surface(
                        color = if (isSuccessMessage) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.errorContainer,
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = alertMessage!!,
                            color = if (isSuccessMessage) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                }
            }

            // Perform Button
            item {
                Spacer(modifier = Modifier.height(10.dp))
                Button(
                    onClick = {
                        alertMessage = null
                        if (isLoginMode) {
                            viewModel.login(username, password) { success, msg ->
                                alertMessage = msg
                                isSuccessMessage = success
                            }
                        } else {
                            viewModel.register(
                                username = username,
                                password = password,
                                role = selectedRole,
                                fullName = fullName,
                                phone = phone,
                                city = city,
                                address = address,
                                vehicleType = vehicleType,
                                vehicleNumber = vehicleNumber
                            ) { success, msg ->
                                alertMessage = msg
                                isSuccessMessage = success
                                if (success) {
                                    // Switch to login instantly
                                    isLoginMode = true
                                    password = ""
                                }
                            }
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(52.dp)
                        .testTag("auth_submit_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(
                        text = if (isLoginMode) "Log In Securely" else "Register and Join Platform",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}
