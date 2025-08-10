package arunkbabu90.popmovies.ui.activity

import android.content.Intent
import android.net.ConnectivityManager
import android.net.ConnectivityManager.NetworkCallback
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.MutableLiveData
import arunkbabu90.popmovies.Constants
import arunkbabu90.popmovies.R
import arunkbabu90.popmovies.databinding.ActivityMovieBinding
import arunkbabu90.popmovies.ui.adapter.CategoryAdapter
import com.google.android.material.tabs.TabLayoutMediator
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class MovieActivity : AppCompatActivity(), FirebaseAuth.AuthStateListener {
    private lateinit var binding: ActivityMovieBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var db: FirebaseFirestore

    var networkChangeLiveData = MutableLiveData<Boolean>()
    private var tabLayoutMediator: TabLayoutMediator? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMovieBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Invoke Network Change Callback
        registerNetworkChangeCallback()

        auth = Firebase.auth
        db = Firebase.firestore
        auth.addAuthStateListener(this)

        fetchUserData()

        // Set the user id globally available in the app
        Constants.userId = auth.currentUser?.uid ?: ""

        window.statusBarColor = ActivityCompat.getColor(this, R.color.colorPurpleDark)
        window.navigationBarColor = ActivityCompat.getColor(this, R.color.colorPurple)

        // Set the title of the tabs
        tabLayoutMediator = TabLayoutMediator(binding.movieTabLayout, binding.movieViewPager) { tab, position ->
            tab.text = when (position) {
                0 -> getString(R.string.tab_title_now_playing)
                1 -> getString(R.string.tab_title_popular)
                2 -> getString(R.string.tab_title_top_rated)
                3 -> getString(R.string.tab_title_search)
                else -> ""
            }
        }

        val categoryAdapter = CategoryAdapter(supportFragmentManager, lifecycle)
        binding.movieViewPager.adapter = categoryAdapter
        binding.movieViewPager.offscreenPageLimit = 4

        tabLayoutMediator?.attach()

        // Set Menu Behaviours
        binding.toolbarMain.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.mnu_favourites -> {
                    // Favourites
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        // Safety Check to prevent opening Favourites if the user is a Guest
                        val favIntent = Intent(this, FavouritesActivity::class.java)
                        startActivity(favIntent)
                    }
                    true
                }
                R.id.mnu_profile_name -> {
                    // Profile
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        // Prevent opening Profile if the user is a Guest
                        val profIntent = Intent(this, ProfileActivity::class.java)
                        startActivity(profIntent)
                    }
                    true
                }
                R.id.mnu_sign_out -> {
                    // Sign Out
                    if (Constants.userType == Constants.USER_TYPE_PERSON) {
                        auth.signOut()
                    } else {
                        val sharedPref = getSharedPreferences(getString(R.string.pref_file_name_key), MODE_PRIVATE)
                        with(sharedPref.edit()) {
                            putBoolean(getString(R.string.pref_is_guest_logged_in), false)
                            apply()
                        }

                        startActivity(Intent(this, LoginActivity::class.java))
                        Toast.makeText(this, getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
                        finish()
                    }
                    true
                }
                else -> false
            }
        }
        val menu = if (Constants.userType == Constants.USER_TYPE_PERSON) R.menu.main_menu_user else R.menu.main_menu_guest
        binding.toolbarMain.inflateMenu(menu)
    }

    override fun onAuthStateChanged(p0: FirebaseAuth) {
        // User is either signed out or the login credentials no longer exists. So launch the login
        // activity again for the user to sign-in if the user is not a Guest User
        if (auth.currentUser == null && Constants.userType != Constants.USER_TYPE_GUEST) {
            startActivity(Intent(this, LoginActivity::class.java))
            Toast.makeText(this, getString(R.string.signed_out), Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun fetchUserData() {
        val user = auth.currentUser
        if (user != null)
            db.collection(Constants.COLLECTION_USERS).document(user.uid).get()
                .addOnCompleteListener { snapshot ->
                    if (snapshot.isSuccessful) {
                        val d = snapshot.result
                        if (d != null) {
                            // Set user full name globally available in the app
                            Constants.userFullName = d.getString(Constants.FIELD_FULL_NAME) ?: ""

                            // Check Account Verification Status
                            Constants.isAccountActivated = d.getBoolean(Constants.FIELD_ACCOUNT_VERIFIED) ?: false
                            if (!Constants.isAccountActivated) {
                                // If email NOT Already Verified; check the status again
                                checkAccountVerificationStatus()
                            } else {
                                // Silently check the verification status for security purposes
                                checkAccountVerificationSilently()
                            }
                        } else {
                            Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this, R.string.err_unable_to_fetch, Toast.LENGTH_SHORT).show()
                    }
                }
    }

    /**
     * Checks whether the email associated with this account is verified silently without alerting
     * the user if email is already verified
     * This is implemented as a security measure to prevent anyone from just modifying the value in
     * the database and gain unauthorized access. If found False-positive the value is changed to
     * False again in the database
     */
    private fun checkAccountVerificationSilently() {
        // Check whether the email associated with the account is verified
        val user = auth.currentUser
        user?.reload()?.addOnSuccessListener {
            if (user.isEmailVerified) {
                // Email verified
                Constants.isAccountActivated = true
                pushVerificationStatusFlag(true)
            } else {
                // Email NOT verified
                Constants.isAccountActivated = false
                pushVerificationStatusFlag(false)
                binding.tvPatientErrMsg.visibility = View.VISIBLE
                binding.tvPatientErrMsg.setText(R.string.err_account_not_verified_desc)
                binding.tvPatientErrMsg.setBackgroundColor(
                    ContextCompat.getColor(
                        this,
                        R.color.colorStatusUnverified
                    )
                )
                window.statusBarColor = ContextCompat.getColor(this, R.color.colorStatusUnverified)
                binding.tvPatientErrMsg.isClickable = true
                binding.tvPatientErrMsg.isFocusable = true
                binding.tvPatientErrMsg.setOnClickListener {
                    // Launch the Verification Activity
                    val i = Intent(this, AccountVerificationActivity::class.java)
                    i.putExtra(AccountVerificationActivity.KEY_USER_EMAIL, user.email)
                    i.putExtra(
                        AccountVerificationActivity.KEY_BACK_BUTTON_BEHAVIOUR,
                        AccountVerificationActivity.BEHAVIOUR_CLOSE
                    )
                    startActivity(i)
                }
            }
        }
    }

    /**
     * Checks whether the email associated with this account is verified
     */
    private fun checkAccountVerificationStatus() {
        with(binding) {
            // Check whether the email associated with the account is verified
            val user = auth.currentUser
            user?.reload()?.addOnSuccessListener {
                if (user.isEmailVerified) {
                    // Email verified
                    Constants.isAccountActivated = true

                    window.statusBarColor =
                        ContextCompat.getColor(this@MovieActivity, R.color.colorStatusVerified)
                    pushVerificationStatusFlag(true)
                    tvPatientErrMsg.visibility = View.VISIBLE
                    tvPatientErrMsg.setText(R.string.account_verified)
                    tvPatientErrMsg.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MovieActivity,
                            R.color.colorStatusVerified
                        )
                    )
                    tvPatientErrMsg.isClickable = false
                    tvPatientErrMsg.isFocusable = false
                    Handler(Looper.getMainLooper()).postDelayed({
                        tvPatientErrMsg.visibility = View.GONE
                        window.statusBarColor =
                            ContextCompat.getColor(this@MovieActivity, R.color.colorPurpleDark)
                    }, 3000)
                } else {
                    // Email NOT verified
                    Constants.isAccountActivated = false
                    window.statusBarColor =
                        ContextCompat.getColor(this@MovieActivity, R.color.colorStatusUnverified)
                    tvPatientErrMsg.visibility = View.VISIBLE
                    tvPatientErrMsg.setText(R.string.err_account_not_verified_desc)
                    tvPatientErrMsg.setBackgroundColor(
                        ContextCompat.getColor(
                            this@MovieActivity,
                            R.color.colorStatusUnverified
                        )
                    )
                    tvPatientErrMsg.isClickable = true
                    tvPatientErrMsg.isFocusable = true
                    tvPatientErrMsg.setOnClickListener {
                        // Launch the Verification Activity
                        val i = Intent(this@MovieActivity, AccountVerificationActivity::class.java)
                        i.putExtra(AccountVerificationActivity.KEY_USER_EMAIL, user.email)
                        i.putExtra(
                            AccountVerificationActivity.KEY_BACK_BUTTON_BEHAVIOUR,
                            AccountVerificationActivity.BEHAVIOUR_CLOSE
                        )
                        startActivity(i)
                    }
                }
            }
        }
    }

    /**
     * Helper method to push the account verified flag to the database
     * @param isVerified The flag that needs to be set
     */
    private fun pushVerificationStatusFlag(isVerified: Boolean) {
        val user = auth.currentUser
        if (user != null) {
            db.collection(Constants.COLLECTION_USERS).document(user.uid)
                .update(Constants.FIELD_ACCOUNT_VERIFIED, isVerified)
                .addOnFailureListener {
                    // Keep retrying if fails
                    pushVerificationStatusFlag(isVerified)
                }
        }
    }

    /**
     * Register a callback to be invoked when network connectivity changes
     */
    private fun registerNetworkChangeCallback() {
        val request = NetworkRequest.Builder()
            .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
            .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
            .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
            .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .build()

        val cm = getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager
        cm.registerNetworkCallback(request, object : NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                // Internet is Available
                runOnUiThread {
                    networkChangeLiveData.postValue(true)
                }
            }

            override fun onLost(network: Network) {
                super.onLost(network)
                // Internet is Unavailable
                runOnUiThread {
                    networkChangeLiveData.postValue(false)
                }
            }
        })
    }

    override fun onResume() {
        super.onResume()
        if (!Constants.isAccountActivated) {
            // If email NOT Already Verified; check the status again
            checkAccountVerificationStatus()
        }
    }
}