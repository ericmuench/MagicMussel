package com.emuench.magischemiesmuschel.model

import android.content.Context
import android.speech.tts.TextToSpeech
import android.widget.Toast
import com.emuench.magischemiesmuschel.R
import java.util.*
import kotlin.random.Random

class Mussel (answerList : MutableList<String>, defaultAnswer : String){

    //Fields
    val answers : MutableList<String> = answerList
    val defaultAnswer : String = defaultAnswer
    private val random = Random(System.currentTimeMillis())


    //functions
    fun answer() : String {
        //choosing Random answer
        return if(answers.size == 0) defaultAnswer else answers.get(random.nextInt(0,answers.size))
    }


}