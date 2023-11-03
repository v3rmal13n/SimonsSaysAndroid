package com.example.simonsays.model

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.room.Room
import com.example.simonsays.data.Score
import com.example.simonsays.data.ScoreDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MyViewModel(application: Application) : AndroidViewModel(application) {

    val TAG_LOG: String = "ViewModel message"

    val seq = mutableListOf<Int>()

    val livedata_seq = MutableLiveData<MutableList<Int>>()

    var record = 0

    val livedata_record = MutableLiveData<Int>()

    @SuppressLint("StaticFieldLeak")
    val context = getApplication<Application>().applicationContext

    val db = Room
        .databaseBuilder(
            context,
            ScoreDatabase::class.java, "simon-dice"
        ).allowMainThreadQueries().build()

    init {
        Log.d(TAG_LOG, "Livedata initialization")
    }

    fun addStep() {
        Log.d("State", "Adding one step to the sequence")

        val num = (0..3).random()
        seq.add(num)

        livedata_seq.value = seq
    }

    fun addScoreToDB(score:Int?){
        CoroutineScope(Dispatchers.Main).launch {
            val scoreDao = db.scoreDao()

            val date = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                LocalDateTime.now()
                    .format(DateTimeFormatter.ofPattern("MMM dd yyyy, hh:mm:ss a"))
            } else {
                TODO("VERSION.SDK_INT < O")
            }

            val score: Score = Score(score, date)
            scoreDao.addScore(score)

            Log.d("State", "Adding new row to DB")

        }
    }

    fun getDBRecord(): Int{
        CoroutineScope(Dispatchers.Main).launch {
            val scoreDao = db.scoreDao()
            record = scoreDao.getRecord()

            Log.d("State", "Recovering record from db. Record: $record")
        }
        return record
    }

}