package com.ttp.powervision

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.ttp.powervision.databinding.ActivityMainBinding
import com.ttp.powervision.model.VisionResponse

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST: Int = 1234
    private val TAG: String = "MainActivity"

    private var mStorageRef: StorageReference? = null
    private var mStorage: FirebaseStorage? = null
    private var mFirestore: FirebaseFirestore? = null

    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.presenter = this
        binding.label = ""

        mStorage = FirebaseStorage.getInstance()
        mStorageRef = mStorage!!.reference
        mFirestore = FirebaseFirestore.getInstance()
    }

    fun onUploadClick() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select Pictures"), PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null && data.data != null) {
            val filePath: Uri = data.data
            uploadImage(filePath)
        }
    }

    private var imageRef: StorageReference? = null

    private fun uploadImage(filePath: Uri) {
        val progressBar = ProgressBar(this, null, android.R.attr.progressBarStyleSmall)
        progressBar.visibility = View.VISIBLE

        imageRef = mStorageRef!!.child("images/" + System.currentTimeMillis())
        imageRef!!.putFile(filePath).addOnSuccessListener { _ ->
            progressBar.visibility = View.GONE
            Toast.makeText(applicationContext, "Image uploaded", Toast.LENGTH_SHORT).show()
            retrieveVisionAPIData()
        }.addOnFailureListener {
            progressBar.visibility = View.GONE
            Toast.makeText(applicationContext, "Upload failed", Toast.LENGTH_SHORT).show()
        }.addOnProgressListener { snapShot ->
            val progress = (100 * snapShot.bytesTransferred / snapShot.totalByteCount).toInt()
            Log.e(TAG, "Progress: " + progress)
            progressBar.progress = progress
        }
    }

    private fun retrieveVisionAPIData() {
        mFirestore!!.collection("images").document(imageRef!!.name)
                .addSnapshotListener { documentSnapshot, firebaseFirestoreException ->
                    if (firebaseFirestoreException != null) {
                        Log.e(TAG, firebaseFirestoreException.localizedMessage)
                    } else {
                        if (documentSnapshot!!.exists()) {
                            Log.d(TAG, documentSnapshot.data.toString())
                            parseVisionResponse(Gson().toJson(documentSnapshot.data))
                        } else {
                            Log.d(TAG, "Waiting for Vision API data...")
                        }
                    }
                }
    }


    private fun parseVisionResponse(data: String) {
        val labels = StringBuffer()
        try {
            val visionResponse = Gson().fromJson(data, VisionResponse::class.java)
            visionResponse.web.webEntities.forEach { webEntity ->
                Log.d(TAG, webEntity.description)
                labels.append(webEntity.description)
                labels.append(" ")
            }
        } catch (e: JsonSyntaxException) {
            Log.e(TAG, "JsonException " + e.localizedMessage)
        }
        binding.label = labels.toString()
    }
}
