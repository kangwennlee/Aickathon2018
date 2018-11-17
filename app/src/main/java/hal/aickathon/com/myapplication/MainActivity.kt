package hal.aickathon.com.myapplication

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.hardware.camera2.CameraAccessException
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.support.annotation.RequiresApi
import android.support.v4.content.FileProvider
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.util.Log
import android.util.SparseIntArray
import android.view.Surface
import android.view.View
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.Response
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.cloud.FirebaseVisionCloudDetectorOptions
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata
import hal.aickathon.com.myapplication.model.Item
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException


class MainActivity : AppCompatActivity() {

    val url = "https://fashion.recoqnitics.com/analyze"

    val CAMERA_PERMISSIONS_REQUEST = 2
    val CAMERA_IMAGE_REQUEST = 3
    val FILE_NAME = "temp.jpg"
    val REQUEST_IMAGE_CAPTURE = 1
    private val GALLERY_PERMISSIONS_REQUEST = 0
    private val GALLERY_IMAGE_REQUEST = 1
    private val MAX_DIMENSION = 1200

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        rvItems.layoutManager = GridLayoutManager(this@MainActivity, 2)
        val items = ArrayList<Item>()
        items.add(Item(R.drawable.pic1, "Adidas", "Ultra Boost", "MYR 600.00"))
        items.add(Item(R.drawable.pic2, "Adidas", "Ultra Boost", "MYR 600.00"))
        items.add(Item(R.drawable.pic3, "Adidas", "Ultra Boost", "MYR 600.00"))
        items.add(Item(R.drawable.pic4, "Adidas", "Ultra Boost", "MYR 600.00"))
        items.add(Item(R.drawable.pic5, "Adidas", "Ultra Boost", "MYR 600.00"))
        items.add(Item(R.drawable.pic6, "Adidas", "Ultra Boost", "MYR 600.00"))
        rvItems.adapter = ItemAdapter(items, this@MainActivity, object : ItemAdapter.OnRecyclerListener {
            override fun onClicked(item: Item, position: Int) {
                val i = Intent(this@MainActivity, ItemDescriptionActivity::class.java)
                i.putExtra("image", item.image)
                startActivity(i)
            }
        })
        //btnLoad.setOnClickListener {
        //    createDialog()
        //}


        btnInstaFetch.setOnClickListener {
            // Handler code here.
            val intent = Intent(this, instafetch::class.java)
            startActivity(intent);
        }
    }

    private fun createDialog() {
        val builder = AlertDialog.Builder(this@MainActivity)
        builder
            .setMessage("Select your method")
            .setPositiveButton("Gallery") { dialog, which -> startGalleryChooser() }
            .setNegativeButton("Camera") { dialog, which -> startCamera() }
        builder.create().show()
    }

    private fun postPic(imgData: ByteArray) {
        val multipartRequest = object : VolleyMultipartRequest(Request.Method.POST, url,
            Response.Listener {
                val data = String(it.data)
                val json = JSONObject(data)
                val person = json.getJSONObject("person")
                if (!person.isNull("styles")) {
                    val boundingBox = person.getJSONObject("boundingBox")
                    val style = person.getJSONArray("styles")
                    val color = person.getJSONArray("colors")
                    val garment = person.getJSONArray("garments")
                } else {
                }
                //tvResponse.text = person.toString()
            }, Response.ErrorListener {
                //tvResponse.text = it.toString()
            }) {
            override fun getParams(): MutableMap<String, String> {
                val params = HashMap<String, String>()
                params["access_key"] = getString(R.string.coqnitive_access_key)
                params["secret_key"] = getString(R.string.coqnitive_secret_key)
                return params
            }

            override fun getByteData(): MutableMap<String, DataPart> {
                val params = HashMap<String, DataPart>()
                params["filename"] = DataPart("file.jpg", imgData, "image/jpeg")
                return params
            }
        }
        VolleySingleton.getInstance(baseContext).addToRequestQueue(multipartRequest)
    }

    private fun startGalleryChooser() {
        if (PermissionUtils.requestPermission(
                this@MainActivity,
                GALLERY_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select a photo"), GALLERY_IMAGE_REQUEST)
        }
    }

    private fun startCamera() {
        if (PermissionUtils.requestPermission(
                this@MainActivity,
                CAMERA_PERMISSIONS_REQUEST,
                android.Manifest.permission.READ_EXTERNAL_STORAGE,
                android.Manifest.permission.CAMERA
            )
        ) {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            val photoUri = FileProvider.getUriForFile(
                this@MainActivity,
                applicationContext.packageName + ".provider",
                getCameraFile()
            )
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivityForResult(intent, CAMERA_IMAGE_REQUEST)
        }
    }

    private fun getCameraFile(): File {
        val dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File(dir, FILE_NAME)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val image: FirebaseVisionImage? = null
        if (requestCode == GALLERY_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            setImage(data.data)
        } else if (requestCode == CAMERA_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            val photoUri =
                FileProvider.getUriForFile(
                    this@MainActivity,
                    applicationContext.packageName + ".provider",
                    getCameraFile()
                )
            setImage(photoUri)
        }
    }

    private fun setImage(uri: Uri?) {
        if (uri != null) {
            try {
                // scale the image to save on bandwidth
                val bitmap = scaleBitmapDown(
                    MediaStore.Images.Media.getBitmap(contentResolver, uri),
                    MAX_DIMENSION
                )
                //callCloudVision(bitmap);
                //runTextRecognition(bitmap)

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                //ivImage.setImageBitmap(bitmap)
                processPicFirebase(bitmap)
                //postPic(baos.toByteArray())

            } catch (e: IOException) {
                Log.d("SetImage.Error", "Image picking failed because " + e.toString())
                Toast.makeText(this, "Image picker error", Toast.LENGTH_LONG).show()
            }

        } else {
            Log.d("SetImage.Error", "Image picker gave us a null image.")
            Toast.makeText(this, "Image picker error", Toast.LENGTH_LONG).show()
        }
    }

    private fun scaleBitmapDown(bitmap: Bitmap, maxDimension: Int): Bitmap {

        val originalWidth = bitmap.width
        val originalHeight = bitmap.height
        var resizedWidth = maxDimension
        var resizedHeight = maxDimension

        if (originalHeight > originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = (resizedHeight * originalWidth.toFloat() / originalHeight.toFloat()).toInt()
        } else if (originalWidth > originalHeight) {
            resizedWidth = maxDimension
            resizedHeight = (resizedWidth * originalHeight.toFloat() / originalWidth.toFloat()).toInt()
        } else if (originalHeight == originalWidth) {
            resizedHeight = maxDimension
            resizedWidth = maxDimension
        }
        return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            CAMERA_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(
                    requestCode,
                    CAMERA_PERMISSIONS_REQUEST,
                    grantResults
                )
            ) {
                startCamera()
            }
            GALLERY_PERMISSIONS_REQUEST -> if (PermissionUtils.permissionGranted(
                    requestCode,
                    GALLERY_PERMISSIONS_REQUEST,
                    grantResults
                )
            ) {
                startGalleryChooser()
            }
        }
    }

    private fun processPicFirebase(bitmap: Bitmap) {
        val options = FirebaseVisionCloudDetectorOptions.Builder()
            .setModelType(FirebaseVisionCloudDetectorOptions.LATEST_MODEL)
            .setMaxResults(15)
            .build()
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val detector = FirebaseVision.getInstance().visionCloudLabelDetector
        var result = detector.detectInImage(image)
            .addOnSuccessListener {
                // Task completed successfully
                // ...
                val string = StringBuilder()
                for (label in it) {
                    val text = label.label
                    val entityId = label.entityId
                    val confidence = label.confidence
                    string.append(text + entityId + confidence)
                }
                //tvResponse.text = string
            }
            .addOnFailureListener {
                // Task failed with an exception
                // ...
            }
    }

    /**
     * Get the angle by which an image must be rotated given the device's current
     * orientation.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Throws(CameraAccessException::class)
    private fun getRotationCompensation(cameraId: String, activity: Activity, context: Context): Int {
        // Get the device's current rotation relative to its "native" orientation.
        // Then, from the ORIENTATIONS table, look up the angle the image must be
        // rotated to compensate for the device's rotation.
        val deviceRotation = activity.windowManager.defaultDisplay.rotation
        var rotationCompensation = ORIENTATIONS.get(deviceRotation)

        // On most devices, the sensor orientation is 90 degrees, but for some
        // devices it is 270 degrees. For devices with a sensor orientation of
        // 270, rotate the image an additional 180 ((270 + 270) % 360) degrees.
        val cameraManager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val sensorOrientation = cameraManager
            .getCameraCharacteristics(cameraId)
            .get(CameraCharacteristics.SENSOR_ORIENTATION)!!
        rotationCompensation = (rotationCompensation + sensorOrientation + 270) % 360

        // Return the corresponding FirebaseVisionImageMetadata rotation value.
        val result: Int
        when (rotationCompensation) {
            0 -> result = FirebaseVisionImageMetadata.ROTATION_0
            90 -> result = FirebaseVisionImageMetadata.ROTATION_90
            180 -> result = FirebaseVisionImageMetadata.ROTATION_180
            270 -> result = FirebaseVisionImageMetadata.ROTATION_270
            else -> {
                result = FirebaseVisionImageMetadata.ROTATION_0
                Log.e(ContentValues.TAG, "Bad rotation value: $rotationCompensation")
            }
        }
        return result
    }

    companion object {
        private val ORIENTATIONS = SparseIntArray()

        init {
            ORIENTATIONS.append(Surface.ROTATION_0, 90)
            ORIENTATIONS.append(Surface.ROTATION_90, 0)
            ORIENTATIONS.append(Surface.ROTATION_180, 270)
            ORIENTATIONS.append(Surface.ROTATION_270, 180)
        }
    }

}
