package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.room.Room
import com.example.data.DeliveryDatabase
import com.example.data.DeliveryRepository
import com.example.ui.DeliveryViewModel
import com.example.ui.DeliveryViewModelFactory
import com.example.ui.screens.DeliveryDashboard
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    val db = Room.databaseBuilder(
      applicationContext,
      DeliveryDatabase::class.java,
      "delivery_links_db"
    ).fallbackToDestructiveMigration()
      .build()

    val repository = DeliveryRepository(db.deliveryDao)
    val factory = DeliveryViewModelFactory(repository)
    val viewModel = ViewModelProvider(this, factory)[DeliveryViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
          DeliveryDashboard(
            viewModel = viewModel,
            modifier = Modifier.padding(innerPadding)
          )
        }
      }
    }
  }
}

