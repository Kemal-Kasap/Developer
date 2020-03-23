package com.dreamscloud.deneme

import android.content.Intent
import android.os.Bundle
import android.widget.TableLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.utilities.F
import com.dreamscloud.deneme.utilities.O
import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Side
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_online_oyun.*

//Bütün hamleleri dokunmatikHamle() fonksiyonu ile yapmak lazım. yoksa oynanmış hamleyi boyamıyor ve sıkıntı çıkıyor. Örneğin sesli hamle ya da online oyunda rakip hamle fonksiyonunda hamle() fonksiyonunu direk çağırma. dokunmatik hamle fonksiyonunu çağir. dokunmaatik hamle fonksiyonun çağırmadan önce de secili_ilk = ilk yap ve dokunmatikHamle(son) şeklinde çağır.
class OnlineOyun : AppCompatActivity() {

    lateinit var beyazMail: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_oyun)

        V.vsComputer = false

        V.resetValues()
        V.vsComputer = false
        V.onlineGame = true

        V.kurucuMu = intent.getBooleanExtra("kurucuMu", false)
        V.kurucu = intent.getStringExtra("kurucu")!!
        V.davetli = intent.getStringExtra("davetli")!!
        V.kurucuMail = intent.getStringExtra("kurucuMail")!!
        V.davetliMail = intent.getStringExtra("davetliMail")!!

        BeyazıBekle()
    }

    fun EveryThing() {
        if (V.kurucuMu) {
            if (beyazMail == V.kurucuMail) {
                F.tahta_ciz(this, duz = true)
                F.put_piece(this, V.board)
                V.kurucuTarafRenk = Side.WHITE.toString()
            } else {
                F.tahta_ciz(this, duz = false)
                F.put_piece(this, V.board)
                V.kurucuTarafRenk = Side.BLACK.toString()
            }
            top.text = V.davetli
            bottom.text = V.kurucu
        } else {
            if (beyazMail == V.kurucuMail) {
                F.tahta_ciz(this, duz = false)
                F.put_piece(this, V.board)
                V.kurucuTarafRenk = Side.WHITE.toString()
            } else {
                F.tahta_ciz(this, duz = true)
                F.put_piece(this, V.board)
                V.kurucuTarafRenk = Side.BLACK.toString()
            }
            top.text = V.kurucu
            bottom.text = V.davetli
        }

        butonClick()
        dinleme.setOnClickListener { S.dinleme(this, 1) }

        H.rakipHamle(this)
    }

    fun BeyazıBekle() {
        val oyunAd = V.kurucuMail + V.davetliMail
        V.ref.child("Games").child(oyunAd).child("oyunMod")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    beyazMail = ds.child("WHITE").value.toString()
                    Toast.makeText(applicationContext, beyazMail, Toast.LENGTH_LONG).show()
                    EveryThing()
                }

                override fun onCancelled(ds: DatabaseError) {
                }
            })
    }

    fun butonClick() {
        for (i in 8 downTo 1) {
            for (j in listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')) {
                findViewById<TableLayout>(R.id.table_layout).findViewWithTag<SquareButton>("$j$i")
                    .setOnClickListener { O.myOnClick(this, GameModum.Online, "$j$i") }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            S.myOnActivityResult(this, data, GameModum.Online)
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            S.myOnActivityResult2(this, data, GameModum.Online)
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            S.myOnActivityResult3(this, data, GameModum.Online)
        }
    }

}
