package com.emuench.magischemiesmuschel.activities

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.speech.tts.TextToSpeech
import android.widget.ImageButton
import android.widget.Toast
import com.emuench.magischemiesmuschel.R
import com.emuench.magischemiesmuschel.model.Mussel
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : BaseActivity(), SensorEventListener{

    //gui fields
    private lateinit var btnMussel : ImageButton
    private lateinit var btnSettings : ImageButton
    private lateinit var btnAnswerManagement : ImageButton

    //model and logic fields
    private lateinit var mussel : Mussel
    private lateinit var speechEngine : TextToSpeech

    //sensor fields
    private lateinit var lightSensor : Sensor
    private lateinit var sensorManager: SensorManager


    //functions
    override fun onCreate(savedInstanceState: Bundle?) {

        println("ONCREATE")
        //getting sharedPreferences for loading preferences of settings and Data
        val settingsPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_settings_key), Context.MODE_PRIVATE)
        val dataPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_data_key),Context.MODE_PRIVATE)


        //creating mussel
        var answersOfMussel : MutableList<String>? = loadDataFromSharedPreferences<MutableList<String>>(dataPreferences,resources.getString(R.string.shared_preferences_data_answers_key))

        //init list
        if(answersOfMussel == null){
            answersOfMussel = mutableListOf(resources.getString(R.string.answer_yes),
                                     resources.getString(R.string.answer_no),
                                     resources.getString(R.string.answer_maybe))

            saveDataToSharedPreferences<MutableList<String>>(dataPreferences,answersOfMussel,resources.getString(R.string.shared_preferences_data_answers_key))
        }


        mussel = Mussel(answersOfMussel,resources.getString(R.string.answer_default))

        //setup speach engine
        //speechEngine = TextToSpeech(this,{ status -> setUpSpeachEngine(status) })

        //inflating layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)



        //setup image button (btnMussel interaction)
        btnMussel = findViewById(R.id.imgbtn_mussel)
        btnMussel.setOnClickListener{

            //getting answer of mussel
            val answer = mussel.answer()
            Toast.makeText(this,answer,Toast.LENGTH_SHORT).show()

            //speak out answer if allowed
            val allowedToSpeak = settingsPreferences.getBoolean(resources.getString(R.string.shared_preferences_settings_key_switch_voice_output),true)
            if(allowedToSpeak){

                speechEngine.speak(answer,TextToSpeech.QUEUE_FLUSH,null)
            }


        }

        //setup settings navigation
        btnSettings = findViewById(R.id.btn_settings)
        btnSettings.setOnClickListener{
            startActivity(Intent(this,SettingsActivity::class.java)) }

        //setup mussel answers navigation
        btnAnswerManagement = findViewById(R.id.btn_answer_management)
        btnAnswerManagement.setOnClickListener{startActivity(Intent(this,AnswerManagementActivity::class.java))}

        //setup pseudo night mode
        sensorManager = getSystemService(Service.SENSOR_SERVICE) as SensorManager
        lightSensor = sensorManager.getDefaultSensor(Sensor.TYPE_LIGHT)


    }

    override fun onPause(){
        super.onPause()
        println("ONPAUSE")
        //sensor unregistration
        sensorManager.unregisterListener(this)

        //saving last language
        val answersOfMussel = mussel.answers
        val settingsPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_settings_key), Context.MODE_PRIVATE)
        val editor = settingsPreferences.edit()
        editor.putString(resources.getString(R.string.shared_preferences_settings_key_last_language),Locale.getDefault().toString())

        //saving last yes
        if(answersOfMussel.contains(resources.getString(R.string.answer_yes))) {
            editor.putString(resources.getString(R.string.shared_preferences_settings_key_language_last_yes),resources.getString(R.string.answer_yes))
        }

        //saving last no
        if(answersOfMussel.contains(resources.getString(R.string.answer_no))) {
            editor.putString(resources.getString(R.string.shared_preferences_settings_key_language_last_no),resources.getString(R.string.answer_no))
        }

        //saving last maybe
        if(answersOfMussel.contains(resources.getString(R.string.answer_maybe))) {
            editor.putString(resources.getString(R.string.shared_preferences_settings_key_language_last_maybe),resources.getString(R.string.answer_maybe))
        }

        editor.apply()

    }

    override fun onResume() {
        super.onResume()
        println("ONRESUME")
        //setup speach engine
        speechEngine = TextToSpeech(this,{ status -> setUpSpeachEngine(status) })

        //sensor registration
        sensorManager.registerListener(this,lightSensor,SensorManager.SENSOR_DELAY_NORMAL)

        //translate default data if existing
        val dataPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_data_key),Context.MODE_PRIVATE)
        val settingsPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_settings_key), Context.MODE_PRIVATE)

        val musselAnswers = mussel.answers//= loadDataFromSharedPreferences<List<String>>(dataPreferences,resources.getString(R.string.shared_preferences_data_answers_key))

        if(languageChanged(settingsPreferences)){
            val lastYes : String? = settingsPreferences.getString(resources.getString(R.string.shared_preferences_settings_key_language_last_yes),"")
            val lastNo : String? = settingsPreferences.getString(resources.getString(R.string.shared_preferences_settings_key_language_last_no),"")
            val lastMaybe : String? = settingsPreferences.getString(resources.getString(R.string.shared_preferences_settings_key_language_last_maybe),"")


            //translating yes if necessary
            if(lastYes != null && musselAnswers.contains(lastYes)){
                musselAnswers.set(musselAnswers.indexOf(lastYes),resources.getString(R.string.answer_yes))
            }

            //translating no if necessary
            if(lastNo != null && musselAnswers.contains(lastNo)){
                musselAnswers.set(musselAnswers.indexOf(lastNo),resources.getString(R.string.answer_no))
            }

            //translating maybe if necessary
            if(lastMaybe != null && musselAnswers.contains(lastMaybe)){
                musselAnswers.set(musselAnswers.indexOf(lastMaybe),resources.getString(R.string.answer_maybe))
            }




            saveDataToSharedPreferences<MutableList<String>>(dataPreferences,musselAnswers,resources.getString(R.string.shared_preferences_data_answers_key))
        }

    }

    override fun onDestroy() {
        speechEngine.stop()
        speechEngine.shutdown()
        super.onDestroy()
    }

    //private functions
    private fun setUpSpeachEngine(status : Int){

        var languageSuport : Int

        if(status == TextToSpeech.SUCCESS){

            //getting default language and look, if it's supported
            var defLocale = Locale.getDefault()

            if(!(defLocale.toString().toLowerCase().contains(Regex("[(en)(de)(fr)(es)]+")))) {

                defLocale = Locale.ENGLISH

            }



            languageSuport = speechEngine.setLanguage(defLocale)

            //show error if language is not supported
            if(languageSuport == TextToSpeech.LANG_MISSING_DATA || languageSuport == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(this, resources.getString(R.string.error_no_language_support), Toast.LENGTH_SHORT).show()
            }
        }
        else{

            languageSuport = speechEngine.setLanguage(Locale.ENGLISH)
            if(languageSuport == TextToSpeech.LANG_MISSING_DATA || languageSuport == TextToSpeech.LANG_NOT_SUPPORTED) {

                Toast.makeText(this, resources.getString(R.string.error_no_language_support), Toast.LENGTH_SHORT).show()
            }

            Toast.makeText(this,resources.getString(R.string.error_speachengine_initializing_failure),Toast.LENGTH_SHORT).show()
        }

        println(speechEngine.language)
    }




    //sensor functions
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

    override fun onSensorChanged(event: SensorEvent?) {

        if(event != null && event.sensor.type == Sensor.TYPE_LIGHT){

            if(event.values[0] <= 12){
                img_bikinibottom.setImageResource(R.drawable.bikini_bottom_night)
            }
            else{
                img_bikinibottom.setImageResource(R.drawable.bikini_bottom_background)
            }

        }
    }


}
