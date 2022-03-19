package com.example.a20220319

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.example.a20220319.databinding.ActivityMainBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class MainActivity : AppCompatActivity() {
    lateinit var activityResultLauncher: ActivityResultLauncher<Intent>
    lateinit var binding: ActivityMainBinding
    lateinit var filePath: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.galleryButton.setOnClickListener {
            //갤러리앱
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent,10)



            
        }
        binding.cameraButton.setOnClickListener {
            //카메라앱
            //파일 준비
            val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
            val storageDir : File? = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val file = File.createTempFile(
                "JPEG_${timeStamp}_",
                ".jpg",
                storageDir
            )
            filePath = file.absolutePath

            val photoURI : Uri = FileProvider.getUriForFile(
                this,
                "com.example.a20220319.fileprovider",file
            )
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            intent.putExtra(MediaStore.EXTRA_OUTPUT,photoURI)
            startActivityForResult(intent,20)


        }
//        //activityResultLauncher 초기화
//        activityResultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//            if (it.resultCode == 10){
//                try {
//                    //insamplesize 비율 계산
//                    val calRatio = calculateInSampleSize(
//                        it.data!!.data!!,
//                        resources.getDimensionPixelSize(R.dimen.imgSize),
//                        resources.getDimensionPixelSize(R.dimen.imgSize)
//                    )
//                    val option = BitmapFactory.Options()
//                    option.inSampleSize = calRatio
//                    //이미지 로딩
//                    var inputStream = contentResolver.openInputStream(it.data!!.data!!)
//                    val bitmap = BitmapFactory.decodeStream(inputStream,null,option)
//                    inputStream!!.close()
//                    inputStream = null
//                    bitmap?.let {
//                        binding.userImageView.setImageBitmap(bitmap)
//                    }
//                }catch (e : Exception)
//                {
//                    e.printStackTrace()
//                }
//            }else if (it.resultCode == 20)
//            {
//                // 카메라 앱
//                val calRatio = calculateInSampleSize(
//                    Uri.fromFile(File(filePath)),
//                    resources.getDimensionPixelSize(R.dimen.imgSize),
//                    resources.getDimensionPixelSize(R.dimen.imgSize)
//                )
//                val option = BitmapFactory.Options()
//                option.inSampleSize = calRatio
//                val bitmap = BitmapFactory.decodeFile(filePath,option)
//                bitmap?.let {
//                    binding.userImageView.setImageBitmap(bitmap)
//                }
//            }
//        }
    }

    private fun calculateInSampleSize(fileUri: Uri, reqWidth: Int, reqHeight: Int): Int {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        try {
            var inputStream = contentResolver.openInputStream(fileUri)

            //inJustDecodeBounds 값을 true 로 설정한 상태에서 decodeXXX() 를 호출.
            //로딩 하고자 하는 이미지의 각종 정보가 options 에 설정 된다.
            BitmapFactory.decodeStream(inputStream, null, options)
            inputStream!!.close()
            inputStream = null
        } catch (e: Exception) {
            e.printStackTrace()
        }
        //비율 계산........................
        val (height: Int, width: Int) = options.run { outHeight to outWidth }
        var inSampleSize = 1
        //inSampleSize 비율 계산
        if (height > reqHeight || width > reqWidth) {

            val halfHeight: Int = height / 2
            val halfWidth: Int = width / 2

            while (halfHeight / inSampleSize >= reqHeight && halfWidth / inSampleSize >= reqWidth) {
                inSampleSize *= 2
            }
        }
        return inSampleSize
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 10 && resultCode == Activity.RESULT_OK){
                try {
                    //insamplesize 비율 계산
                    val calRatio = calculateInSampleSize(
                        data!!.data!!,
                        resources.getDimensionPixelSize(R.dimen.imgSize),
                        resources.getDimensionPixelSize(R.dimen.imgSize)
                    )
                    val option = BitmapFactory.Options()
                    option.inSampleSize = calRatio
                    //이미지 로딩
                    var inputStream = contentResolver.openInputStream(data!!.data!!)
                    val bitmap = BitmapFactory.decodeStream(inputStream,null,option)
                    inputStream!!.close()
                    inputStream = null
                    bitmap?.let {
                        binding.userImageView.setImageBitmap(bitmap)
                    }
                }catch (e : Exception)
                {
                    e.printStackTrace()
                }
            }else if (requestCode == 20 && resultCode == Activity.RESULT_OK)
            {
                // 카메라 앱
                val calRatio = calculateInSampleSize(
                    Uri.fromFile(File(filePath)),
                    resources.getDimensionPixelSize(R.dimen.imgSize),
                    resources.getDimensionPixelSize(R.dimen.imgSize)
                )
                val option = BitmapFactory.Options()
                option.inSampleSize = calRatio
                val bitmap = BitmapFactory.decodeFile(filePath,option)
                bitmap?.let {
                    binding.userImageView.setImageBitmap(bitmap)
                }
            }

    }



    }
