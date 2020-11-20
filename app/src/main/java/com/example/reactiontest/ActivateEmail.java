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
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ActivateEmail extends AppCompatActivity {
    Button button;
    Button buttonLogout;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_activate_email);

        startupFade();

        button = findViewById(R.id.buttonRefresh);
        buttonLogout = findViewById(R.id.buttonLogout);
        fAuth = FirebaseAuth.getInstance();

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (fAuth.getCurrentUser().isEmailVerified()) {
                    Intent intent = new Intent(ActivateEmail.this, ProfileFirstSetup.class);
                    startActivity(intent);
                } else {
                    Toast.makeText(ActivateEmail.this, "Email not verified!", Toast.LENGTH_SHORT).show();
                    fAuth.getCurrentUser().reload();
                }
            }
        });

        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);

                Pair[] pairs = new Pair[2];
                pairs[0] = new Pair<View, String>(button, "login_transition");
                pairs[1] = new Pair<View, String>(buttonLogout, "sign_transition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ActivateEmail.this, pairs);

                startActivity(intent, options.toBundle());
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

    public void resendEmail(View view) {
        fAuth.getCurrentUser().sendEmailVerification().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Toast.makeText(ActivateEmail.this, "Email verification link sent!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(ActivateEmail.this, "Error! " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }
}