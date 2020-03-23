package com.dreamscloud.deneme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dreamscloud.deneme.utilities.V
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.mikepenz.fastadapter.FastAdapter
import com.mikepenz.fastadapter.adapters.ItemAdapter
import com.mikepenz.fastadapter.listeners.ClickEventHook
import kotlinx.android.synthetic.main.activity_online_players.*
import java.util.*

class OnlinePlayers : AppCompatActivity() {

    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    val user = FirebaseAuth.getInstance().currentUser
    val ref = FirebaseDatabase.getInstance().reference
    lateinit var mail: String
    lateinit var kurucu: String
    lateinit var davetli: String
    lateinit var davetliMail: String
    lateinit var appUser: String
    lateinit var appUserMail: String
    lateinit var kurucuMail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_online_players)

        configureGoogleSignIn()

        val email = user!!.email!!
        mail = email.split("@")[0]

        //KENDİ ADINI ÖĞRENMEK İÇİN
        ref.child("Oyuncular").child(mail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    appUser = dataSnapshot.child("name").value.toString()
                    appUserMail = dataSnapshot.child("mail").value.toString()
                }

                override fun onCancelled(error: DatabaseError) {}
            })

        //KENDİNE OLAN TEKLİFLERİ DİNLEMESİ İÇİN
        ref.child("Oyuncular").child(mail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    kurucu = dataSnapshot.child("teklifeden").value.toString()
                    kurucuMail = dataSnapshot.child("teklifedenmail").value.toString()
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
                    val oyuncular = ds.child("name").value.toString()
                    val maili = ds.child("mail").value.toString()
                    val onlineMi = ds.child("online").value.toString()
                    val oynuyorMu = ds.child("kabul").value.toString()
                    val teklifEden = ds.child("teklifeden").value.toString()
                    val simpleitem = SimpleItem()
                    simpleitem.name = oyuncular
                    simpleitem.mail = maili
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

    override fun onPause() {
        super.onPause()
        ref.child("Oyuncular").child(mail).child("online").setValue("false")
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_online_players, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_sign_out -> {
                signOut()
                revokeAccess()
                ref.child("Oyuncular").child(mail).removeValue()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun signOut() {
        startActivity(Login.getLaunchIntent(this))
        FirebaseAuth.getInstance().signOut()
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, OnlinePlayers::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }

    private fun revokeAccess() {
        mGoogleSignInClient.revokeAccess()
            .addOnCompleteListener(this, object : OnCompleteListener<Void?> {
                override fun onComplete(task: Task<Void?>) {
                    // hesabı defaulttan kurataracak
                }
            })
    }

    fun oyunBaslat(kurucuMu: Boolean) {
        if (kurucuMu) oyunKur()
        if (V.teklifedenoynabaslamis != "true") {
            if (kurucuMu) {
                kurucu = appUser
                kurucuMail = appUserMail
                //davetli = rakip
            } else {
                //kurucu = davetEden
                davetli = appUser
                davetliMail = appUserMail
            }
            val intent = Intent(this, OnlineOyun::class.java)
            intent.putExtra("kurucuMu", kurucuMu)
            intent.putExtra("kurucu", kurucu)
            intent.putExtra("davetli", davetli)
            intent.putExtra("kurucuMail", kurucuMail)
            intent.putExtra("davetliMail", davetliMail)
            startActivity(intent)
            //KURUCU OLMAYAN OYUNU OYNAYANLARI TRUE YAPAR. //bunu oyun kura alabilirsin aslında. sadece kurucu değiştirmiş olur.
            if (!kurucuMu) {
                ref.child("Oyuncular").child(kurucuMail).child("kabul").setValue("true")
                ref.child("Oyuncular").child(mail).child("kabul").setValue("true")
            }
        } else {
            ref.child("Oyuncular").child(mail).child("teklifeden").setValue("yok")
            Toast.makeText(applicationContext, "Başkası ile oyna başlamış", Toast.LENGTH_LONG)
                .show()
        }
    }

    fun oyunKur() {
        val oyunAd = mail + davetliMail
        //İlk başta eski dataları temizle
        ref.child("Games").child(oyunAd).removeValue()
        if (V.kurucuTaraf == getString(R.string.white)) {
            ref.child("Games").child(oyunAd).child("oyunMod").child("WHITE").setValue(appUserMail)
            ref.child("Games").child(oyunAd).child("oyunMod").child("BLACK").setValue(davetliMail)
        } else {
            ref.child("Games").child(oyunAd).child("oyunMod").child("WHITE").setValue(davetliMail)
            ref.child("Games").child(oyunAd).child("oyunMod").child("BLACK").setValue(appUserMail)
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
            //Toast.makeText(applicationContext, "kabul", Toast.LENGTH_LONG).show()
            oyunBaslat(kurucuMu = false)
        }
        builder.setNegativeButton(android.R.string.no) { _, _ ->
            //Toast.makeText(applicationContext, "red", Toast.LENGTH_LONG).show()
            ref.child("Oyuncular").child(mail).child("teklifeden").setValue("yok")
            ref.child("Oyuncular").child(mail).child("kabul").setValue("false")
            ref.child("Oyuncular").child(mail).child("kabul").setValue("notr")
        }
        builder.setCancelable(false)
        builder.show()
    }

    fun beyazKim(): String {
        val oyunAd = kurucuMail + davetliMail
        lateinit var beyaz: String
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
                if (item.mail != mail) {
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
                } else {
                    Toast.makeText(
                        applicationContext,
                        "Kendinize teklif gönderemezsiniz",
                        Toast.LENGTH_LONG
                    ).show()
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
        ref.child("Oyuncular").child(item.mail.toString()).child("teklifeden")
            .setValue(appUser)
        ref.child("Oyuncular").child(item.mail.toString()).child("teklifedenmail")
            .setValue(appUserMail)
        //BURADA KENDİ MAİLİNİ DİNLİYOR. KABUL ETİİ Mİ DİYE
        ref.child("Oyuncular").child(mail)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val kabul = dataSnapshot.child("kabul").value.toString()
                    if (kabul == "true") {
                        //Toast.makeText(applicationContext, "true", Toast.LENGTH_LONG).show()
                        davetli = item.name.toString()
                        davetliMail = item.mail.toString()
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


}