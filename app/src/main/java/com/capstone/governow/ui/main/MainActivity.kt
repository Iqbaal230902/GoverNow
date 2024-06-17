package com.capstone.governow.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.capstone.governow.R
import com.capstone.governow.ui.edit.EditProfileActivity
import com.capstone.governow.ui.form.FormActivity
import com.capstone.governow.ui.login.LoginScreenActivity
import com.capstone.governow.ui.news.NewsActivity
import com.capstone.governow.ui.profileuser.ProfileUserActivity
import com.capstone.governow.ui.welcome.WelcomeActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        startActivity(Intent(this@MainActivity, WelcomeActivity::class.java))


        setContentView(R.layout.activity_main)


        bottomNavigation = findViewById(R.id.bottom_navigation)
        bottomNavigation.setOnNavigationItemReselectedListener { item ->
            when(item.itemId) {
                R.id.home -> {
                    val intent = Intent(this, MainActivity::class.java )
                    startActivity(intent)
                }
                R.id.news -> {
                    // Respond to navigation item 2 reselection
                    val intent = Intent(this, NewsActivity::class.java)
                    startActivity(intent)
                }

                R.id.profile -> {
                    // Respond to navigation item 2 reselection
                    val intent = Intent(this, ProfileUserActivity::class.java )
                    startActivity(intent)
                }

                R.id.form -> {
                    // Respond to navigation item 2 reselection
                    val intent = Intent(this, FormActivity::class.java )
                    startActivity(intent)
                }
            }
        }

    }


}