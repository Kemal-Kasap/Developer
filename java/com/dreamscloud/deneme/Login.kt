package com.dreamscloud.deneme

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_login.*
import java.text.SimpleDateFormat
import java.util.*

class Login : AppCompatActivity() {

    val RC_SIGN_IN: Int = 1
    lateinit var mGoogleSignInClient: GoogleSignInClient
    lateinit var mGoogleSignInOptions: GoogleSignInOptions
    private lateinit var firebaseAuth: FirebaseAuth
    val ref = FirebaseDatabase.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        configureGoogleSignIn()
        setupUI()
        firebaseAuth = FirebaseAuth.getInstance()

    }

    private fun configureGoogleSignIn() {
        mGoogleSignInOptions = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, mGoogleSignInOptions)
    }

    private fun setupUI() {
        google_button.setOnClickListener {
            if (editText.text.toString() != "") signIn()
            else Toast.makeText(
                this,
                getString(R.string.please_enter_user_name),
                Toast.LENGTH_LONG
            ).show()
        }
    }

    private fun signIn() {
        val signInIntent: Intent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SIGN_IN) {
            val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account!!)
                val email = account.email!!
                val mail = email.split("@")[0]
                val edit = editText.text.toString()
                ref.child("Oyuncular").child(mail).child("mail").setValue(mail)
                ref.child("Oyuncular").child(mail).child("name").setValue(edit)
                ref.child("Oyuncular").child(mail).child("online").setValue("true")
                ref.child("Oyuncular").child(mail).child("teklifeden").setValue("yok")
                ref.child("Oyuncular").child(mail).child("kabul").setValue("notr")
            } catch (e: ApiException) {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(acct: GoogleSignInAccount) {
        val credential = GoogleAuthProvider.getCredential(acct.idToken, null)
        firebaseAuth.signInWithCredential(credential).addOnCompleteListener {
            if (it.isSuccessful) {
                val intent = OnlinePlayers.getLaunchIntent(this)
                startActivity(intent)
            } else {
                Toast.makeText(this, "Google sign in failed :(", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val user = FirebaseAuth.getInstance().currentUser
        if (user != null) {
            val email = user.email!!
            val mail = email.split("@")[0]
            val sdf = SimpleDateFormat("dd/M/yyyy hh:mm:ss", Locale("tr", "TR"))
            val currentDate = sdf.format(Date())
            ref.child("Oyuncular").child(mail).child("lastlogin").setValue(currentDate)
            ref.child("Oyuncular").child(mail).child("online").setValue("true")
            ref.child("Oyuncular").child(mail).child("teklifeden").setValue("yok")
            ref.child("Oyuncular").child(mail).child("kabul").setValue("notr")
            startActivity(OnlinePlayers.getLaunchIntent(this))
            finish()
        }
    }

    companion object {
        fun getLaunchIntent(from: Context) = Intent(from, Login::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        }
    }


}
