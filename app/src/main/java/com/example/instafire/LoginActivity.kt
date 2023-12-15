package com.example.instafire

import android.content.ContentValues.TAG
import android.content.Intent
import android.nfc.Tag
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        val MyButton: Button = findViewById(R.id.button)
        val etEmail: EditText = findViewById(R.id.etEmail)
        val etPassword: EditText = findViewById(R.id.etPassword)
        val btnRegister = findViewById<Button>(R.id.btnRegister1)

        btnRegister.setOnClickListener {
            val intent = Intent(this,RegisterActivity::class.java)
            startActivity(intent)
        }

        var auth = FirebaseAuth.getInstance()
        if(auth.currentUser != null){
            goPostActivity()
        }
        MyButton.setOnClickListener {
            MyButton.isEnabled = false
            val email = etEmail.text.toString()
            val password = etPassword.text.toString()
            if (email.isBlank() || password.isBlank()) {
                Toast.makeText(this, "Email/Password cannot be empty", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                MyButton.isEnabled = true
                if (task.isSuccessful){
                    Toast.makeText(this,"Success!",Toast.LENGTH_SHORT).show()
                    goPostActivity()
            }else{
                Log.i(TAG,"signInWithEmailandPassword is failed",task.exception)
                 Toast.makeText(this,"Authentication is failed",Toast.LENGTH_SHORT).show()
                }

        }
    }
    }

    fun goPostActivity() {
        Log.i(TAG,"goPostActivity")
        val intent = Intent(this,PostsActivity::class.java)
        startActivity(intent)
        finish()
    }

}