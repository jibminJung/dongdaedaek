package com.jimmy.dongdaedaek.data.preference

import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPreferenceManager(val sharedPreferences: SharedPreferences):PreferenceManager {
    override fun getString(key: String): String? =
        sharedPreferences.getString(key,null)


    override fun putString(key: String, value: String) {
        sharedPreferences.edit {
            putString(key,value)
        }
    }
}