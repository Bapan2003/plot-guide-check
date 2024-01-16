package com.example.plotguide.activity

import android.content.res.ColorStateList
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.ContextCompat
import androidx.navigation.Navigation
import androidx.navigation.ui.NavigationUI
import com.example.plotguide.R
import com.example.plotguide.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var mainBinding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mainBinding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(mainBinding.root)
         val  btmNavigation=mainBinding.btmNav
        val navController=Navigation.findNavController(this,R.id.navHost)
        NavigationUI.setupWithNavController(btmNavigation,navController)

        val bottomNavSelectorStates = arrayOf(
            intArrayOf(android.R.attr.state_selected),//selected
            intArrayOf(-android.R.attr.state_selected),//default/un-selected
        )


        val colors = intArrayOf(
            ContextCompat.getColor(this,R.color.black), ContextCompat.getColor(this, R.color.iconColor)
        )


        btmNavigation.itemIconTintList =
            ColorStateList(bottomNavSelectorStates, colors)
        btmNavigation.itemTextColor =
            ColorStateList(bottomNavSelectorStates, colors)

    }
}