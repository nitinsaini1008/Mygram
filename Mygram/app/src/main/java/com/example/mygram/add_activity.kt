package com.example.mygram

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.mygram.models.Posts
import com.example.mygram.models.Users
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import kotlinx.android.synthetic.main.activity_add_activity.*
import android.Manifest

private const val TAG="add Activity"
private const val PICK_PHOTO_CODE=1234
class add_activity : AppCompatActivity() {
    private var signedInUser: Users?=null
    private var photouri: Uri?=null
    private lateinit var firestoreDB:FirebaseFirestore
    private lateinit var storageRefrence: StorageReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_activity)
        storageRefrence=FirebaseStorage.getInstance().reference
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

        file_chooser.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }
        upload.setOnClickListener {
            handleSubmitButton()
        }
    }
    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    //handle requested permission result
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.size > 0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED
                ) {
                    //permission from popup granted
                    pickImageFromGallery()
                } else {
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun handleSubmitButton() {
        if (photouri == null){
            Toast.makeText(this,"please select photo",Toast.LENGTH_SHORT).show()
            return
        }
        if (desc.text.isBlank()){
            Toast.makeText(this,"please enter descreption",Toast.LENGTH_SHORT).show()
            return
        }
        if (signedInUser==null){
            Toast.makeText(this,"signed first",Toast.LENGTH_SHORT).show()
            return
        }
        //uploading file code
        upload.isEnabled=false
        val photoUploadUri=photouri as Uri
        val photorefrence= storageRefrence.child("images/${System.currentTimeMillis()}-photo.jpg")

        photorefrence.putFile(photoUploadUri)
            .continueWithTask { photoUploadTask ->
                Log.i(TAG,"photo is uploadeding")
                photorefrence.downloadUrl
            }.continueWithTask { downloadUriTask ->
                val post=Posts(
                    desc.text.toString(),
                    downloadUriTask.result.toString(),
                    System.currentTimeMillis(),
                    signedInUser)
                firestoreDB.collection("posts").add(post)
            }.addOnCompleteListener { postCretionTask ->
                upload.isEnabled=true
                Log.i(TAG,"photo is still uploadeding")
                if (!postCretionTask.isSuccessful){
                    Log.i(TAG,"error in uploading")
                    Toast.makeText(this,"some error occupied",Toast.LENGTH_SHORT).show()
                }
                desc.text.clear()
                img.setImageResource(0)
                Toast.makeText(this,"Succcess",Toast.LENGTH_SHORT).show()

                val profileIntent=Intent(this,profile_activity::class.java)
                profileIntent.putExtra(EXTRA_USERNAME,signedInUser?.username)
                startActivity(profileIntent)
                finish()
            }
    }

        override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
            super.onActivityResult(requestCode, resultCode, data)
            if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE){
                photouri=data?.data
                img.setImageURI(data?.data)
            }
        }
}