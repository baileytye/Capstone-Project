package com.bowtye.decisive.ui.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bowtye.decisive.R;
import com.bowtye.decisive.utils.ViewUtils;
import com.google.firebase.auth.FirebaseAuth;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

public class ForgotPasswordActivity extends AppCompatActivity {

    @BindView(R.id.et_email)
    EditText mEmailEditText;
    @BindView(R.id.bt_reset_password)
    Button mSendForgotPasswordEmailButton;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    String mEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        ButterKnife.bind(this);

        setStatusBarColor();

        mSendForgotPasswordEmailButton.setOnClickListener(view -> {
            ViewUtils.hideKeyboardFrom(this, mSendForgotPasswordEmailButton);
            mEmailEditText.clearFocus();
            sendResetEmail();
        });

    }

    private void sendResetEmail(){
        if(getAndCheckEmail()) {

            setIsLoading(true);
            FirebaseAuth.getInstance().sendPasswordResetEmail(mEmail)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            Timber.d("Sent reset password email");
                            Toast.makeText(this, R.string.dialog_email_sent, Toast.LENGTH_SHORT).show();
                            setIsLoading(false);
                            finish();
                        } else {
                            Timber.d("Reset password failed");
                            Toast.makeText(this, R.string.dialog_reset_password_failed, Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void setIsLoading(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    private boolean getAndCheckEmail(){
        mEmail = mEmailEditText.getText().toString();

        if(mEmail.equals("")){
            Toast.makeText(this, R.string.dialog_enter_email, Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void setStatusBarColor() {
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this, R.color.colorPrimaryDark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int lFlags = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
