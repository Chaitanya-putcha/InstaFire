package com.example.instafire

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.instafire.models.Post
import com.example.instafire.models.Users
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import com.google.firebase.ktx.Firebase
private const val TAG = "PostsActivity"
const val EXTRA_USERNAME = "EXTRA_USERNAME"
open class PostsActivity : AppCompatActivity() {
    private var signInUser: Users? = null
    private lateinit var toolbar:Toolbar
    private lateinit var post : MutableList<Post>
    private lateinit var adapter:PostsAdapter
    private  lateinit var fireStoreDb:FirebaseFirestore
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_posts)
        // toolbar setting
        toolbar = findViewById(R.id.toolbar3)
        setSupportActionBar(toolbar)

         val rvPosts:RecyclerView = findViewById(R.id.rvPosts)
        // Create a view for single post
        // Create data source
        post = mutableListOf()
        // Create a adapter class
        adapter = PostsAdapter(this,post)
        // Bind adapter class and layout manager to rv
        rvPosts.adapter = adapter
        rvPosts.layoutManager = LinearLayoutManager(this)

        fireStoreDb = FirebaseFirestore.getInstance()
        var postsReference = fireStoreDb
            .collection("posts")
            .limit(20)
            .orderBy("creation_time",Query.Direction.DESCENDING)

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

        //getting postsRefernce for user profile
        var username = intent.getStringExtra(EXTRA_USERNAME)
        if(username != null){
            toolbar.setTitle(username)
            postsReference = postsReference.whereEqualTo("user.name",username)
        }

        postsReference.addSnapshotListener { snapshot, exception ->
            if(exception != null || snapshot == null){
                Log.i(TAG,"Exception while query posts",exception)
                return@addSnapshotListener
            }
            val postList = snapshot.toObjects(Post::class.java)
            post.clear()
            post.addAll(postList)
            adapter.notifyDataSetChanged()
            for(post in postList){
                Log.i(TAG,"Post ${post}")
            }
        }

        // Floating action button set up
        var fabCreate:FloatingActionButton = findViewById(R.id.fabCreate)
        fabCreate.setOnClickListener {
            val intent = Intent(this,CreateActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_posts,menu)
        Log.d(TAG,"Menu inflated successfully")
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
          val intent = Intent(this,Profile_Activity::class.java)
           intent.putExtra(EXTRA_USERNAME,signInUser?.name)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }

}