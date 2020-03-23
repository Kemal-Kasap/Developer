package com.dreamscloud.deneme

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TableLayout
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.dreamscloud.deneme.utilities.F
import com.dreamscloud.deneme.utilities.O
import com.dreamscloud.deneme.utilities.V
import com.github.bhlangonijr.chesslib.Board
import kotlinx.android.synthetic.main.activity_offline_oyun.*

/*
* cmd komut satırına yazarak Sha1 keyini alabilirsin. Şifre isteyecek şifreyi biliyorsun zaten :)
* keytool -list -v -keystore C:\Users\lenovo\Desktop\KEMAL\PROJELER\semptom.jks -alias key0
 */

//Bütün hamleleri dokunmatikHamle() fonksiyonu ile yapmak lazım. yoksa oynanmış hamleyi boyamıyor ve sıkıntı çıkıyor. Örneğin sesli hamle ya da online oyunda rakip hamle fonksiyonunda hamle() fonksiyonunu direk çağırma. dokunmatik hamle fonksiyonunu çağir. dokunmaatik hamle fonksiyonun çağırmadan önce de secili_ilk = ilk yap ve dokunmatikHamle(son) şeklinde çağır.
class OfflineOyun : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_offline_oyun)

        V.resetValues()

        F.tahta_ciz(this, duz = true)
        F.put_piece(this, V.board)
        butonClick()
        tusClick()

        if(V.vsComputer){
            val difficulty = arrayOf("Kolay", "Orta", "Zor")
            val builder = AlertDialog.Builder(this)
            builder.setTitle("Zorluk seviyesi seçiniz")
            builder.setItems(difficulty) { _, which ->
                when (difficulty[which]) {
                    //"Çok Kolay" -> V.depth = 0
                    "Kolay" -> V.zorluk = 0
                    "Orta" -> V.zorluk = 1
                    "Zor" -> V.zorluk = 2
                    else -> V.zorluk = 0
                }
            }
            builder.setCancelable(false)
            builder.show()
        }
        /*derin.setOnClickListener {
            V.depth++
        }*/
        /*button_hamleler.setOnClickListener {
            val tahtaValue = E.eval(V.board)
            //val move:Move = V.board.undoMove()
            val text = "$tahtaValue    " +
                    "Derinlik: ${V.depth}, ${V.TotalMoveCount} hamle hesaplandı, Sonuç: ${V.posMovesValuewithDepth}"
            textView.text = text
            //V.board.doMove(move)
        }*/

    }


    fun tusClick() {
        dinleme.setOnClickListener { S.dinleme(this, 1) }
    }

    fun butonClick() {
        for (i in 8 downTo 1) {
            for (j in listOf('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H')) {
                findViewById<TableLayout>(R.id.table_layout).findViewWithTag<SquareButton>("$j$i")
                    .setOnClickListener { O.myOnClick(this, GameModum.Normal, "$j$i") }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK) {
            S.myOnActivityResult(this, data, GameModum.Normal)
        } else if (requestCode == 2 && resultCode == RESULT_OK) {
            S.myOnActivityResult2(this, data, GameModum.Normal)
        } else if (requestCode == 3 && resultCode == RESULT_OK) {
            S.myOnActivityResult3(this, data, GameModum.Normal)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_turn -> {
                if (V.tahtaDuzMu) {
                    F.tahta_ciz(this, duz = false)
                    F.put_piece(this, V.board)
                    V.tahtaDuzMu = false
                } else {
                    F.tahta_ciz(this, duz = true)
                    F.put_piece(this, V.board)
                    V.tahtaDuzMu = true
                }
                butonClick()
                if (V.vsComputer) {
                    //EĞER BİLGİSAYARLA OYNUYOSA BİLGİSAYAR HAMLE YAPTIYSA
                    // TAHTAYI ÇEVİRİNCE HAMLE SIRASI TEKRAR BİLGİSAYARDA OLUR
                    //O YÜZDEN TEKRAR HAMLE YAPTIRDIK
                    if (V.computerPlayed) {
                        V.computerPlayed = false
                        H.bilgisayar(this, V.board)
                    } else {
                        V.computerPlayed = true
                    }
                }
                true
            }
            R.id.action_undo -> {
                if (V.moveCount > 0) {
                    V.board.undoMove();V.moveCount--; F.put_piece(this, V.board)
                }
                //BİLGİSAYARLA OYNARKEN 3 KERE GERİ ALINCA BİLGİSAYAR 2 HAMLE YAPIYOR
                //2 SN SONRA BİLGİSAYARIN OYNAYIP OYNAMADINI KONTORL EDİP ÖLE OYNAMASI SAĞLANDI SORUN ÇÖZÜLDÜ
                if (V.vsComputer) {
                    if (V.computerPlayed) {
                        V.computerPlayed = false
                        H.bilgisayar(this, V.board)
                    } else {
                        V.computerPlayed = true
                    }
                }
                true
            }
            R.id.action_new -> {
                V.board = Board();V.moveCount = 0 ;F.put_piece(this, V.board)
                if (V.vsComputer) {
                    if (!V.tahtaDuzMu) {
                        H.bilgisayar(this, V.board)
                        V.computerPlayed = false
                    } else {
                        V.computerPlayed = true
                    }
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}
