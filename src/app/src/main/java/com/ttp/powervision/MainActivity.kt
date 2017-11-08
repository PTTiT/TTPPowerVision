package com.ttp.powervision

import android.app.Activity
import android.content.Intent
import android.databinding.DataBindingUtil
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.ttp.powervision.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST: Int = 1234

    private var mStorageRef: StorageReference? = null
    private var mStorage: FirebaseStorage? = null
    private var mFirestore: FirebaseFirestore? = null

    lateinit var binding: ActivityMainBinding
    private var mFilePath: Uri? = null

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
            mFilePath = data.data
        }

    }
}
