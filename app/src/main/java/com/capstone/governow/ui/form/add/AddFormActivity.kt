package com.capstone.governow.ui.form.add

import android.Manifest
import android.content.ContentResolver
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.location.Location
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.capstone.governow.BuildConfig
import com.capstone.governow.R
import com.capstone.governow.databinding.ActivityAddFormBinding
import com.capstone.governow.ui.ViewModelFactory
import com.capstone.governow.ui.form.FormActivity
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AddFormActivity : AppCompatActivity() {
    private val viewModel by viewModels<AddFormViewModel> {
        ViewModelFactory.getInstance(this)
    }
    private lateinit var binding: ActivityAddFormBinding
    private lateinit var currentImagePath: String
    private lateinit var token: String
    private var currentLocation: Location? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddFormBinding.inflate(layoutInflater)
        setContentView(binding.root)

        viewModel.getSession().observe(this) { user ->
            if (user != null) {
                token = user.token.toString()
            }
        }

        binding.buttonAttach.setOnClickListener {
            openGallery()
        }

        binding.buttonSubmit.setOnClickListener {
            binding.buttonSubmit.isEnabled = false
            val title = binding.edTitle.text.toString().trim()
            val description = binding.edDescription.text.toString().trim()
            val date = binding.edDate.text.toString().trim()
            val location = binding.edLocation.text.toString().trim()

            if (title.isEmpty() || description.isEmpty() || date.isEmpty() || location.isEmpty()) {
                Toast.makeText(this, "Title, description, date, and location cannot be empty!", Toast.LENGTH_SHORT).show()
                binding.buttonSubmit.isEnabled = true
                return@setOnClickListener
            }

            currentImageUri?.let { uri ->
                val contentResolver: ContentResolver = applicationContext.contentResolver
                val inputStream = contentResolver.openInputStream(uri)
                val tempFile = File(cacheDir, "temp_image.jpg")
                inputStream?.use { input ->
                    FileOutputStream(tempFile).use { output ->
                        input.copyTo(output)
                    }
                }

                val compressedPhoto = compressImage(tempFile)
                compressedPhoto?.let { compressed ->
                    val requestFile = compressed.asRequestBody("image/jpeg".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData("photo", compressed.name, requestFile)

                    val descriptionBody = description.toRequestBody("text/plain".toMediaTypeOrNull())
                    val titleBody = title.toRequestBody("text/plain".toMediaTypeOrNull())
                    val dateBody = date.toRequestBody("text/plain".toMediaTypeOrNull())
                    val locationBody = location.toRequestBody("text/plain".toMediaTypeOrNull())

                    val insertStory = viewModel.addNewForm(token, body, titleBody, descriptionBody, dateBody, locationBody)
                    if (insertStory != null && !insertStory.error) {
                        startActivity(Intent(this, FormActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Error, Fill all fields correctly", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            binding.buttonSubmit.isEnabled = true
        }
    }

    private fun compressImage(file: File): File? {
        try {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(file.absolutePath, options)

            options.inSampleSize = calculateInSampleSize(options, 1024, 1024)

            options.inJustDecodeBounds = false
            val bitmap = BitmapFactory.decodeFile(file.absolutePath, options)

            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, byteArrayOutputStream)
            val fos = FileOutputStream(file)
            fos.write(byteArrayOutputStream.toByteArray())
            fos.flush()
            fos.close()

            return file
        } catch (e: Exception) {
            return null
        }
    }

    private fun calculateInSampleSize(options: BitmapFactory.Options, reqWidth: Int, reqHeight: Int): Int {
        val height = options.outHeight
        val width = options.outWidth
        var inSampleSize = 1

        if (height > reqHeight || width > reqWidth) {
            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2
            }
        }

        return inSampleSize
    }

    private fun showFilePreview(uri: Uri) {
        val contentResolver: ContentResolver = applicationContext.contentResolver
        val mimeType = contentResolver.getType(uri)

        binding.ivItemPhoto.visibility = View.GONE
        binding.tvItemFileName.visibility = View.GONE

        if (mimeType != null) {
            if (mimeType.startsWith("image/")) {
                binding.ivItemPhoto.setImageURI(uri)
                binding.ivItemPhoto.visibility = View.VISIBLE
            } else {
                val fileName = getFileName(uri)
                binding.tvItemFileName.text = fileName
                binding.tvItemFileName.visibility = View.VISIBLE
            }
        }
    }

    private fun getFileName(uri: Uri): String {
        var result: String? = null
        if (uri.scheme == "content") {
            val cursor = contentResolver.query(uri, null, null, null, null)
            try {
                cursor?.let {
                    if (it.moveToFirst()) {
                        result = it.getString(it.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME))
                    }
                }
            } finally {
                cursor?.close()
            }
        }
        if (result == null) {
            result = uri.path
            val cut = result?.lastIndexOf('/')
            if (cut != null && cut != -1) {
                result = result?.substring(cut + 1)
            }
        }
        return result ?: "Unknown file"
    }

    private val getContent = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        currentImageUri = uri
        showFilePreview(uri)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openGallery() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_MEDIA_IMAGES
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchGallery()
        } else {
            requestGalleryPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestGalleryPermission() {
        val permission = Manifest.permission.READ_MEDIA_IMAGES
        if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(this, "Gallery access is required to select an image.", Toast.LENGTH_SHORT).show()
        }
        requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            launchCamera()
        } else {
            requestCameraPermission()
        }
    }

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private fun requestCameraPermission() {
        val permission = Manifest.permission.CAMERA
        if (shouldShowRequestPermissionRationale(permission)) {
            Toast.makeText(this, "Camera access is required to take a photo.", Toast.LENGTH_SHORT).show()
        }
        requestPermissions(arrayOf(permission), REQUEST_CODE_PERMISSIONS)
    }

    @Suppress("DEPRECATION")
    private fun launchCamera() {
        val pictureFile: File? = try {
            createImageFile()
        } catch (ex: IOException) {
            null
        }

        pictureFile?.also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this,
                "${BuildConfig.APPLICATION_ID}.fileprovider",
                it
            )
            currentImageUri = photoURI

            val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            }

            startActivityForResult(cameraIntent, PIC_ID)
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File {
        val timeStamp: String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir: File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(
            "JPEG_${timeStamp}_",
            ".jpg",
            storageDir
        ).apply {
            currentImagePath = absolutePath
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PIC_ID && resultCode == RESULT_OK) {
            showFilePreview(currentImageUri!!)
        }
    }

    private fun launchGallery() {
        getContent.launch("image/* video/* application/pdf")
    }

    companion object {
        private var currentImageUri: Uri? = null
        private const val PIC_ID = 123
        private const val REQUEST_CODE_PERMISSIONS: Int = 101
    }
}
