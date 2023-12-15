package com.example.instafire

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.instafire.models.Post
import com.example.instafire.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

private  const val TAG = "CreateActivty"
private  const val PICK_PHOTO_CODE = 1234
class CreateActivity : AppCompatActivity() {
    private var photoUri: Uri? = null
    private var signInUser: Users? = null
    private  lateinit var fireStoreDb: FirebaseFirestore
    private lateinit var storageReference : StorageReference
    lateinit var btnSubmit : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        fireStoreDb = FirebaseFirestore.getInstance()
        storageReference = FirebaseStorage.getInstance().reference

        fireStoreDb.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapShot ->
                signInUser = userSnapShot.toObject(Users::class.java)
                Log.i(TAG,"signed in user: $signInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG,"Failure in fetching user",exception)
            }


        val btnPickImage:Button = findViewById(R.id.btnPickImage)
        btnSubmit = findViewById(R.id.CreateButton)

        btnPickImage.setOnClickListener {
            Log.i(TAG,"Open up image picker on device")
            val imageSelectIntent = Intent(Intent.ACTION_GET_CONTENT)
            imageSelectIntent.type = "image/*"
            startActivityForResult(imageSelectIntent,PICK_PHOTO_CODE)
        }

        // Creating method for submit button
        btnSubmit.setOnClickListener {
            handleSubmitButtonClick()
        }

    }

    private fun handleSubmitButtonClick() {
        val tvDes: TextView = findViewById(R.id.etDescription)
        if(photoUri == null){
            Toast.makeText(this,"No photo is added",Toast.LENGTH_SHORT).show()
            return
        }
        if(tvDes.text.isBlank()){
            Toast.makeText(this,"Description need to be added",Toast.LENGTH_SHORT).show()
            return
        }
        if(signInUser == null){
            Toast.makeText(this,"No user is signed in",Toast.LENGTH_SHORT).show()
            return
        }

        btnSubmit.isEnabled = false
        val photoReference = storageReference.child("Images/${System.currentTimeMillis()}-photo.jpg")
        // Upload photo to firebase storage
        val photoUploadUri = photoUri as Uri

        photoReference.putFile(photoUploadUri)
            .continueWithTask{photoUploadTask ->
                Log.i(TAG,"Uploaded bytes: ${photoUploadTask.result?.bytesTransferred}")
                // Retrieve image url for the uploaded image
                photoReference.downloadUrl
            }.continueWithTask { downloadUrlTask ->
                // Create post object with image Uri and added it to the posts collection
                val post = Post(
                    tvDes.text.toString(),
                    downloadUrlTask.result.toString(),
                    System.currentTimeMillis(),
                    signInUser)
                fireStoreDb.collection("posts").add(post)
            }.addOnCompleteListener { postCreationTask ->
                btnSubmit.isEnabled = true
                if(!postCreationTask.isSuccessful){
                    Log.i(TAG,"Execption during Firebase operations",postCreationTask.exception)
                    Toast.makeText(this, "Failed to save post", Toast.LENGTH_SHORT).show()
                }
                tvDes.text = ""
                var imageview : ImageView = findViewById(R.id.ivImage1)
                imageview.setImageResource(0)
                Toast.makeText(this, "Success!", Toast.LENGTH_SHORT).show()
                val profileIntent = Intent(this,Profile_Activity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME,signInUser?.name)
                startActivity(profileIntent)
                finish()
            }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == PICK_PHOTO_CODE){
            if(resultCode == Activity.RESULT_OK){
                 photoUri = data?.data
                 Log.i(TAG,"photoUri $photoUri")
                 val imageview:ImageView = findViewById(R.id.ivImage1)
                imageview.setImageURI(photoUri)
            }else{
                Toast.makeText(this, "Image picker action canceled", Toast.LENGTH_SHORT).show()
            }
        }
    }
}