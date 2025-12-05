package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SideMenu(
    isOpen: Boolean,
    onClose: () -> Unit,
    menuItems: List<Pair<String, () -> Unit>>
) {
    if (isOpen) {
        ModalNavigationDrawer(
            drawerState = rememberDrawerState(DrawerValue.Open),
            /*onDismissRequest = onClose,*/
            drawerContent = {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Меню",
                        /*style = MaterialTheme.typography.h5,*/
                        modifier = Modifier.padding(vertical = 16.dp)
                    )

                    menuItems.forEach { (title, onClick) ->
                        Text(
                            text = title,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onClick() }
                                .padding(vertical = 12.dp),
                            fontSize = 18.sp
                        )
                        Divider()
                    }
                }
            }
        ) {}
    }
}