package com.gideonst.uploadpdf

import android.Manifest
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
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import com.karumi.dexter.listener.single.PermissionListener
import java.io.File
import java.util.ArrayList
import ir.sohreco.androidfilechooser.FileChooser
import ir.sohreco.androidfilechooser.ExternalStorageNotAvailableException
import org.apache.commons.lang3.StringUtils
import java.net.URLDecoder


class MainActivity : BaseActivity(), View.OnClickListener, FileChooser.ChooserListener {
    override fun onSelect(path: String?) {


        Toast.makeText(this, path, Toast.LENGTH_SHORT).show();


    }


    private val REQUEST_CODE = 42

    var fileUtils: FileUtils? = null


    private lateinit var btnOpen: Button


    private val PDF_DIRECTORY = "/PDF_FOLDER_TESTING/"

    val TAG = MainActivity::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        Dexter.withActivity(this)
                .withPermissions(
                        Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.MANAGE_DOCUMENTS
                )
                .withListener(object : MultiplePermissionsListener {
                    override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }

                    override fun onPermissionRationaleShouldBeShown(permissions: MutableList<PermissionRequest>?, token: PermissionToken?) {
                        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    }


                }).check()


        btnOpen = findViewById(R.id.btn_upload)


        btnOpen.setOnClickListener(this)

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




    private fun getMyDataResult(data: Intent?) {
        val uri = data!!.getData()

        val src = uri.path

        val decodedUri = URLDecoder.decode(uri.toString(), "UTF-8")
        val cleanUri = Uri.parse(decodedUri)


        Log.e(TAG, "MY URI " + uri)
        Log.e(TAG, "MY CLEAN  URI " + cleanUri)
        Log.e(TAG, "MY SRC " + src)
//            Log.e(TAG, "MY GET PATH " + FileUtils.getPath(this,uri))


        Log.e(TAG, "MY FILE NAME " + getFileName(uri))
        Log.e(TAG, "MY REAL PATH  " + getPathFromUri(this, cleanUri))


        var realPath = getPathFromUri(this, cleanUri)
        val realFileName = getFileName(uri)
        val substr = StringUtils.substringBefore(realPath, "/" + realFileName);
        Log.e(TAG, "MY PATH WITHOUT FILE NAME  " + substr)


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
        Log.e(TAG, "FILE YANG DI KIRIM ADALAH  " + getFile(this, uri))
        Log.e(TAG, "NAMA DARI FILE YANG AKAN DIKIRIM  " + getFileName(uri))

        copyFile(this, substr, getFileName(uri), destination.toString())
//            copyFile(this, getPathFromURI(this,uri),"",destination.toString())/

//            copy(source,destination)


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