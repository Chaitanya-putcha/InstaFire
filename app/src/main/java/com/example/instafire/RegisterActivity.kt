package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.instafire.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

private const val TAG = "RegisterActivity"
class RegisterActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        var  auth: FirebaseAuth
        var userUid:String
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        val tvUsername = findViewById<EditText>(R.id.tvUsername1)
        val tvAge = findViewById<EditText>(R.id.tvAge)
        val tvEmail = findViewById<EditText>(R.id.tvEmail1)
        val tvPassword = findViewById<EditText>(R.id.tvPassword)
        val btnCreate = findViewById<Button>(R.id.btnRegister)

        btnCreate.setOnClickListener {
            if(tvUsername.text.isBlank()||tvAge.text.isBlank()||tvEmail.text.isBlank()||tvPassword.text.isBlank()){
                Toast.makeText(this, "Please Enter all details to sign up", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth = FirebaseAuth.getInstance()
            val email = tvEmail.text.toString()
            val password = tvPassword.text.toString()
            val username = tvUsername.text.toString()
            val Age = tvAge.text.toString().toInt()
            auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener { task ->
                if(task.isSuccessful){
                    Log.i(TAG,"Successfully created account")

                    // Creating of new user
                    userUid = auth.currentUser?.uid.toString()
                    val user = Users(
                        username,
                        Age
                    )
                    val firestore = FirebaseFirestore.getInstance()
                    firestore.collection("users").document(userUid).set(user)
                        .addOnCompleteListener { uploadTask ->
                            if(uploadTask.isSuccessful){
                                Log.i(TAG,"UserId : ${userUid}")
                            }else{
                                Log.i(TAG,"user is not created",uploadTask.exception)
                            }
                        }

                    //Getting into post activity
                    val intent = Intent(this,PostsActivity::class.java)
                    startActivity(intent)
                    finish()
                }else{
                    Toast.makeText(this, "Failed to create account", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}