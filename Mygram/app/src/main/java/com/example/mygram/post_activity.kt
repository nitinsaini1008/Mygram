package com.example.mygram

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mygram.models.Posts
import com.example.mygram.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.auth.User
import kotlinx.android.synthetic.main.activity_post_activity.*

private const val TAG="Postactivity"
const val EXTRA_USERNAME="EXTRA_USERNAME"
open class post_activity : AppCompatActivity() {
    private var signedInUser:Users?=null
    private lateinit var firestoreDB:FirebaseFirestore
    private lateinit var post:MutableList<Posts>
    private lateinit var adapter: PostsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_activity)

        //create layout
        post= mutableListOf()

        adapter= PostsAdapter(this,post)
        tvPosts.adapter=adapter
        tvPosts.layoutManager=LinearLayoutManager(this)

        firestoreDB= FirebaseFirestore.getInstance()

        firestoreDB.collection("users")
            .document(FirebaseAuth.getInstance().currentUser?.uid as String)
            .get()
            .addOnSuccessListener { userSnapshot ->
                signedInUser = userSnapshot.toObject(Users::class.java)
                Log.i(TAG,"signed in user :$signedInUser")
            }
            .addOnFailureListener { exception ->
                Log.i(TAG,"Failer in signed ",exception)
            }

        var postsrefrence=firestoreDB
            .collection("posts")
            .limit(10)
            .orderBy("current_time",Query.Direction.DESCENDING)
        val username=intent.getStringExtra(EXTRA_USERNAME)
        if (username!=null){
            supportActionBar?.title=username
           postsrefrence= postsrefrence.whereEqualTo("user.username",username)
        }

        postsrefrence.addSnapshotListener { snapshot, exception ->
            if (snapshot==null || exception!=null){
                Log.i(TAG,"error in extrecting post",exception)
                return@addSnapshotListener
            }
            val postList= snapshot.toObjects(Posts::class.java)
            post.clear()
            post.addAll(postList)
            adapter.notifyDataSetChanged()
            for (i in postList){
                Log.i(TAG,"Post ${i}")
            }
        }
        addbutton.setOnClickListener {
            val intent=Intent(this,add_activity::class.java)
            startActivity(intent)
        }

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_post,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if(item.itemId == R.id.menu_profile){
            val intent=Intent(this,profile_activity::class.java)
            intent.putExtra(EXTRA_USERNAME, signedInUser?.username)
            startActivity(intent)
        }
        return super.onOptionsItemSelected(item)
    }
}