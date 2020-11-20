package com.example.reactiontest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;

public class PassReset extends AppCompatActivity {
    Button button;
    EditText mEmail;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pass_reset);

        startupFade();

        button = findViewById(R.id.buttonReset);
        mEmail = findViewById(R.id.emailTextReset);
        fAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                fAuth.sendPasswordResetEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(PassReset.this, "Password reset link sent!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(PassReset.this, LoginActivity.class);

                        Pair pair = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

                        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PassReset.this, pair);

                        startActivity(intent, options.toBundle());
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(PassReset.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        Intent intent = new Intent(PassReset.this, LoginActivity.class);

        Pair pair = new Pair<View, String>(button, ViewCompat.getTransitionName(button));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PassReset.this, pair);

        startActivity(intent, options.toBundle());
        finish();
    }
}