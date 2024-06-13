package com.capstone.governow.di

import android.content.Context
import com.capstone.governow.api.ApiConfig
import com.capstone.governow.data.pref.UserPreference
import com.capstone.governow.data.pref.dataStore
import com.capstone.governow.data.repository.UserRepository

object Injection {
    fun provideRepository(context: Context) : UserRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }
}