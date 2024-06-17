package com.capstone.governow.ui.profileuser

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.capstone.governow.databinding.ActivityProfileUserBinding
import com.capstone.governow.ui.edit.EditProfileActivity

class ProfileUserActivity : AppCompatActivity() {

    private lateinit var binding: ActivityProfileUserBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lateinit var binding: ActivityProfileUserBinding

        setSupportActionBar(binding.ToolbarMainProfile)

        binding.ToolbarMainProfile.setOnClickListener {
            navigateToEditProfile()
        }
    }

    private fun navigateToEditProfile() {
        val intent = Intent(this, EditProfileActivity::class.java)
        startActivity(intent)
    }
}
