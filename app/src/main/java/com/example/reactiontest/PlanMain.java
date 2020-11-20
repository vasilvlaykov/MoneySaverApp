package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import javax.annotation.Nullable;

public class PlanMain extends AppCompatActivity {
    TextView currency;
    TextView saveMonthly;
    TextView days;
    RelativeLayout rl1;
    RelativeLayout rl2;
    RelativeLayout rl3;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button button;
    ScrollView scrollView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_plan_main);

        startupFade();

        currency = findViewById(R.id.curr_sign);
        saveMonthly = findViewById(R.id.u_save_monthly);
        days = findViewById(R.id.days_to_goal);
        rl1 = findViewById(R.id.rest_improve);
        rl2 = findViewById(R.id.vac_improve);
        rl3 = findViewById(R.id.cloth_improve);
        button = findViewById(R.id.back_to_menu);
        scrollView = findViewById(R.id.scrollbar1);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(PlanMain.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.getString("currency").equals("euro")) {
                        currency.setText("€");
                    } else if (documentSnapshot.getString("currency").equals("usd")) {
                        currency.setText("$");
                    }

                    int a = Integer.parseInt(documentSnapshot.getString("totalExpenses"));
                    int b = Integer.parseInt(documentSnapshot.getString("mIncome"));
                    int res = b - a;
                    String monthlySave = Integer.valueOf(res).toString();

                    double moneyPd = (Integer.parseInt(monthlySave) * 1.0) / 30;
                    int daysRemain = (int) Math.ceil((Integer.parseInt(documentSnapshot.getString("amountToSave")) * 1.0) / moneyPd);
                    String daysToReachGoal = Integer.valueOf(daysRemain).toString();

                    ProfileFirstSetup.user.put("earningsPerDay", Double.valueOf(moneyPd).toString());

                    saveMonthly.setText(monthlySave);
                    days.setText(daysToReachGoal);

                    int tempRest = Integer.parseInt(documentSnapshot.getString("eRestaurants"));
                    int tempVac = Integer.parseInt(documentSnapshot.getString("eHoliday"));
                    int tempCloth = Integer.parseInt(documentSnapshot.getString("eClothing"));

                    if (tempRest > 0) {
                        rl1.setVisibility(View.VISIBLE);
                    }

                    if (tempVac > 0) {
                        rl2.setVisibility(View.VISIBLE);
                    }

                    if (tempCloth > 0) {
                        rl3.setVisibility(View.VISIBLE);
                    }
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(PlanMain.this, ProfileActivity.class);


                DocumentReference documentReference = fStore.collection("users").document(userID);
                ProfileFirstSetup.user.put("monthlySave", saveMonthly.getText().toString());
                ProfileFirstSetup.user.put("daysToReachGoal", days.getText().toString());

                documentReference.update(ProfileFirstSetup.user);


                Pair pair = new Pair<View, String>(scrollView, ViewCompat.getTransitionName(scrollView));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(PlanMain.this, pair);

                startActivity(intent, options.toBundle());
            }
        });

    }

    private void startupFade() {
        Fade fade = new Fade();
        View decor = getWindow().getDecorView();
        fade.excludeTarget(android.R.id.statusBarBackground, true);
        fade.excludeTarget(android.R.id.navigationBarBackground, true);
        fade.excludeTarget(decor.findViewById(R.id.loginBg), true);
        fade.excludeTarget(decor.findViewById(R.id.signupBg), true);
        fade.excludeTarget(decor.findViewById(R.id.profStart), true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }
}