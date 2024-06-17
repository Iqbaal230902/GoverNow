package com.capstone.governow.ui.edit

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import androidx.activity.viewModels
import com.capstone.governow.R
import com.capstone.governow.customview.EmailEditText
import com.capstone.governow.customview.PasswordEditText
import com.capstone.governow.databinding.ActivityEditProfileBinding
import com.capstone.governow.databinding.ActivityLoginScreenBinding
import com.capstone.governow.ui.ViewModelFactory
import com.capstone.governow.ui.login.LoginScreenViewModel
import com.google.android.material.textfield.TextInputEditText

class EditProfileActivity : AppCompatActivity() {
    private val viewModel by viewModels<EditProfileViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityEditProfileBinding
    private lateinit var fullnameEdit: EditText
    private lateinit var usernameEdit: EditText
    private lateinit var emailEdit: TextInputEditText
    private lateinit var token: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_profile)
        binding = ActivityEditProfileBinding.inflate(layoutInflater)

        token = "abc"

        fullnameEdit = findViewById(R.id.et_full_name)
        usernameEdit = findViewById(R.id.et_username)
        emailEdit = findViewById(R.id.et_email)

        viewModel.getSession().observe(this) { user ->
            if (user != null) {
                token = user.token.toString()
                Log.d("hohoxytk", token)

                val profile = viewModel.getProfile(token)
                if(profile != null){
                    fullnameEdit.setText(profile.data.fullName)
                    usernameEdit.setText(profile.data.username)
                    emailEdit.setText(profile.data.email)
                }
            }
        }
    }
}