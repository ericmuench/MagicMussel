package com.emuench.magischemiesmuschel.activities

import android.content.Context
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import androidx.appcompat.widget.Toolbar
import androidx.core.view.size

import com.emuench.magischemiesmuschel.R

class AnswerManagementActivity : BaseActivity() {

    //GUI Components
    private lateinit var toolbar : Toolbar
    private lateinit var linearLayout : LinearLayout
    private lateinit var btnAddEditTexts : ImageButton

    //fields
    private lateinit var answers : MutableList<String>
    private val editTexts : MutableList<EditText> = mutableListOf<EditText>()


    override fun onCreate(savedInstanceState: Bundle?) {

        //getting sharedPreferences for loading data
        val dataPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_data_key),Context.MODE_PRIVATE)

        //inflating layout
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_answer_management)

        //setup Linear Layout
        linearLayout = findViewById(R.id.LinLayout_answer_management)

        //setup toolbar
        toolbar = findViewById(R.id.toolbar_answer_management)
        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        //setup edittext-add button
        btnAddEditTexts = findViewById(R.id.btn_answer_management_add_edittexts)
        btnAddEditTexts.setOnClickListener({addEditText("")})

        //getting answers
        val musselAnwers = loadDataFromSharedPreferences<MutableList<String>>(dataPreferences,resources.getString(R.string.shared_preferences_data_answers_key))

        if(musselAnwers != null){
            answers = musselAnwers
        }

    }

    override fun onResume() {
        super.onResume()

        for(answer in answers){
            addEditText(answer)
        }
    }

    override fun onPause() {
        super.onPause()

        //getting sharedPreferences for loading data
        val dataPreferences = getSharedPreferences(resources.getString(R.string.shared_preferences_data_key),Context.MODE_PRIVATE)

        val editTextAnswers = mutableListOf<String>()
        for (eText in editTexts){

            val text = eText.text.toString()
            if(text.isNotEmpty() && text.isNotBlank()){
                editTextAnswers.add(text)
            }

        }

        saveDataToSharedPreferences<MutableList<String>>(dataPreferences,editTextAnswers,resources.getString(R.string.shared_preferences_data_answers_key))

    }

    //private functions
    private fun addEditText(text: String){

        val newOne = EditText(this)

        if(text.isNotBlank() && text.isNotEmpty()){
            newOne.setText(text)
        }

        linearLayout.addView(newOne,linearLayout.size-1)
        editTexts.add(newOne)
    }

}
