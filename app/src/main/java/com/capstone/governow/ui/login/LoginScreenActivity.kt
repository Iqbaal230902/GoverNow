package com.capstone.governow.ui.login

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Button
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.paging.ExperimentalPagingApi
import com.capstone.governow.R
import com.capstone.governow.customview.EmailEditText
import com.capstone.governow.customview.PasswordEditText
import com.capstone.governow.databinding.ActivityLoginScreenBinding
import com.capstone.governow.model.UserModel
import com.capstone.governow.ui.ViewModelFactory
import com.capstone.governow.ui.main.MainActivity


class LoginScreenActivity : AppCompatActivity() {
    private val viewModel by viewModels<LoginScreenViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityLoginScreenBinding
    private lateinit var emailEdit: EmailEditText
    private lateinit var passwordEdit: PasswordEditText
    private lateinit var buttonSubmit: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        emailEdit = findViewById(R.id.emailEditTextLayout)
        passwordEdit = findViewById(R.id.passwordEditTextLayout)

        setupView()
        setupAction(this)
    }

    private fun setupView() {
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        supportActionBar?.hide()
    }

    @OptIn(ExperimentalPagingApi::class)
    private fun setupAction(context: Context) {
        binding.loginButton.setOnClickListener {
            buttonSubmit = findViewById(R.id.loginButton)
            buttonSubmit.isEnabled = false
            val emailLogin = emailEdit.emailVal
            val passwordLogin = passwordEdit.passwordVal


            val login = viewModel.loginUser(emailLogin, passwordLogin)
            Log.d("hoho",login.toString())
            val token = login?.token
            Log.d("uhuyy", token.toString())

            if(login != null || token != null){
                val user = (viewModel.getProfile(token.toString()))?.data
                val fullName  = user?.fullName
                val username = user?.username
                val email = user?.email

                viewModel.saveSession(UserModel(fullName, email, username, token))

                Log.d("uhuyy2", "hihi")
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }else{
                AlertDialog.Builder(this).apply {
                    setTitle("Sorry!")
                    setMessage("Login failed. Please check your email and password and try again.")
                    setPositiveButton("Try Again") { _, _ ->}
                    create()
                    show()
                }
                buttonSubmit.isEnabled = true
            }

        }
    }
}

