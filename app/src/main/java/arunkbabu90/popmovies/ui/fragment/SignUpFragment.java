package arunkbabu90.popmovies.ui.fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.transition.MaterialSharedAxis;
import com.google.firebase.auth.FirebaseAuth;

import org.jetbrains.annotations.NotNull;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.Utils;
import arunkbabu90.popmovies.databinding.FragmentSignUpBinding;
import arunkbabu90.popmovies.ui.view.CustomInputTextField;

public class SignUpFragment extends Fragment implements View.OnFocusChangeListener {
    private FragmentSignUpBinding binding;

    private CustomInputTextField mNameField;
    private CustomInputTextField mEmailField;
    private CustomInputTextField mPasswordField;
    private CustomInputTextField mConfirmPasswordField;

    public static boolean signUpFragActive = false;

    private FirebaseAuth mAuth;
    private boolean mIsEmailRegistered;
    private boolean mIsFragmentDetached;

    /**
     * Check the primary password field
     */
    public static final int MODE_FIELD_1 = 4001;

    /**
     * Check the confirm password field
     */
    public static final int MODE_FIELD_2 = 4002;

    /**
     * Check both the fields
     */
    public static final int MODE_BOTH = 4003;

    public SignUpFragment() {
        // Required empty public constructor
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        mIsFragmentDetached = false;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setEnterTransition(new MaterialSharedAxis(MaterialSharedAxis.X, true));
        setReturnTransition(new MaterialSharedAxis(MaterialSharedAxis.X, false));
    }

    @Override
    public View onCreateView(@NotNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentSignUpBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        signUpFragActive = true;

        mNameField = binding.etSignUpFullName;
        mEmailField = binding.etSignUpEmail;
        mPasswordField = binding.etSignUpPassword;
        mConfirmPasswordField = binding.etSignUpPasswordConfirm;

        mAuth = FirebaseAuth.getInstance();

        mEmailField.setOnFocusChangeListener(this);
        mPasswordField.setOnFocusChangeListener(this);
        mConfirmPasswordField.setOnFocusChangeListener(this);
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        int id = v.getId();
        if (id == R.id.et_sign_up_email) {
            if (!hasFocus) {
                // Only check the validity of the email, if the user navigates away from the view
                checkEmail();
            }
        } else if (id == R.id.et_sign_up_password) {
            if (!hasFocus) {
                // Only check the password, if the user navigates away from the view
                checkPassword(MODE_FIELD_1);
            }
        } else if (id == R.id.et_sign_up_password_confirm) {
            if (!hasFocus) {
                // Only check the password, if the user navigates away from the view
                checkPassword(MODE_FIELD_2);
            }
        }
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
        } else {
            checkIsEmailRegistered(email);
        }
        // Email is correct
        return true;
    }

    /**
     * Checks whether the password is not empty
     * @param mode The evaluation mode; whether to check all the fields or or just a single one
     * @return True If it is not empty
     */
    private boolean checkPassword(int mode) {
        String password1 = mPasswordField.getText().toString();
        String password2 = mConfirmPasswordField.getText().toString();

        switch (mode) {
            case MODE_FIELD_1:
                if (password1.matches("")) {
                    // Password field is empty
                    mPasswordField.setError(getString(R.string.err_sign_up_password));
                    return false;
                } else if (password1.length() < 8) {
                    mPasswordField.setError(getString(R.string.err_password_length));
                    return false;
                }
                break;
            case MODE_FIELD_2:
                if (!password1.equals(password2)) {
                    // Passwords doesn't match
                    mConfirmPasswordField.setError(getString(R.string.err_password_no_match));
                    return false;
                }
                break;
            case MODE_BOTH:
                if (password1.matches("")) {
                    // Password field is empty
                    mPasswordField.setError(getResources().getString(R.string.err_sign_up_password));
                    return false;
                } else if (password1.length() < 8) {
                    mPasswordField.setError(getString(R.string.err_password_length));
                    return false;
                } else if (!password1.equals(password2)) {
                    // Passwords doesn't match
                    mConfirmPasswordField.setError(getResources().getString(R.string.err_password_no_match));
                    return false;
                }
                break;
        }
        // Password is correct
        return true;
    }

    /**
     * Helper method to check if the email is already registered
     * @param email The email to check against
     */
    private void checkIsEmailRegistered(String email) {
        if (!mIsFragmentDetached) {
            mAuth.fetchSignInMethodsForEmail(email)
                    .addOnCompleteListener(task -> {
                        boolean isRegistered = !task.getResult().getSignInMethods().isEmpty();
                        if (isRegistered) {
                            // Email Registered
                            mIsEmailRegistered = true;
                            mEmailField.setError(getString(R.string.err_email_exists));
                        } else {
                            // Email NOT Registered
                            mIsEmailRegistered = false;
                        }
                    });
        }
    }

    /**
     * Inspects all the EditTexts in this fragment to check whether all are filled
     * @return True  if all the fields are filled and the emails and passwords are valid; False  otherwise
     */
    public boolean checkAllFields() {
        if (!getFullName().equals("") && checkEmail() && checkPassword(MODE_BOTH) && !mIsEmailRegistered) {
            // If all the fields are filled and emails and passwords are valid; Return true
            return true;
        }

        // Show an error in all the fields that are empty
        if (getFullName().equals("")) {
            mNameField.setError(getString(R.string.err_empty_field));
        }

        checkEmail();
        checkPassword(MODE_BOTH);

        return false;
    }

    /**
     * Returns the text in the First Name field
     * @return String  Text in the first name field
     */
    public String getFullName() {
        if (mNameField.getText() != null)
            return mNameField.getText().toString().trim();
        else
            return "";
    }

    /**
     * Returns the text in the Email field
     * @return String  Text in the Email field
     */
    public String getEmail() {
        if (mEmailField.getText() != null)
            return mEmailField.getText().toString().trim();
        else
            return "";
    }

    /**
     * Returns the text in the Password field
     * @return String  Text in the Password field
     */
    public String getPassword() {
        if (mPasswordField.getText() != null)
            return mPasswordField.getText().toString().trim();
        else
            return "";
    }

    @Override
    public void onStop() {
        super.onStop();
        mIsFragmentDetached = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
        signUpFragActive = false;
    }
}