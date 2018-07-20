package com.gideonst.uploadpdf

import android.content.ActivityNotFoundException
import android.content.ContentUris
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.DocumentsContract
import android.provider.DocumentsContract.isDocumentUri
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.Toast
import java.io.File
import java.util.ArrayList
import ir.sohreco.androidfilechooser.FileChooser
import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException
import org.apache.commons.lang3.StringUtils


class MainActivity : BaseActivity(), View.OnClickListener, FileChooser.ChooserListener {
    override fun onSelect(path: String?) {


        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();


    }


    val MULTIPLE_PERMISSIONS = 10 // code you want.
    private val REQUEST_CODE = 42

     var fileUtils: FileUtils? = null

    internal var permissions = arrayOf(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

    private lateinit var btnOpen: Button


    private val PDF_DIRECTORY = "/PDF_FOLDER_TESTING/"

    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        btnOpen = findViewById(R.id.btn_upload)

        btnOpen.setOnClickListener(this)
        if (checkPermission()) {
//            showToast("hello")
        }
    }

    private fun checkPermission(): Boolean {
        var result: Int
        val listPermissionsNeeded = ArrayList<String>()
        for (p in permissions) {
            result = ContextCompat.checkSelfPermission(applicationContext, p)
            if (result != PackageManager.PERMISSION_GRANTED) {
                listPermissionsNeeded.add(p)
            }
        }
        if (!listPermissionsNeeded.isEmpty()) {
            ActivityCompat.requestPermissions(this, listPermissionsNeeded.toTypedArray(), MULTIPLE_PERMISSIONS)
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            MULTIPLE_PERMISSIONS -> {
                if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.
                    Log.d(TAG, "PERMISSION 1")


                }
                if (grantResults.size > 0 && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // permissions granted.

                    Log.d(TAG, "PERMISSION 2")

                }


            }
        }
    }

  /*  fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }*/

    private fun openFileExplorer() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "application/pdf"
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY)
        try {
            startActivityForResult(intent, REQUEST_CODE)
        } catch (e: ActivityNotFoundException) {
            //alert user that file manager not working
            Toast.makeText(this, "Error picking file", Toast.LENGTH_SHORT).show()
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {

            val uri = data!!.getData()
//            val src = uri.path

            Log.e(TAG, "MY URI " + uri)
//            Log.e(TAG, "MY SRC " + src)
//            Log.e(TAG, "MY GET PATH " + FileUtils.getPath(this,uri))


            Log.e(TAG, "MY FILE NAME "+getFileName(uri))
            Log.e(TAG, "MY REAL PATH  "+ getPathFromURI(this,uri))

            var realPath = getPathFromURI(this,uri)
            val realFileName = getFileName(uri)
            val substr = StringUtils.substringBefore(realPath, "/"+realFileName);
            Log.e(TAG, "MY PATH WITHOUT FILE NAME  "+ substr)


//            val source = File(src)
//            val filename = getFileName(uri)
//            val destination = File(Environment.getExternalStorageDirectory().absolutePath + "/CustomFolder/" + filename)
            val destination = File(Environment.getExternalStorageDirectory().absolutePath + PDF_DIRECTORY)


        /*    val destination = File(Environment.getExternalStorageDirectory().absolutePath + PDF_DIRECTORY + filename)
            if (!destination.exists()) {
                destination.mkdirs()
            }*/
//            Log.e(TAG, "SOURCE FILE " + source)
            Log.e(TAG, "DESTINATION  FILE " + destination)
            Log.e(TAG, "FILE YANG DI KIRIM ADALAH  " + getFile(this,uri))
            Log.e(TAG, "NAMA DARI FILE YANG AKAN DIKIRIM  " + getFileName(uri))

            copyFile(this, substr,getFileName(uri),destination.toString())
//            copyFile(this, getPathFromURI(this,uri),"",destination.toString())/

//            copy(source,destination)


        }
    }

    private fun getFileName(uri: Uri?): String {
        var result: String? = null
        if (uri != null) {
            if (uri.scheme == "content") {
                val cursor = contentResolver.query(uri, null, null, null, null)
                try {
                    if (cursor != null && cursor.moveToFirst()) {
                        result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME))
                        Log.d(TAG, "FILE NAME " + result)
                    }
                } finally {
                    cursor?.close()
                }
            }
        }
        if (result == null) {
            if (uri != null) {
                result = uri.lastPathSegment
            }
        }
        return result!!
    }

    private fun addFileChooserFragment() {
        val builder = FileChooser.Builder(FileChooser.ChooserType.FILE_CHOOSER,
                FileChooser.ChooserListener { path -> Toast.makeText(this@MainActivity, path, Toast.LENGTH_SHORT).show() })
        try {
            supportFragmentManager.beginTransaction()
                    .add(R.id.file_chooser_fragment_container_framelayout, builder.build())
                    .commit()
        } catch (e: ExternalStorageNotAvailableException) {
            Toast.makeText(this, "There is no external storage available on this device.",
                    Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }

    }

    override fun onClick(v: View?) {
        if (v != null) {
            when (v.id) {
                R.id.btn_upload -> {
                    openFileExplorer()
                }
            }
        }
    }

}