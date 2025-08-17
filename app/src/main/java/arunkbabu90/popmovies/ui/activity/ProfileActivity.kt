package arunkbabu90.popmovies.ui.activity

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentTransaction
import androidx.recyclerview.widget.LinearLayoutManager
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.databinding.ActivityProfileBinding
import arunkbabu90.popmovies.isNetworkConnected
import arunkbabu90.popmovies.resize
import arunkbabu90.popmovies.runStackedRevealAnimation
import arunkbabu90.popmovies.ui.adapter.ProfileAdapter
import arunkbabu90.popmovies.ui.dialogs.SimpleInputDialog
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.BitmapTransitionOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.gms.tasks.Task
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.firestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import com.google.firebase.storage.storage
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

class ProfileActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var binding: ActivityProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var adapter: ProfileAdapter
    private lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private var profileData = arrayListOf<Pair<String, String>>()

    private var dpPath = ""
    private var fullName = ""
    private var email = ""

    private var isInternetConnected = false
    private var isUpdatesAvailable = false
    private var isDataLoaded = false

    private val nameTitle = "Name"
    private val emailTitle = "Email"

    private val TAG = ProfileActivity::class.simpleName

    companion object {
        private const val REQUEST_CODE_PICK_IMAGE = 2000
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Status bar and Navigation bar colors
        window.statusBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)
        window.navigationBarColor = ContextCompat.getColor(this, R.color.colorBlackBackground)

        auth = Firebase.auth
        storage = Firebase.storage
        db = Firebase.firestore

        email = auth.currentUser?.email ?: ""

        registerNetworkChangeCallback()

        binding.fabDocProfileDpEdit.setOnClickListener(this)
        binding.ivProfileDp.setOnClickListener(this)
        binding.btnSignOut.setOnClickListener(this)
    }

    override fun onClick(p0: View?) {
        when(p0?.id) {
            binding.ivProfileDp.id -> {
                // View Dp
                val viewIntent = Intent(this, ViewPictureActivity::class.java)
                viewIntent.putExtra(ViewPictureActivity.PROFILE_PICTURE_PATH_EXTRA_KEY, dpPath)
                startActivity(viewIntent)
            }
            binding.fabDocProfileDpEdit.id -> {
                // Pick Image
                val pickImg = Intent(
                    Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI
                )
                pickImg.type = "image/*"
                startActivityForResult(
                    Intent.createChooser(
                        pickImg,
                        getString(R.string.pick_photo)
                    ), REQUEST_CODE_PICK_IMAGE
                )
            }
            binding.btnSignOut.id -> {
                // Sign out
                auth.signOut()
                finish()
            }
        }
    }

    /**
     * Pulls all the profile data from the database
     */
    private fun fetchData() {
        binding.pbProfileLoading.visibility = View.VISIBLE

        val user = auth.currentUser
        if (user != null)
        db.collection(Constants.COLLECTION_USERS).document(user.uid).get()
            .addOnCompleteListener { snapshot ->
                if (snapshot.isSuccessful) {
                    val d = snapshot.result
                    if (d != null) {
                        fullName = d.getString(Constants.FIELD_FULL_NAME) ?: ""
                        dpPath = d.getString(Constants.FIELD_DP_PATH) ?: ""

                        isDataLoaded = true

                        populateViews()
                        loadImageToView(Uri.parse(dpPath))
                    } else {
                        Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                        isDataLoaded = false
                    }
                } else {
                    Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                    isDataLoaded = false
                }
            }
    }

    /**
     * Populate the views with loaded data
     */
    private fun populateViews() {
        profileData = arrayListOf(
            nameTitle to fullName,
            emailTitle to email
        )

        adapter = ProfileAdapter(profileData) { dataPair -> onProfileItemClick(dataPair) }
        binding.rvProfile.adapter = adapter
        binding.rvProfile.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.VERTICAL,
            false
        )
        runStackedRevealAnimation(this, binding.rvProfile, true)

        binding.pbProfileLoading.visibility = View.GONE
    }

    private fun onProfileItemClick(data: Pair<String, String>) {
        val (title, subtitle) = data
        if (title == emailTitle) return

        val ft: FragmentTransaction = supportFragmentManager.beginTransaction()
        val prevFrag: Fragment? = supportFragmentManager.findFragmentByTag("dialog")
        if (prevFrag != null) {
            ft.remove(prevFrag)
        }
        ft.addToBackStack(null)

        val dialog = SimpleInputDialog(this, this, title, subtitle, getString(R.string.save))
        dialog.setButtonClickListener(object : SimpleInputDialog.ButtonClickListener {
            override fun onPositiveButtonClick(inputText: String?) {
                // Only push if the input text has some text in it
                if (!inputText.isNullOrBlank())
                    pushToDatabase(title, inputText)
            }

            override fun onNegativeButtonClick() {}

        })
        dialog.show(ft, "dialog")
    }

    /**
     * Pushes the updated profile data to database
     * @param title The Current Title of the dialog
     * @param inputText The text entered in the input field of the dialog
     */
    private fun pushToDatabase(title: String, inputText: String) {
        // Only update non empty and different values
        if (inputText.isBlank() || fullName == inputText) return

        val user = auth.currentUser

        // Update the Display Name of Firebase Auth
        val profileUpdateRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(inputText)
            .build()

        user?.updateProfile(profileUpdateRequest)?.addOnSuccessListener {
            Log.d(TAG, "Profile Update: Success")
        }?.addOnFailureListener { e: Exception? ->
            // Show error
            Toast.makeText(this, R.string.err_default, Toast.LENGTH_SHORT).show()
            Log.d(TAG, "Profile Update: Failure")
        }

        val dataMap = hashMapOf<String, String>()

        when (title) {
            nameTitle -> {
                // Name Update
                // Only add Updated values
                if (fullName != inputText)
                    dataMap[Constants.FIELD_FULL_NAME] = inputText
            }
            emailTitle -> { }
        }

        if (dataMap.isEmpty()) return

        if (user != null) {
            // Push details to database
            db.collection(Constants.COLLECTION_USERS).document(user.uid)
                .set(dataMap, SetOptions.merge())
                .addOnSuccessListener {
                    // Name Change Success
                    Toast.makeText(this, R.string.saved, Toast.LENGTH_SHORT).show()
                    profileData.clear()
                    profileData.add(nameTitle to inputText)
                    profileData.add(emailTitle to email)
                    adapter.notifyDataSetChanged()
                    Constants.userFullName = inputText

                    isUpdatesAvailable = false
                }.addOnFailureListener {
                    Toast.makeText(this, R.string.err_no_internet, Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, R.string.err_default, Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadImageToView(imageUri: Uri) {
        Glide.with(this)
            .asBitmap()
            .load(imageUri)
            .transition(BitmapTransitionOptions.withCrossFade())
            .into(object : CustomTarget<Bitmap>() {
                override fun onLoadStarted(placeholder: Drawable?) {
                    binding.pbProfileDpLoading.visibility = View.VISIBLE
                }

                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    binding.pbProfileDpLoading.visibility = View.GONE
                    binding.ivProfileDp.setImageBitmap(resource)

                    if (isUpdatesAvailable) {
                        // Scale Down the bitmap & Upload
                        val resizedBitmap = resource.resize(
                            height = Constants.DP_UPLOAD_SIZE,
                            width = Constants.DP_UPLOAD_SIZE
                        )
                        uploadImageFile(resizedBitmap)
                    }
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    binding.ivProfileDp.setImageBitmap(null)
                }

                override fun onLoadFailed(errorDrawable: Drawable?) {
                    binding.pbProfileDpLoading.visibility = View.GONE
                }
            })
    }

    /**
     * Upload the image files selected
     * @param bitmap The image to upload
     */
    fun uploadImageFile(bitmap: Bitmap) {
        binding.pbProfileDpLoading.visibility = View.VISIBLE
        Toast.makeText(this, getString(R.string.uploading_dp), Toast.LENGTH_SHORT).show()

        // Convert the image bitmap to InputStream
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val bs = ByteArrayInputStream(bos.toByteArray())

        val user = auth.currentUser
        if (user != null) {
            // Upload the image file
            val uploadPath = "${user.uid}/${Constants.DIRECTORY_PROFILE_PICTURE}/${Constants.PROFILE_PICTURE_FILE_NAME}${Constants.IMG_FORMAT_JPG}"
            val storageReference = storage.getReference(uploadPath)
            storageReference.putStream(bs).continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
                if (!task.isSuccessful) {
                    // Upload failed
                    Toast.makeText(this, getString(R.string.err_upload_failed), Toast.LENGTH_LONG).show()
                    return@continueWithTask null
                }
                storageReference.downloadUrl
            }.addOnCompleteListener { task: Task<Uri?> ->
                    if (task.isSuccessful && task.result != null) {
                        // Upload success; push the download URL to the database, also update the mDoctorDpPath
                        val imagePath = task.result.toString()
                        dpPath = imagePath
                        db.collection(Constants.COLLECTION_USERS).document(user.uid)
                            .update(Constants.FIELD_DP_PATH, imagePath)
                            .addOnSuccessListener {
                                Toast.makeText(this, getString(R.string.saved), Toast.LENGTH_SHORT).show()
                                isUpdatesAvailable = false
                            }
                            .addOnFailureListener { Toast.makeText(
                                this,
                                R.string.err_upload_failed,
                                Toast.LENGTH_SHORT
                            ).show() }
                    } else {
                        Toast.makeText(
                            this,
                            getString(R.string.err_upload_failed),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    binding.pbProfileDpLoading.visibility = View.GONE
                }
        }
        isUpdatesAvailable = true
    }


    /**
     * Register a callback to be invoked when network connectivity changes
     * @return True If internet is available; False otherwise
     */
    private fun registerNetworkChangeCallback(): Boolean {
        val isAvailable = BooleanArray(1)
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(request, object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Internet is Available
                runOnUiThread {
                    isInternetConnected = true
                    isAvailable[0] = true
                    if (!isDataLoaded)
                        fetchData()
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Internet is Unavailable
                isAvailable[0] = false
                runOnUiThread {
                    isInternetConnected = false
                }
            }
        })
        return isAvailable[0]
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE_PICK_IMAGE && resultCode == RESULT_OK) {
            val uri = data?.data
            if (uri != null) {
                if (isNetworkConnected(this)) {
                    binding.pbProfileDpLoading.visibility = View.VISIBLE
                    isUpdatesAvailable = true
                    loadImageToView(uri)
                } else {
                    Toast.makeText(this, R.string.err_img_load, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}