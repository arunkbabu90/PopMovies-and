package arunkbabu90.popmovies.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

import arunkbabu90.popmovies.Constants;
import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Utils;
import arunkbabu90.popmovies.databinding.FragmentVerificationEmailBinding;
import arunkbabu90.popmovies.ui.activity.AccountVerificationActivity;

public class VerificationEmailFragment extends Fragment {
    private FragmentVerificationEmailBinding binding;

    private MaterialTextView mVerifyEmailTextView;
    private MaterialTextView mStatusTextView;

    private FirebaseAuth mAuth;
    private FirebaseFirestore mDb;
    private AppCompatActivity mActivity;
    private FirebaseUser mUser;
    private boolean mIsAccountVerified;
    private String mEmail;
    private String mPassword;

    public VerificationEmailFragment() {
        // Required empty public constructor
    }

    public VerificationEmailFragment(Context context, AppCompatActivity activity) {
        mActivity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Utils.inVerificationEmailFragment = true;
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentVerificationEmailBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mStatusTextView = binding.tvVerifyEmailStatus;
        mVerifyEmailTextView = binding.tvVerifyEmail;

        mAuth = FirebaseAuth.getInstance();
        mDb = FirebaseFirestore.getInstance();

        mEmail = AccountVerificationActivity.getUserEmail();
        mPassword = AccountVerificationActivity.getPassword();

        if (mPassword != null && !mPassword.equals("")) {
            // Sign in to the account if we have an email and a password then send the verification
            // When user signs up for the first time they may not be logged in; so log in
            // and then check for the account verification status; Otherwise we won't get the
            // verification status
            mAuth.signInWithEmailAndPassword(mEmail, mPassword)
                    .addOnSuccessListener(authResult -> {
                        mUser = authResult.getUser();
                        sendVerificationEmail(mEmail);
                    })
                    .addOnFailureListener(e -> {
                        mVerifyEmailTextView.setText(R.string.err_email_send);
                        if (getActivity() instanceof AccountVerificationActivity) {
                            AccountVerificationActivity a = (AccountVerificationActivity) getActivity();
                            a.makeRetryButtonVisible();
                        }
                    });
        } else {
            // If no password is provided, try to get the verification status without a password
            mUser = mAuth.getCurrentUser();
            sendVerificationEmail(mEmail);
        }

        if (getContext()!= null)
            mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextRed));

        mStatusTextView.setText(R.string.unverified);
    }

    /**
     * Send the verification email
     * @param email The email to which the verification is sent
     * @return True if the verification is in progress; False if failed
     */
    public boolean sendVerificationEmail(String email) {
        if (mUser != null && getActivity() != null) {
            mVerifyEmailTextView.setText(getString(R.string.sending_verification, email));
            mUser.sendEmailVerification()
                    .addOnCompleteListener(getActivity(), task -> {
                        if (task.isSuccessful()) {
                            mVerifyEmailTextView.setText(getString(R.string.verification_email_sent, email));
                        } else {
                            mVerifyEmailTextView.setText(R.string.err_verification_email_already_sent);
                            if (mActivity instanceof AccountVerificationActivity) {
                                ((AccountVerificationActivity) mActivity).makeRetryButtonVisible();
                            }
                        }
                    });
        } else {
            Toast.makeText(getContext(), R.string.err_default, Toast.LENGTH_SHORT).show();
            if (mActivity instanceof AccountVerificationActivity) {
                ((AccountVerificationActivity) mActivity).makeRetryButtonVisible();
            }
            return false;
        }
        return true;
    }

    /**
     * Helper method to push the account verified flag to the database
     * @param isVerified The flag that needs to be set
     */
    private void pushVerificationStatusFlag(boolean isVerified) {
        mDb.collection(Constants.COLLECTION_USERS).document(mUser.getUid())
                .update(Constants.FIELD_ACCOUNT_VERIFIED, isVerified)
                .addOnFailureListener(e -> {
                    // Keep retrying if fails
                    pushVerificationStatusFlag(isVerified);
                });
    }

    /**
     * Returns whether the account is verified or not
     * @return True if the account has been successfully verified; False otherwise
     */
    public boolean isAccountVerified() {
        return mIsAccountVerified;
    }

    @Override
    public void onStop() {
        super.onStop();
        Utils.inVerificationEmailFragment = false;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Check whether the email associated with the account is verified
        if (mUser != null) {
            mUser.reload()
                    .addOnSuccessListener(aVoid -> {
                        mUser = mAuth.getCurrentUser();
                        if (mUser != null && mUser.isEmailVerified()) {
                            // Email verified
                            mIsAccountVerified = true;
                            pushVerificationStatusFlag(true);
                            if (getContext()!= null)
                                mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextGreen));

                            mStatusTextView.setText(R.string.verified);
                            mVerifyEmailTextView.setText(R.string.verification_success_desc);
                            if (getActivity() instanceof AccountVerificationActivity) {
                                AccountVerificationActivity a = (AccountVerificationActivity) getActivity();
                                a.makeRetryButtonVisible();
                            }
                        } else {
                            // Email NOT verified
                            mIsAccountVerified = false;
                            if (getContext()!= null) {
                                mStatusTextView.setTextColor(ContextCompat.getColor(getContext(), R.color.colorTextRed));
                            }

                            mStatusTextView.setText(R.string.unverified);
                        }
                    });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}