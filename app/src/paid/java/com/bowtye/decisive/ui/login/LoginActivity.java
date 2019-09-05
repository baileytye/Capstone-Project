package com.bowtye.decisive.ui.login;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bowtye.decisive.ui.main.MainActivity;
import com.bowtye.decisive.ui.signUp.SignUpActivity;
import com.bowtye.decisive.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

import static com.bowtye.decisive.ui.main.home.HomeFragment.EXTRA_SIGN_OUT;

public class LoginActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_SIGN_IN = 10;
    public static final int SIGN_OUT = 11;
    private static final int GOOGLE_SIGN_IN = 1;
    private static final int EMAIL_SIGN_IN = 2;

    @BindView(R.id.bt_google_sign_in)
    GoogleSignInButton mGoogleSignInButton;
    @BindView(R.id.bt_continue_offline)
    Button mContinueOfflineButton;
    @BindView(R.id.bt_sign_up)
    Button mSignUpButton;
    @BindView(R.id.bt_sign_in)
    Button mSignInButton;
    @BindView(R.id.et_email)
    EditText mEmailEditText;
    @BindView(R.id.et_password)
    EditText mPasswordEditText;
    @BindView(R.id.progressBar)
    ProgressBar mProgressBar;

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        setStatusBarColor();

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        Intent in = getIntent();
        if (in != null && in.hasExtra(EXTRA_SIGN_OUT)) {
            mEmailEditText.setText(null);
            mPasswordEditText.setText(null);
            mPasswordEditText.clearFocus();
            mEmailEditText.clearFocus();
            signOut();
        }

        mGoogleSignInButton.setOnClickListener(view -> signIn(GOOGLE_SIGN_IN));
        mSignInButton.setOnClickListener(view -> signIn(EMAIL_SIGN_IN));

        mContinueOfflineButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            saveUseOfflineToSharedPref(true);
            startActivity(intent);
            finish();
        });

        mSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        setIsLoading(false);
        // Check if user is signed in (non-null) and update UI accordingly.
        checkAuthAndStartHomeActivity();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(Objects.requireNonNull(account));
            } catch (ApiException e) {
                Timber.w("Google sign in failed %d", e.getStatusCode());
                Toast.makeText(getApplicationContext(), getString(R.string.dialog_sign_in_failed) + e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    public void signIn(int code) {
        if (code == GOOGLE_SIGN_IN) {

            Timber.d("Starting sign in");

            Intent intent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(intent, REQUEST_CODE_SIGN_IN);

        } else if (code == EMAIL_SIGN_IN) {

            String email = mEmailEditText.getText().toString();
            final String password = mPasswordEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), getString(R.string.dialog_enter_email_address), Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), getString(R.string.dialog_enter_password), Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        if (!task.isSuccessful()) {

                            if (password.length() < 6) {
                                mPasswordEditText.setError(getString(R.string.dialog_password_minimum_length_error));
                            } else {
                                Toast.makeText(LoginActivity.this, R.string.dialog_sign_in_failed, Toast.LENGTH_LONG).show();
                            }

                            setIsLoading(false);

                        } else {
                            checkAuthAndStartHomeActivity();
                        }
                    });
            setIsLoading(true);
        }
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

    private void saveUseOfflineToSharedPref(boolean useOffline) {
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(getString(R.string.pref_use_offline_key), useOffline);
        editor.apply();
    }

    private void signOut() {

        saveUseOfflineToSharedPref(false);

        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        if (GoogleSignIn.getLastSignedInAccount(this) != null) {
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> Timber.d("Signed out"));
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Timber.d("firebaseAuthWithGoogle: %s", acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);

        setIsLoading(true);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        Timber.d("signInWithCredential:success");
                        checkAuthAndStartHomeActivity();
                    } else {
                        Timber.w("signInWithCredential:failure %s", Objects.requireNonNull(task.getException()).getMessage());
                        Toast.makeText(LoginActivity.this, getString(R.string.dialog_sign_in_failed_with_message) + task.getException().getLocalizedMessage(), Toast.LENGTH_LONG).show();
                        setIsLoading(false);
                    }
                });
    }

    private void setIsLoading(boolean isLoading) {
        if (isLoading) {
            mProgressBar.setVisibility(View.VISIBLE);

        } else {
            mProgressBar.setVisibility(View.GONE);
        }
    }

    public void checkAuthAndStartHomeActivity() {
        if (mAuth.getCurrentUser() != null) {
            Timber.d("Signed in with user: %s", mAuth.getCurrentUser().getDisplayName());
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, SIGN_OUT);
            finish();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences(
                    getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            if (sharedPreferences.getBoolean(getString(R.string.pref_use_offline_key), false)) {
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }
    }
}
