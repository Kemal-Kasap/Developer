package com.dreamscloud.deneme

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.utilities.V
import kotlinx.android.synthetic.main.activity_main.*

/*
* cmd komut satırına yazarak Sha1 keyini alabilirsin. Şifre isteyecek şifreyi biliyorsun zaten :)
* keytool -list -v -keystore C:\Users\lenovo\Desktop\KEMAL\PROJELER\semptom.jks -alias key0
 */

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ana_layout.background.alpha = 100

        vs_human.setOnClickListener {
            V.vsComputer = false
            startActivity(Intent(this, OfflineOyun::class.java))
        }

        vs_computer.setOnClickListener {
            V.vsComputer = true
            V.computerPlayed = true
            startActivity(Intent(this, OfflineOyun::class.java))
        }

        online.setOnClickListener {
            V.vsComputer = false
            startActivity(Intent(this, Login::class.java))
        }

        introduction.setOnClickListener {
            startActivity(Intent(this, Introduction::class.java))
        }

        settings.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

        vs_human2.setOnClickListener {
            V.vsComputer = false
            startActivity(Intent(this, OfflineOyun::class.java))
        }

        vs_computer2.setOnClickListener {
            V.vsComputer = true
            V.computerPlayed = true
            startActivity(Intent(this, OfflineOyun::class.java))
        }

        online2.setOnClickListener {
            V.vsComputer = false
            startActivity(Intent(this, Login::class.java))
        }

        introduction2.setOnClickListener {
            startActivity(Intent(this, Introduction::class.java))
        }

        settings2.setOnClickListener {
            startActivity(Intent(this, SettingsActivity::class.java))
        }

    }

}
