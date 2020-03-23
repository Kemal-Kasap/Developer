package com.dreamscloud.deneme.utilities

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.R

object T {
    fun toast(activity: AppCompatActivity, no: Int) {
        val message = when (no){
            1 -> activity.getString(R.string.sira_karsida)
            2 -> activity.getString(R.string.computers_turn)
            3 -> activity.getString(R.string.sira_online_rakipte)
            else -> "Ayarlanacaktır"
        }
        Toast.makeText(
            activity.applicationContext,
            message,
            Toast.LENGTH_SHORT
        ).show()
    }

}