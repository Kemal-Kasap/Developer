package com.dreamscloud.deneme

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dreamscloud.deneme.utilities.V
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.android.synthetic.main.activity_firebase.*
import java.util.*

class Firebase : AppCompatActivity() {

    val ref = FirebaseDatabase.getInstance().reference
    var ad: String = ""
    var kurucu = ""
    var davetli = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_firebase)

        ad = intent.getStringExtra("ad")!!

        //KENDİNE OLAN TEKLİFLERİ DİNLEMESİ İÇİN
        ref.child("Oyuncular").child(ad).child("teklifeden")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    kurucu = dataSnapshot.value.toString()
                    //kurucu burada karşi taraf, appyi kullanan değil yani child olarak teklifeden
                    //null ile oynamak ister misin diye soruyor diye "null" ekledim
                    if (kurucu != "yok" && kurucu != "null") {
                        teklifVar(kurucu)
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        //ONLİNE OYUNCULARI VE DURUMLARINI DİNLEMEK İÇİN
        ref.child("Oyuncular").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val items = ArrayList<SimpleItem>()
                for (ds in dataSnapshot.children) {
                    val oyuncular = ds.child("ad").value.toString()
                    val onlineMi = ds.child("online").value.toString()
                    val oynuyorMu = ds.child("kabul").value.toString()
                    val teklifEden = ds.child("teklifeden").value.toString()
                    val simpleitem = SimpleItem()
                    simpleitem.name = oyuncular
                    simpleitem.online = onlineMi
                    simpleitem.playing = oynuyorMu
                    simpleitem.teklifeden = teklifEden
                    items.add(simpleitem)
                }
                recyclerView(items)
            }

            override fun onCancelled(error: DatabaseError) {}
        })

    }

    fun oyunBaslat(kurucuMu: Boolean) {
        if (kurucuMu) oyunKur()
        if (V.teklifedenoynabaslamis != "true") {
            if (kurucuMu) {
                kurucu = ad
                //davetli = rakip
            } else {
                //kurucu = davetEden
                davetli = ad
            }
            val intent = Intent(this, OnlineOyun::class.java)
            intent.putExtra("kurucuMu", kurucuMu)
            intent.putExtra("kurucu", kurucu)
            intent.putExtra("davetli", davetli)
            startActivity(intent)
            //KURUCU OLMAYAN OYUNU OYNAYANLARI TRUE YAPAR. //bunu oyun kura alabilirsin aslında. sadece kurucu değiştirmiş olur.
            if (!kurucuMu) {
                ref.child("Oyuncular").child(kurucu).child("kabul").setValue("true")
                ref.child("Oyuncular").child(davetli).child("kabul").setValue("true")
            }
        } else {
            ref.child("Oyuncular").child(ad).child("teklifeden").setValue("yok")
            Toast.makeText(applicationContext, "Başkası ile oyna başlamış", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun oyunKur() {
        val oyunAd = ad + davetli
        if (V.kurucuTaraf == getString(R.string.white)) {
            ref.child("Games").child(oyunAd).child("oyunMod").child("WHITE").setValue(ad)
            ref.child("Games").child(oyunAd).child("oyunMod").child("BLACK").setValue(davetli)
        } else {
            ref.child("Games").child(oyunAd).child("oyunMod").child("WHITE").setValue(davetli)
            ref.child("Games").child(oyunAd).child("oyunMod").child("BLACK").setValue(ad)
        }
    }

    fun teklifVar(teklifEden: String) {
        V.teklifedenoynabaslamis = ""
        ref.child("Oyuncular").child(kurucu).child("kabul")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    V.teklifedenoynabaslamis = ds.value.toString()
                }

                override fun onCancelled(ds: DatabaseError) {
                }
            })
        //bunun çalışması için oyunu kurmadan önce oyun modunu firebase e gir
        //val beyaz = beyazKim()
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.teklif_var))
        val message = teklifEden + " " + getString(R.string.ile_oyna) //+ "\n" + "Beyaz: " + beyaz
        builder.setMessage(message)
        //builder.setPositiveButton("OK", DialogInterface.OnClickListener(function = x))
        builder.setPositiveButton(android.R.string.yes) { _, _ ->
            Toast.makeText(applicationContext, "kabul", Toast.LENGTH_LONG).show()

            oyunBaslat(kurucuMu = false)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            Toast.makeText(applicationContext, "red", Toast.LENGTH_LONG).show()
            ref.child("Oyuncular").child(ad).child("teklifeden").setValue("yok")
            ref.child("Oyuncular").child(ad).child("kabul").setValue("false")
            ref.child("Oyuncular").child(ad).child("kabul").setValue("notr")
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun beyazKim(): String {
        val oyunAd = kurucu + davetli
        var beyaz = ""
        V.ref.child("Games").child(oyunAd).child("oyunMod")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(ds: DataSnapshot) {
                    beyaz = ds.child("WHITE").value.toString()
                }

                override fun onCancelled(ds: DatabaseError) {
                }
            })
        return beyaz
    }

    fun recyclerView(items: ArrayList<SimpleItem>) {
        //create the ItemAdapter holding your Items
        val itemAdapter = ItemAdapter<SimpleItem>()
        //create the managing FastAdapter, by passing in the itemAdapter
        val fastAdapter: FastAdapter<SimpleItem> = FastAdapter.with(itemAdapter)
        //set our adapters to the RecyclerView
        recylerOnClick(fastAdapter)
        recylerViewim.setAdapter(fastAdapter)

        itemAdapter.add(items)

        val layoutManager = LinearLayoutManager(this)
        recylerViewim!!.layoutManager = layoutManager
    }

    fun recylerOnClick(fastAdapter: FastAdapter<SimpleItem>) {
        fastAdapter.addEventHook(object : ClickEventHook<SimpleItem>() {
            override fun onBind(viewHolder: RecyclerView.ViewHolder): View? {
                //return the views on which you want to bind this event
                return if (viewHolder is SimpleItem.ViewHolder) {
                    viewHolder.name
                } else {
                    null
                }
            }

            override fun onClick(
                v: View,
                position: Int,
                fastAdapter: FastAdapter<SimpleItem>,
                item: SimpleItem
            ) {
                if (item.online == "true") {
                    val oynuyorMu = item.playing
                    if (oynuyorMu != "true") {
                        if (item.teklifeden == "yok") {
                            renkSec(item)
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Zaten bir teklif değerlendiriyor.",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    } else {
                        Toast.makeText(applicationContext, "Zaten Oynuyor", Toast.LENGTH_LONG)
                            .show()
                    }
                } else {
                    Toast.makeText(applicationContext, "Online Değil", Toast.LENGTH_LONG).show()
                }
            }
        })
    }

    fun renkSec(item: SimpleItem) {
        val pieces = arrayOf(
            getString(R.string.white),
            getString(R.string.black),
            getString(R.string.cancel)
        )
        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.pick_your_side))
        builder.setItems(pieces) { _, which ->
            when (pieces[which]) {
                getString(R.string.white) -> {
                    V.kurucuTaraf = getString(R.string.white); TeklifEt(item)
                }
                getString(R.string.black) -> {
                    V.kurucuTaraf = getString(R.string.black); TeklifEt(item)
                }
                getString(R.string.cancel) -> {
                } //Hiç bi şey yapma
                else -> {
                    V.kurucuTaraf = getString(R.string.white); TeklifEt(item)
                }
            }
        }
        builder.setCancelable(false)
        builder.show()

    }

    fun TeklifEt(item: SimpleItem) {
        V.teklifduzenleyici = 0
        ref.child("Oyuncular").child(item.name.toString()).child("teklifeden")
            .setValue(ad)
        ref.child("Oyuncular").child(item.name.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val kabul = dataSnapshot.child("kabul").value.toString()
                    if (kabul == "true") {
                        Toast.makeText(applicationContext, "true", Toast.LENGTH_LONG).show()
                        davetli = item.name.toString()
                        oyunBaslat(kurucuMu = true)
                    } else if (kabul == "notr") {
                        //ADAM REDDETTİKTEN SONRA NOTRE DONDUGUNDE DE TEKLİFİNİZ GONDERİLDİ DİYOR. Duzenleyici bunu önledi şükür.
                        if (V.teklifduzenleyici == 0)
                            Toast.makeText(
                                applicationContext,
                                "Teklifiniz gönderildi",
                                Toast.LENGTH_LONG
                            ).show()
                    } else {
                        Toast.makeText(
                            applicationContext,
                            "Teklifinizi reddetti",
                            Toast.LENGTH_LONG
                        ).show()
                        V.teklifduzenleyici++
                    }
                }

                override fun onCancelled(error: DatabaseError) {}
            })
    }

    override fun onResume() {
        super.onResume()
        ref.child("Oyuncular").child(ad).child("ad").setValue(ad)
        ref.child("Oyuncular").child(ad).child("online").setValue("true")
        ref.child("Oyuncular").child(ad).child("teklifeden").setValue("yok")
        ref.child("Oyuncular").child(ad).child("kabul").setValue("notr")
    }

}
