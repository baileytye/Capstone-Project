package com.bowtye.decisive.ui.signUp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.bowtye.decisive.R;
import com.bowtye.decisive.ui.main.MainActivity;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

public class SignUpActivity extends AppCompatActivity {

    @BindView(R.id.bt_already_have_account)
    Button mHaveAccountButton;
    @BindView(R.id.et_email)
    EditText mEmailEditText;
    @BindView(R.id.et_password)
    EditText mPasswordEditText;
    @BindView(R.id.bt_sign_up)
    Button mSignUpButton;

    FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        ButterKnife.bind(this);
        setStatusBarColor();

        mAuth = FirebaseAuth.getInstance();

        mHaveAccountButton.setOnClickListener(view -> finish());
        mSignUpButton.setOnClickListener(view -> signUp());

    }

    private void signUp(){
        String email = mEmailEditText.getText().toString().trim();
        String password = mPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email)) {
            Toast.makeText(getApplicationContext(), R.string.dialog_enter_email_address, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(password)) {
            Toast.makeText(getApplicationContext(), R.string.dialog_enter_password, Toast.LENGTH_SHORT).show();
            return;
        }

        if (password.length() < 6) {
            Toast.makeText(getApplicationContext(), R.string.dialog_password_minimum_length_error, Toast.LENGTH_SHORT).show();
            return;
        }

        //create user
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Timber.d("Creating user failed!");
                        if(task.getException() != null) {
                            Toast.makeText(this, "User creation failed: " + task.getException().getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Timber.d("Creating user with email: %s", Objects.requireNonNull(Objects.requireNonNull(task.getResult()).getAdditionalUserInfo()).getUsername());
                        startActivity(new Intent(this, MainActivity.class));
                        MaterialShowcaseView.resetAll(Objects.requireNonNull(this));
                        finish();
                    }
                });

    }

    private void setStatusBarColor(){
        Window window = this.getWindow();

        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);

        window.setStatusBarColor(ContextCompat.getColor(this,R.color.colorPrimaryDark));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            final int lFlags = window.getDecorView().getSystemUiVisibility();
            window.getDecorView().setSystemUiVisibility(lFlags & ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
    }

}
