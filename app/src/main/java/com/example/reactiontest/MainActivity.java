package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.view.ViewCompat;

import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    ImageView money;
    Button login;
    Button sign;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        money = findViewById(R.id.moneyImg);
        login = findViewById(R.id.buttonL);
        sign = findViewById(R.id.buttonR);
        fAuth = FirebaseAuth.getInstance();

        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.mainBg), true);
        fade.excludeTarget(decor.findViewById(R.id.loginBg), true);
        fade.excludeTarget(decor.findViewById(R.id.signupBg), true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);

//        if (fAuth.getCurrentUser() != null) {
//            Intent intent = new Intent(MainActivity.this, ActivateEmail.class);
//            startActivity(intent);
//            finish();
//        }

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, money, ViewCompat.getTransitionName(money));
                startActivity(intent, options.toBundle());
            }
        });

        sign.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, SignupActivity.class);
                ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this, money, ViewCompat.getTransitionName(money));
                startActivity(intent, options.toBundle());
            }
        });
    }
}