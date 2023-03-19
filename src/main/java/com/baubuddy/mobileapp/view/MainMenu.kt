package com.baubuddy.mobileapp.view

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

class MainMenu {

    val myItems: List<String> = listOf("dsds",  "dsd", "dsds")
    @Composable
    fun showListItems(){

        val token: String = ""
        Column(modifier = Modifier.fillMaxSize()) {
            LazyColumn {
                items(myItems) { index ->
                    Text(text = "Item: $index")
                }


            }
            Text(text = "This: $token")

        }
    }


}