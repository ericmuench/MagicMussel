package com.emuench.magischemiesmuschel.activities

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.SwitchCompat
import androidx.appcompat.widget.Toolbar
import com.emuench.magischemiesmuschel.R

class SettingsActivity : BaseActivity() {

    //GUI COMPONENTS
    private lateinit var toolbar: Toolbar
    private lateinit var switch_voiceOutput : SwitchCompat



    override fun onCreate(savedInstanceState: Bundle?) {
        //inflating layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)


        //getting sharedPreferences for loading & saving preferences
        val preferences = getSharedPreferences(resources.getString(R.string.shared_preferences_settings_key), Context.MODE_PRIVATE)

        //setup toolbar
        toolbar = findViewById(R.id.toolbar_settings)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //setup switch for voice output
        switch_voiceOutput = findViewById(R.id.switch_voice_output)
        switch_voiceOutput.setOnCheckedChangeListener{btn,checked ->

            //writing checked state into shared prefernces
            println(switch_voiceOutput.isChecked)


            val preferencesEditor = preferences.edit()
            preferencesEditor.putBoolean(resources.getString(R.string.shared_preferences_settings_key_switch_voice_output),switch_voiceOutput.isChecked)
            preferencesEditor.apply()

        }

        switch_voiceOutput.isChecked = preferences.getBoolean(resources.getString(R.string.shared_preferences_settings_key_switch_voice_output),true)


    }
}
