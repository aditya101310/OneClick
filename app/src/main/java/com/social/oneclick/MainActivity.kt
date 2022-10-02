package com.social.oneclick

import android.Manifest
import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.button.MaterialButton
import java.io.ByteArrayOutputStream


class MainActivity : AppCompatActivity() {

    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2


    /*      Ask for camera and gallery Permission on resume (starting of app)       */
    override fun onResume() {
        super.onResume()
        checkCameraPermission()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val clickBtn = findViewById<MaterialButton>(R.id.clickBtn)

        /*    On Click of Share Button it will open pop up to select from gallery or camera     */
        clickBtn.setOnClickListener {

            val alertDialog: AlertDialog = AlertDialog.Builder(this).create() //Read Update
            alertDialog.setTitle("Select Image From")

            alertDialog.setButton(Dialog.BUTTON_NEGATIVE,"Camera",
                DialogInterface.OnClickListener { dialog, which ->
                    openCamera()
                })

            alertDialog.setButton(Dialog.BUTTON_POSITIVE,"Gallery",
                DialogInterface.OnClickListener { dialog, which ->
                    openGallery()
                })

            alertDialog.show()
        }


    }



    /*     It will check if we have permission to open gallery & camera to get image in      */
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }


    /*     Open Camera     */
    private fun openCamera() {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { intent ->
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
            }
        }
    }

    /*     Open Gallery     */
    private fun openGallery() {
        Intent(Intent.ACTION_GET_CONTENT).also { intent ->
            intent.type = "image/*"
            intent.resolveActivity(packageManager)?.also {
                startActivityForResult(intent, REQUEST_PICK_IMAGE)
            }
        }
    }


    /*      It will triggered when we have selected the image from gallery/camera successfully then it will ask to share on socail media     */
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_IMAGE_CAPTURE) {

                // Getting image as bitmap
                val bitmap = data?.extras?.get("data") as Bitmap

                // Converting bitmap image to uri
                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, bytes)
                val path: String = MediaStore.Images.Media.insertImage(this.contentResolver, bitmap, "Share Image", null)

                // Sending image to share on social media though uri
                val share = Intent(Intent.ACTION_SEND)
                share.setType("image/*")
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("$path"))
                startActivity(Intent.createChooser(share, "Share Image"));

            }
            else if (requestCode == REQUEST_PICK_IMAGE) {

                val uri = data?.getData()

                // Sending image to share on social media though uri
                val share = Intent(Intent.ACTION_SEND)
                share.setType("image/*")
                share.putExtra(Intent.EXTRA_STREAM, Uri.parse("$uri"))
                startActivity(Intent.createChooser(share, "Share Image"));

            }
        }
    }
}