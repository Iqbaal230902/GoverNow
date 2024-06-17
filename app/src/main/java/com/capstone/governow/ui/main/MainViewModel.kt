package com.capstone.governow.ui.main

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import com.capstone.governow.data.repository.UserRepository
import com.capstone.governow.di.Injection
import com.capstone.governow.data.model.UserModel
import com.google.firebase.firestore.auth.User
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class MainViewModel constructor (private val userRepository: UserRepository) : ViewModel() {
    var scrollPosition = 0
    fun getSession(): Flow<UserModel> {
        return userRepository.getSession()
    }


    val logout = {
        viewModelScope.launch {
            userRepository.logout()
        }
    }
}

class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(Injection.provideRepository(context)) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}