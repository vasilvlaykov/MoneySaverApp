package com.example.reactiontest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignupActivity extends AppCompatActivity {
    TextView signupText;

    Button button;
    EditText mEmail;
    EditText mPass;
    EditText mPassRepeat;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        startupFade();

        signupText = findViewById(R.id.textView2);
        button = findViewById(R.id.buttonSu);
        mEmail =findViewById(R.id.emailTextS);
        mPass = findViewById(R.id.passTextS);
        mPassRepeat = findViewById(R.id.passTextSRepeat);
        fAuth = FirebaseAuth.getInstance();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();
                String passR = mPassRepeat.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    if (TextUtils.isEmpty(pass)) {
                        mPass.setError("Password is Required");
                        if (TextUtils.isEmpty(passR)) {
                            mPassRepeat.setError("Repeat Password");
                        }
                    }
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is Required");
                    if (TextUtils.isEmpty(passR)) {
                        mPassRepeat.setError("Repeat Password");
                    }
                    return;
                }

                if (TextUtils.isEmpty(passR)) {
                    mPassRepeat.setError("Repeat Password");
                    return;
                }

                if (pass.length() < 6) {
                    mPass.setError("Password must be 6 or more characters");
                    return;
                }

                if (!pass.equals(passR)) {
                    mPassRepeat.setError("Passwords do not match");
                    return;
                }

                fAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            //email verification *****************************************************

                            FirebaseUser currentUser = fAuth.getCurrentUser();
                            currentUser.sendEmailVerification();

                            //************************************************************************

                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);

                            Toast.makeText(SignupActivity.this, "User Created", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(SignupActivity.this, ActivateEmail.class);
                            startActivity(intent, options.toBundle());
                            finish();
                        } else {
                            Toast.makeText(SignupActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });




            }
        });

    }

    private void startupFade() {
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.mainBg), true);
        fade.excludeTarget(decor.findViewById(R.id.loginBg), true);
        fade.excludeTarget(decor.findViewById(R.id.signupBg), true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    public void goToLogin(View view) {
        Intent intent = new Intent(SignupActivity.this, LoginActivity.class);

        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(signupText, ViewCompat.getTransitionName(signupText));
        pairs[1] = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(SignupActivity.this, pairs);

        startActivity(intent, options.toBundle());
        finish();
    }
}