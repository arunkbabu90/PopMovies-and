package arunkbabu90.popmovies.ui.activity;

import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.NetworkRequest;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Utils;
import arunkbabu90.popmovies.databinding.ActivityForgotPasswordBinding;
import arunkbabu90.popmovies.ui.view.CustomInputTextField;


public class ForgotPasswordActivity extends AppCompatActivity {
    private CustomInputTextField mEmailField;
    private TextView mErrorTextView;
    private MaterialButton mSentButton;

    private String mEmail;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityForgotPasswordBinding binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Set status and nav bar colors
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));
        getWindow().setNavigationBarColor(ContextCompat.getColor(this, R.color.colorBlackBackground));

        mEmailField = binding.etForgotPasswordEmail;
        mErrorTextView = binding.tvForgotPasswordErr;
        mSentButton = binding.btnForgotPasswordSent;

        checkNetwork();

        mAuth = FirebaseAuth.getInstance();
    }

    /**
     * Check whether the email is valid or not empty
     * @return True If it is valid
     */
    private boolean checkEmail() {
        Editable emailText = mEmailField.getText();
        if (emailText != null) mEmail = emailText.toString();

        if (mEmail.matches("")) {
            // Email is blank
            mEmailField.setError(getResources().getString(R.string.err_blank_email));
            mEmailField.requestFocus();
            return false;
        } else if (Utils.verifyEmail(mEmail)) {
            // Invalid Email
            mEmailField.setError(getResources().getString(R.string.err_invalid_email));
            mEmailField.requestFocus();
            return false;
        }
        // Email is correct
        return true;
    }

    /**
     * Check for network connectivity changes
     * @return True If internet is available
     */
    private boolean checkNetwork() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        final boolean[] isAvailable = new boolean[1];

        NetworkRequest request = new NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_CELLULAR)
                .addTransportType(NetworkCapabilities.TRANSPORT_ETHERNET)
                .addTransportType(NetworkCapabilities.TRANSPORT_VPN)
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .build();
        if (cm != null) {
            cm.registerNetworkCallback(request, new ConnectivityManager.NetworkCallback() {
                @Override
                public void onAvailable(@NonNull Network network) {
                    super.onAvailable(network);
                    // Internet is Available
                    runOnUiThread(() -> {
                        // Update views on success
                        mSentButton.setClickable(true);
                        mErrorTextView.setVisibility(View.INVISIBLE);
                        mErrorTextView.setText("");

                    });
                    isAvailable[0] = true;
                }

                @Override
                public void onLost(@NonNull Network network) {
                    super.onLost(network);
                    // Internet is Unavailable
                    isAvailable[0] = false;

                    runOnUiThread(() -> {
                        // Update views on failure
                        mSentButton.setClickable(false);
                        mErrorTextView.setVisibility(View.VISIBLE);
                        mErrorTextView.setText(R.string.err_no_internet);
                    });
                }
            });
        }

        return isAvailable[0];
    }

    // SEND VERIFICATION Button Click
    public void onSendVerificationClick(View view) {
        // If the user's email is valid try to send verification email
        if (checkEmail()) {
            mAuth.sendPasswordResetEmail(mEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            // Verification Email Sent
                            Toast.makeText(ForgotPasswordActivity.this,
                                    getString(R.string.verification_email_sent, mEmail), Toast.LENGTH_LONG).show();
                            mErrorTextView.setVisibility(View.INVISIBLE);
                            mErrorTextView.setText("");
                            finish();
                        } else {
                            // Failed to send Verification Email
                            mErrorTextView.setVisibility(View.VISIBLE);
                            mErrorTextView.setText(R.string.err_send_verification_failed);
                        }
                    });
        }
    }
}