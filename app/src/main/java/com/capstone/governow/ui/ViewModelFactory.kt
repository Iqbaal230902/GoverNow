package com.capstone.governow.ui

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.capstone.governow.data.repository.UserRepository
import com.capstone.governow.di.Injection
import com.capstone.governow.ui.login.LoginScreenViewModel
import com.capstone.governow.ui.main.MainViewModel
import com.capstone.governow.ui.signup.SignUpScreenViewModel

class ViewModelFactory (private val repository: UserRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress ("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T{
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java)-> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginScreenViewModel::class.java) -> {
                LoginScreenViewModel(repository) as T
            }
            modelClass.isAssignableFrom(SignUpScreenViewModel::class.java) -> {
                SignUpScreenViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class : "+modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context) : ViewModelFactory {
            if (INSTANCE == null){
                synchronized(ViewModelFactory::class.java){
                    INSTANCE = ViewModelFactory(Injection.provideRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}