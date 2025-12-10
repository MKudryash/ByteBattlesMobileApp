package com.example.bytebattlesmobileapp.presentation.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.bytebattlesmobileapp.R

@Composable
fun SideMenu(
    icon: Painter,
    nameMenu:String,
    action:()->Unit,


) {
    Row(
        Modifier.fillMaxWidth().clickable(onClick = {action()}),
        verticalAlignment = Alignment.CenterVertically
    ){
        Image(
            modifier = Modifier.weight(0.2f),
            painter = icon,
            contentDescription = "IconMenu")
        Text(
            modifier = Modifier.weight(0.7f),
            text =  nameMenu,
            color = Color.White,
            fontWeight = FontWeight.Normal,
            fontSize = 16.sp,
            lineHeight = 20.sp,
            fontFamily = FontFamily(Font(R.font.ibmplexmono_medium)))
        Image(
            modifier = Modifier.weight(0.1f),
            painter = painterResource(R.drawable.arrow_menu_left),
           contentDescription =  "IconMenu")
    }
}
@Preview
@Composable
fun SideMenuPreview(){
    SideMenu(painterResource(R.drawable.menu_profile),"Профиль",{})
}