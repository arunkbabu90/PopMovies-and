package arunkbabu90.popmovies.ui.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

import arunkbabu90.popmovies.Constants;
import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Utils;
import arunkbabu90.popmovies.databinding.ActivitySignUpBinding;
import arunkbabu90.popmovies.ui.dialogs.ErrorDialog;
import arunkbabu90.popmovies.ui.dialogs.ProcessingDialog;
import arunkbabu90.popmovies.ui.fragment.SignUpFragment;

public class SignUpActivity extends AppCompatActivity  implements ErrorDialog.ButtonClickListener {
    private int mErrorCase = -1;
    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;

    private SignUpFragment mSignUpFrag;
    private ProcessingDialog mProcessingDialog;

    private boolean mIsSigningUp;
    private String mFullName;
    private String mEmail;
    private String mPassword;
    private int mUserType;

    private static final String TAG = SignUpActivity.class.getSimpleName();

    /**
     * Error case occurred during adding information to user's profile
     */
    public static final int CASE_UPDATE_PROFILE = 5000;

    /**
     * Error case occurred while adding information to user's database
     */
    public static final int CASE_PUSH_DETAILS = 5001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivitySignUpBinding binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set status and nav bar colors
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mSignUpFrag = new SignUpFragment();

        getSupportFragmentManager().beginTransaction()
                .add(R.id.sign_up_fragment_container, mSignUpFrag)
                .commit();
    }

    /**
     * Creates a user account and signs up the user
     */
    private void performSignUp() {
        if (mSignUpFrag != null) {
            if (!mSignUpFrag.checkAllFields()) {
                // Field(s) are empty
                Toast.makeText(this, R.string.err_pls_fix_all_errors, Toast.LENGTH_SHORT).show();
                return;
            }
        }

        mIsSigningUp = true;
        mProcessingDialog = showProcessingDialog(getString(R.string.creating_profile), "");

        if (mSignUpFrag != null && SignUpFragment.signUpFragActive) {
            mFullName = mSignUpFrag.getFullName();
            mEmail = mSignUpFrag.getEmail();
            mPassword = mSignUpFrag.getPassword();
            mUserType = Constants.USER_TYPE_PERSON;
        }

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword)
                .addOnSuccessListener(authResult -> {
                    // User Created
                    // Make sure the user is signed in
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {
                        // Update the user's account with details first
                        updateProfile(user);
                    }
                })
                .addOnFailureListener(e -> {
                    // User Creation Failure
                    mIsSigningUp = false;
                });
    }

    /**
     * Helper method to push all the details the user entered to the database
     * @param user The Firebase user instance
     */
    private void pushDetailsToDatabase(FirebaseUser user) {
        if (mFullName.equals("") || mEmail.equals("") || mPassword.equals("")) {
            return;
        }
        // Create database with the UID and fill up all the details to the user's
        // profile database document
        String uid = user.getUid();

        Map<String, Object> ud = new HashMap<>();
        ud.put(Constants.FIELD_FULL_NAME, mFullName);
        ud.put(Constants.FIELD_USER_TYPE, mUserType);
        ud.put(Constants.FIELD_ACCOUNT_VERIFIED, false); // Initialize Verification status to false always

        mDb.collection(Constants.COLLECTION_USERS)
                .document(uid).set(ud)
                .addOnSuccessListener(aVoid -> {
                    // Information successfully added to database
                    if (mProcessingDialog != null) {
                        mProcessingDialog.dismiss();
                    }

                    // Initiate Account verification
                    Intent i = new Intent(this, AccountVerificationActivity.class);
                    i.putExtra(AccountVerificationActivity.KEY_USER_EMAIL, mSignUpFrag.getEmail());
                    i.putExtra(AccountVerificationActivity.KEY_USER_PASSWORD, mSignUpFrag.getPassword());
                    i.putExtra(AccountVerificationActivity.KEY_BACK_BUTTON_BEHAVIOUR, AccountVerificationActivity.BEHAVIOUR_LAUNCH_DASHBOARD);

                    startActivity(i);
                    finish();

                    mIsSigningUp = false;
                })
                .addOnFailureListener(e -> {
                    // Error while adding information to database
                    mErrorCase = CASE_PUSH_DETAILS;
                    if (mProcessingDialog != null) {
                        mProcessingDialog.dismiss();
                    }
                    mIsSigningUp = false;

                    Toast.makeText(this, R.string.err_profile_add_details_failed, Toast.LENGTH_LONG).show();
                });
    }

    /**
     * Helper method to update Firebase Profile
     * @param user The FirebaseUser
     */
    private void updateProfile(FirebaseUser user) {
        String fullName = mSignUpFrag.getFullName();

        if (!fullName.equals("")) {
            UserProfileChangeRequest profileUpdateRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(fullName)
                    .build();

            user.updateProfile(profileUpdateRequest)
                    .addOnSuccessListener(aVoid -> {
                        // Push the user data to database
                        pushDetailsToDatabase(user);
                    })
                    .addOnFailureListener(e -> {
                        // Show error with retry button
                        showErrorDialog(getString(R.string.err_profile_add_details_failed),
                                getString(R.string.retry));
                        mErrorCase = CASE_UPDATE_PROFILE;
                        mIsSigningUp = false;
                    });
        } else {
            Toast.makeText(this, R.string.err_some_field_empty, Toast.LENGTH_SHORT).show();
            mIsSigningUp = false;
        }
    }


    /**
     * Show the error dialog
     * @param message The error message to be shown
     * @param positiveButtonLabel The label of the positive button
     * @return An instance of the ErrorDialog Fragment
     */
    private ErrorDialog showErrorDialog(@NonNull String message, String positiveButtonLabel) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction. We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ErrorDialog dialog = new ErrorDialog(this, message, positiveButtonLabel);
        dialog.setButtonClickListener(this);
        dialog.show(ft, "dialog");
        return dialog;
    }

    /**
     * Show the processing dialog
     * @param message The message to be shown beside the loading circle
     * @param buttonLabel The label of the button. If empty, the button will be disabled
     * @return An instance of the ProcessingDialog Fragment
     */
    private ProcessingDialog showProcessingDialog(@NonNull String message, @NonNull String buttonLabel) {
        // DialogFragment.show() will take care of adding the fragment
        // in a transaction.  We also want to remove any currently showing
        // dialog, so make our own transaction and take care of that here.
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);

        ProcessingDialog dialog = new ProcessingDialog(this, message, buttonLabel, false);
        dialog.show(ft, "dialog");
        return dialog;
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Hide the soft input
        Utils.closeSoftInput(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Delete account if there is an error in pushing data to database
        if (mErrorCase != -1) {
            // If the mErrorCase is not NULL_INT there is an error
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user != null) {
                user.delete();
            }
        }
    }

    /**
     * Positive Button Click Event of ErrorDialog
     */
    @Override
    public void onPositiveButtonClick() {
        FirebaseUser user = mAuth.getCurrentUser();
        switch (mErrorCase) {
            case CASE_UPDATE_PROFILE:
                if (user != null) {
                    updateProfile(user);
                    Toast.makeText(this, R.string.retrying_profile_update, Toast.LENGTH_SHORT).show();
                }
                break;
            case CASE_PUSH_DETAILS:
                if (user != null)
                    pushDetailsToDatabase(user);

                mErrorCase = -1;
                break;
        }
    }

    /**
     * Negative Button Click Event of ErrorDialog
     */
    @Override
    public void onNegativeButtonClick() { }

    /**
     * Invoked when the Next button is clicked
     * @param view The view that is clicked
     */
    public void onNextClick(View view) {
        // Create button behaviour
        performSignUp();
    }
}