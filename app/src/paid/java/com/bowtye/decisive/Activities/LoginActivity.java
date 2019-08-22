package com.bowtye.decisive.Activities;

import androidx.annotation.Nullable;
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
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.shobhitpuri.custombuttons.GoogleSignInButton;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

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

    GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        setStatusBarColor();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();

        mGoogleSignInButton.setOnClickListener(view -> signIn(GOOGLE_SIGN_IN));
        mSignInButton.setOnClickListener(view -> signIn(EMAIL_SIGN_IN));

        mContinueOfflineButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        });

        mSignUpButton.setOnClickListener(view -> {
            Intent intent = new Intent(this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        checkAuth(currentUser);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Timber.w("Google sign in failed %d", e.getStatusCode());
                // ...
            }
        }
        if(requestCode == SIGN_OUT) {
            signOut();
        }
    }

    public void signIn(int code){
        if(code == GOOGLE_SIGN_IN) {
            Intent intent = mGoogleSignInClient.getSignInIntent();
            Timber.d("Starting sign in");
            startActivityForResult(intent, REQUEST_CODE_SIGN_IN);
        } else if(code == EMAIL_SIGN_IN){

            String email = mEmailEditText.getText().toString();
            final String password = mPasswordEditText.getText().toString();

            if (TextUtils.isEmpty(email)) {
                Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                return;
            }

            if (TextUtils.isEmpty(password)) {
                Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                return;
            }

            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(LoginActivity.this, task -> {
                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) {
                            // there was an error
                            if (password.length() < 6) {
                                mPasswordEditText.setError("Password must be 6 characters long");
                            } else {
                                Toast.makeText(LoginActivity.this, "Sign in failed", Toast.LENGTH_LONG).show();
                            }
                        } else {
                            checkAuth(mAuth.getCurrentUser());
                        }
                    });
        }
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

    private void signOut() {
        // Firebase sign out
        FirebaseAuth.getInstance().signOut();

        if(GoogleSignIn.getLastSignedInAccount(this) != null) {
            // Google sign out
            mGoogleSignInClient.signOut().addOnCompleteListener(this,
                    task -> Timber.d("Signed out"));
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Timber.d("firebaseAuthWithGoogle: %s", acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Timber.d("signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        checkAuth(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Timber.w("signInWithCredential:failure %s", Objects.requireNonNull(task.getException()).getMessage());
                        checkAuth(null);
                    }

                    // ...
                });
    }

    public void checkAuth(FirebaseUser user){
        if(user != null) {
            Timber.d("Signed in with user: %s", user.getDisplayName());
            Intent intent = new Intent(this, MainActivity.class);
            startActivityForResult(intent, SIGN_OUT);
        }
    }
}
