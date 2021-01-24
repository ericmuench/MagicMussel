package com.emuench.magischemiesmuschel.activities

import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import com.emuench.magischemiesmuschel.R
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

open class BaseActivity : AppCompatActivity() {

    //Fields
    private val gson : Gson = Gson()

    //functions
     fun <T> loadDataFromSharedPreferences(prefs: SharedPreferences, searchKey: String) : T? {
        val json = prefs.getString(searchKey,"")

        if(json == null || json.isEmpty()){
            return null
        }
        else{

            return gson.fromJson(json,object: TypeToken<T>(){}.type)
        }
     }

     fun <T> saveDataToSharedPreferences(prefs: SharedPreferences, data: T, key: String){

         val editor = prefs.edit()
         val json = gson.toJson(data)
         editor.putString(key,json)
         editor.apply()

     }

     fun languageChanged(prefs : SharedPreferences) : Boolean{
        val lastLanguage : String? = prefs.getString(resources.getString(R.string.shared_preferences_settings_key_last_language),"")

        if(lastLanguage == null || lastLanguage.isEmpty() || lastLanguage.equals(Locale.getDefault().toString())){
            return false
        }
        else{
            return true
        }

     }


}