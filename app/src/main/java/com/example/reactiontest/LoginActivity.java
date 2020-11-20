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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class LoginActivity extends AppCompatActivity {
    TextView loginText;
    Button button;
    EditText mEmail;
    EditText mPass;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    boolean isVerified;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        startupFade();

        loginText = findViewById(R.id.textView2);
        button = findViewById(R.id.buttonLo);
        mEmail =findViewById(R.id.emailTextL);
        mPass = findViewById(R.id.passTextL);

        isVerified = false;

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
//        userID = fAuth.getCurrentUser().getUid();
//
//
//        DocumentReference documentReference = fStore.collection("users").document(userID);
//        documentReference.addSnapshotListener(LoginActivity.this, new EventListener<DocumentSnapshot>() {
//            @Override
//            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
//                if (documentSnapshot != null) {
//
//                    if (documentSnapshot.getString("isSetup") != null) {
//                        isVerified = true;
//                    }
//
//                }
//            }
//        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = mEmail.getText().toString().trim();
                String pass = mPass.getText().toString().trim();

                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Email is Required");
                    if (TextUtils.isEmpty(pass)) {
                        mPass.setError("Password is Required");
                    }
                    return;
                }

                if (TextUtils.isEmpty(pass)) {
                    mPass.setError("Password is Required");
                    return;
                }

                fAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            userID = fAuth.getCurrentUser().getUid();
                            Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_SHORT).show();



                            Pair[] pairs = new Pair[1];
                            pairs[0] = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

                            final ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                            //******************************************************

                            //******************************************************

                            if (fAuth.getCurrentUser().isEmailVerified()) {
                                DocumentReference documentReference = fStore.collection("users").document(userID);
                                documentReference.addSnapshotListener(LoginActivity.this, new EventListener<DocumentSnapshot>() {
                                    @Override
                                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                                        if (documentSnapshot != null) {

                                            if (documentSnapshot.getString("isSetup") != null) {
                                                Intent intentVerified = new Intent(LoginActivity.this, ProfileActivity.class);
                                                startActivity(intentVerified, options.toBundle());
                                            } else {
                                                Intent firstSetup = new Intent(LoginActivity.this, ProfileFirstSetup.class);
                                                startActivity(firstSetup, options.toBundle());
                                            }
                                        }
                                    }
                                });
                            } else {
                                Intent intent = new Intent(LoginActivity.this, ActivateEmail.class);
                                startActivity(intent, options.toBundle());
                            }
                            //finish();
                        } else {
                            Toast.makeText(LoginActivity.this, "Error! " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
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
        fade.excludeTarget(decor.findViewById(R.id.verifyBg), true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }

    public void goToSignUp(View view) {
        Intent intent = new Intent(LoginActivity.this, SignupActivity.class);

        Pair[] pairs = new Pair[2];
        pairs[0] = new Pair<View, String>(loginText, ViewCompat.getTransitionName(loginText));
        pairs[1] = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);

        startActivity(intent, options.toBundle());

        finish();
    }

    public void forgotPass(View view) {
        Intent intent = new Intent(LoginActivity.this, PassReset.class);

        Pair pair = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pair);

        startActivity(intent, options.toBundle());
    }
}