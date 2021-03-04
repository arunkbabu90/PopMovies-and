package arunkbabu90.popmovies.ui.dialogs;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import arunkbabu90.popmovies.R;
import arunkbabu90.popmovies.databinding.DialogSimpleInputBinding;

/**
 * A simple dialog fragment containing an EditText for receiving user input
 */
public class SimpleInputDialog extends DialogFragment {
    private DialogSimpleInputBinding mBinding;
    private TextInputLayout mInputTextInputLayout;
    private TextInputEditText mInputField;
    private TextView mPositiveTextView;
    private TextView mNegativeTextView;

    private final String mPrompt;
    private String mDefaultInput;
    private final String mPositiveButtonLabel;
    private final Context mContext;
    private final Activity mActivity;

    private ButtonClickListener mListener;

    /**
     * Creates a new instance of SimpleInputDialog.
     * @param context The context where this dialog belongs
     * @param activity The activity where this dialog belongs
     * @param prompt The prompt message to show in the dialog
     * @param positiveButtonLabel The label of the positive button
     */
    public SimpleInputDialog(Context context, Activity activity, @NonNull String prompt, @NonNull String positiveButtonLabel) {
        mContext = context;
        mActivity = activity;
        mPrompt = prompt;
        if (!positiveButtonLabel.equals("")) {
            mPositiveButtonLabel = positiveButtonLabel;
        } else {
            mPositiveButtonLabel = context.getString(R.string.commit);
        }
    }

    /**
     * Creates a new instance of SimpleInputDialog.
     * @param context The context where this dialog belongs
     * @param activity The activity where this dialog belongs
     * @param prompt The prompt message to show in the dialog
     * @param defaultInput The default text to be shown in the input field
     * @param positiveButtonLabel The label of the positive button. If empty, a default label will be used
     */
    public SimpleInputDialog(Context context, Activity activity, @NonNull String prompt, String defaultInput, @NonNull String positiveButtonLabel) {
        mContext = context;
        mActivity = activity;
        mDefaultInput = defaultInput;
        mPrompt = prompt;
        if (!positiveButtonLabel.equals("")) {
            mPositiveButtonLabel = positiveButtonLabel;
        } else {
            mPositiveButtonLabel = context.getString(R.string.commit);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Dialog d = getDialog();
        if (d != null && d.getWindow() != null) {
            d.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
            // Make the window transparent to set the round corner background drawable without
            // background artifacts
            d.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }
        mBinding = DialogSimpleInputBinding.inflate(inflater, container, false);
        return mBinding.getRoot();
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mPositiveTextView = mBinding.simpleInputDialogPositiveButton;
        mNegativeTextView = mBinding.simpleInputDialogNegativeButton;
        mInputField = mBinding.simpleInputDialogInputField;
        mInputTextInputLayout = mBinding.simpleInputDialogTextInputLayout;

        mPositiveTextView.setText(mPositiveButtonLabel);

        mInputTextInputLayout.requestFocus();
        mInputTextInputLayout.setHint(mPrompt);

        // Set the text to the input edit text if there is mDefaultInputText
        if (mDefaultInput != null && !mDefaultInput.equals("")) {
            mInputField.setText(mDefaultInput);
            mInputField.selectAll();
        }

        // Show the virtual keyboard
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null)
            imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);

        mPositiveTextView.setOnClickListener(v -> {
            // Add Click
            if (mInputField.getText() != null) {
                if (mListener != null) {
                    String text = mInputField.getText().toString();
                    mListener.onPositiveButtonClick(text);
                }
                dismiss();
            }
        });

        mNegativeTextView.setOnClickListener(v -> {
            // Cancel click
            if (mListener != null)
                mListener.onNegativeButtonClick();
            dismiss();
        });

        // Brighten up the text view to show that it is activated
        mPositiveTextView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mPositiveTextView.setTextColor(getResources().getColor(R.color.colorLightIndigoActivated));
                    break;
                case MotionEvent.ACTION_UP:
                    mPositiveTextView.setTextColor(getResources().getColor(R.color.colorLightIndigoNormal));
                    break;
            }
            return false;
        });

        // Brighten up the text view to show that it is activated
        mNegativeTextView.setOnTouchListener((v, event) -> {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    mNegativeTextView.setTextColor(getResources().getColor(R.color.colorLightIndigoActivated));
                    break;
                case MotionEvent.ACTION_UP:
                    mNegativeTextView.setTextColor(getResources().getColor(R.color.colorLightIndigoNormal));
                    break;
            }
            return false;
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            // Scale the dialog with respect to the screen size
            DisplayMetrics displayMetrics = new DisplayMetrics();
            mActivity.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

            getDialog().getWindow().setLayout(displayMetrics.widthPixels - 100, WindowManager.LayoutParams.WRAP_CONTENT);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Hide the virtual keyboard when exiting the dialog
        InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && !imm.isActive())
            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    }

    /**
     * Register a callback to be invoked when a button in the dialog is clicked
     * @param listener The callback that will be run
     */
    public void setButtonClickListener(ButtonClickListener listener) {
        mListener = listener;
    }

    /**
     * Interface definition for a callback to be invoked when a button in the dialog is clicked
     */
    public interface ButtonClickListener {
        /**
         * Called when positive button in the dialog is clicked
         * @param inputText The text in the input field
         */
        void onPositiveButtonClick(String inputText);
        /**
         * Called when negative button in the dialog is clicked
         */
        void onNegativeButtonClick();
    }
}