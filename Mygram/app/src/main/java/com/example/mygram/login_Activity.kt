package com.example.mygram

import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login_.*

class login_Activity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_)
        val auth=FirebaseAuth.getInstance()
        if(auth.currentUser!=null){
            gotopost()
        }
        b1.setOnClickListener {
            b1.isEnabled=false
            val email=e_mail.text.toString()
            val password=paswd.text.toString()
            if(email.isBlank() || password.isBlank()){
                Toast.makeText(this,"login",Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                b1.isEnabled=true
                if (task.isSuccessful){
                    Toast.makeText(this,"successfull",Toast.LENGTH_SHORT).show()
                    gotopost()
                }
                else{

                    Toast.makeText(this,"erroe in login",Toast.LENGTH_SHORT).show()
                }
            }

        }
    }
    private fun gotopost(){
        Toast.makeText(this,"welcome",Toast.LENGTH_SHORT).show()
        val intent=Intent(this,post_activity::class.java)
        startActivity(intent)
        finish()
    }
}