package arunkbabu90.popmovies.ui.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseException;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;

import arunkbabu90.popmovies.Constants;
import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Utils;
import arunkbabu90.popmovies.databinding.ActivityLoginBinding;
import arunkbabu90.popmovies.ui.view.CustomInputTextField;

public class LoginActivity extends AppCompatActivity implements View.OnFocusChangeListener, OnCompleteListener<DocumentSnapshot> {
    private TextView mErrorTextView;
    private CustomInputTextField mEmailField;
    private CustomInputTextField mPasswordField;
    private MaterialButton mLoginButton;
    private TextView mForgotPasswordTextView;
    private ProgressBar mLoginProgressBar;

    private FirebaseAuth mAuth;
    private ConnectivityManager mConnectivityManager;
    private FirebaseFirestore mDb;
    private FirebaseUser mUser;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        // Get shared preferences
        sharedPref = getSharedPreferences(getString(R.string.pref_file_name_key), MODE_PRIVATE);
        boolean isGuestLoggedIn = sharedPref.getBoolean(getString(R.string.pref_is_guest_logged_in), false);

        // Get instances
        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mUser = mAuth.getCurrentUser();
        if (mUser != null && Utils.isNetworkConnected(this)) {
            // Login the user, if already logged in
            mUser.reload().addOnSuccessListener(aVoid -> {
                // The result will be available in onComplete() callback
                // REFRESH the User
                mUser = mAuth.getCurrentUser();
                if (mUser != null) {
                    mDb.collection(Constants.COLLECTION_USERS)
                            .document(mUser.getUid())
                            .get().addOnCompleteListener(LoginActivity.this);
                }
            });
        } else if (mUser != null && !Utils.isNetworkConnected(this)) {
            // If the user is already logged in even though there is no network, load the profile
            // from cache
            mDb.collection(Constants.COLLECTION_USERS).document(mUser.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> startMovieActivity(Constants.USER_TYPE_PERSON));
        } else if (isGuestLoggedIn) {
            startMovieActivity(Constants.USER_TYPE_GUEST);
        } else {
            // User not logged in. So show the login screen
            setTheme(R.style.Theme_PopMovies);
            ActivityLoginBinding binding = ActivityLoginBinding.inflate(getLayoutInflater());
            setContentView(binding.getRoot());

            // Set status and nav bar colors
            getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
            getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));

            mErrorTextView = binding.tvLoginError;
            mEmailField = binding.etEmail;
            mPasswordField = binding.etPassword;
            mLoginButton = binding.btnLogin;
            mForgotPasswordTextView = binding.tvForgotPassword;
            mLoginProgressBar = binding.pbLogin;

            // Load the TMDB Logo into the bottom image view
            Glide.with(this).load(R.drawable.tmdb_logo_short).into(binding.ivTmdbLogo);

            mConnectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);

            // Register a callback to track internet connectivity changes
            registerNetworkChangeCallback();
            if (!Utils.isNetworkConnected(this)) {
                // Network NOT Connected
                mErrorTextView.setText(getString(R.string.err_no_internet));
                mErrorTextView.setVisibility(View.VISIBLE);
                mLoginButton.setClickable(false);
                mForgotPasswordTextView.setClickable(false);
                mForgotPasswordTextView.setEnabled(false);
            }

            // Make the text field a password field here rather that in the XML to successfully apply
            //  the custom font; because setting it in XML won't work
            mPasswordField.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            mPasswordField.setTypeface(mEmailField.getTypeface());
            mPasswordField.setTransformationMethod(PasswordTransformationMethod.getInstance());
            mPasswordField.setOnFocusChangeListener(this);
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        if (hasFocus && v.getId() == mPasswordField.getId()) {
            // Check whether the email isn't blank or is valid
            checkEmail();
        }
    }

    /**
     * Login Button click
     */
    public void onLoginClick(View view) {
        if (checkEmail() && checkPassword()) {
            mLoginButton.setClickable(false);
            mLoginProgressBar.setVisibility(View.VISIBLE);

            // Hide virtual keyboard
            Utils.closeSoftInput(this);

            Editable emailText = mEmailField.getText();
            String email = "";
            if (emailText != null) email = emailText.toString();

            Editable passText = mPasswordField.getText();
            String password = "";
            if (passText != null) password = passText.toString();

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            // Login Success
                            mErrorTextView.setVisibility(View.INVISIBLE);
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                // The result will be available in onComplete() callback
                                mDb.collection(Constants.COLLECTION_USERS)
                                        .document(user.getUid())
                                        .get().addOnCompleteListener(LoginActivity.this);
                            }
                        } else {
                            // Login Failure
                            mErrorTextView.setText(getString(R.string.err_login));
                            mErrorTextView.setVisibility(View.VISIBLE);
                            mLoginProgressBar.setVisibility(View.GONE);
                            mLoginButton.setClickable(true);
                        }
                    });
        }
    }

    /**
     * Forgot Password TextView Click
     */
    public void onForgotPasswordClick(View view) {
        // Launch Forgot Password Activity
        startActivity(new Intent(this, ForgotPasswordActivity.class));
    }

    /**
     * Sign up TextView click
     */
    public void onSignUpTextViewClick(View view) {
        // Launch SignUp Activity
        startActivity(new Intent(this, SignUpActivity.class));
    }

    /**
     * Login as Guest TextView Click
     */
    public void onLoginGuestClick(View view) {
        startMovieActivity(Constants.USER_TYPE_GUEST);
    }

    /**
     * Check whether the email is valid or not empty
     * @return True If it is valid
     */
    private boolean checkEmail() {
        Editable emailText = mEmailField.getText();
        String email = "";
        if (emailText != null) {
            email = emailText.toString();
        }

        if (email.matches("")) {
            // Email is blank
            mEmailField.setError(getResources().getString(R.string.err_blank_email));
            return false;
        } else if (Utils.verifyEmail(email)) {
            // Invalid Email
            mEmailField.setError(getResources().getString(R.string.err_invalid_email));
            return false;
        }
        // Email is correct
        return true;
    }

    /**
     * Checks whether the password is not empty
     * @return True If it is not empty
     */
    private boolean checkPassword() {
        String password = Objects.requireNonNull(mPasswordField.getText()).toString();
        if (password.matches("")) {
            // Password is blank
            mPasswordField.setError(getResources().getString(R.string.err_password));
            return false;
        }
        // Password is correct
        return true;
    }

    /**
     * Register a callback to be invoked when network connectivity changes
     * @return True If internet is available; False otherwise
     */
    private boolean registerNetworkChangeCallback() {
        final boolean[] isAvailable = new boolean[1];

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        if (mConnectivityManager != null) {
            mConnectivityManager.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    // Internet is Available
                    runOnUiThread(() -> {
                        mErrorTextView.setText("");
                        mErrorTextView.setVisibility(View.INVISIBLE);
                        mLoginButton.setClickable(true);
                        mForgotPasswordTextView.setClickable(true);
                        mForgotPasswordTextView.setEnabled(true);

                        // In-case if the user is already logged in, perform auto-login
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            mLoginProgressBar.setVisibility(View.VISIBLE);
                            mDb.collection(Constants.COLLECTION_USERS).document(user.getUid()).get()
                                    .addOnSuccessListener(documentSnapshot -> {
                                        startMovieActivity(Constants.USER_TYPE_PERSON);
                                        mLoginProgressBar.setVisibility(View.GONE);
                                    });
                        }
                    });
                    isAvailable[0] = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Internet is Unavailable
                    isAvailable[0] = false;

                    runOnUiThread(() -> {
                        mErrorTextView.setText(getString(R.string.err_no_internet));
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mLoginButton.setClickable(false);
                        mForgotPasswordTextView.setClickable(false);
                        mForgotPasswordTextView.setEnabled(false);
                    });
                }
            });
        }

        return isAvailable[0];
    }

    /**
     * Launches movie activity
     * @param userType The user type integer {PERSON or GUEST}
     */
    private void startMovieActivity(int userType) {
        if (mLoginProgressBar != null) mLoginProgressBar.setVisibility(View.GONE);
        if (mLoginButton != null) mLoginButton.setClickable(true);

        if (sharedPref != null && userType == Constants.USER_TYPE_GUEST) {
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.pref_is_guest_logged_in), true);
            editor.apply();
        }

        Constants.setUserType(userType);
        startActivity(new Intent(this, MovieActivity.class));
        finish();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    /**
     * Database query result will be available here
     * @param task Contains the database query result
     */
    @Override
    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
        if (task.isSuccessful()) {
            DocumentSnapshot document = task.getResult();
            if (document != null) {
                if (document.exists()) {
                    // User logged in; Start the activity in USER Mode
                    startMovieActivity(Constants.USER_TYPE_PERSON);
                }
            }
        }
    }
}