package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class IncomeFirstSetup extends AppCompatActivity {
    SwitchCompat switchTarget;
    SwitchCompat switchCurrency;
    EditText mIncome;
    EditText targetAmount;
    RelativeLayout rl4;
    TextView tv1;
    static String monthlyIncome;
    String currency;
    String debtOrSave;
    static String amountToSave;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    Button button;
    RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_first_setup);

        startupFade();

        switchTarget = findViewById(R.id.target_switch);
        switchCurrency = findViewById(R.id.switch_currency);
        mIncome = findViewById(R.id.monthly_income);
        targetAmount = findViewById(R.id.target_amount);
        rl4 = findViewById(R.id.p4);
        tv1 = findViewById(R.id.debt_or);
        button = findViewById(R.id.buttonInext);
        relativeLayout = findViewById(R.id.relativeLayout2);


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();



        if (switchTarget.isChecked()) {
            tv1.setText("Amount");
            debtOrSave = "save";
        } else {
            tv1.setText("Total Debt");
            debtOrSave = "debt";
        }

        if (switchCurrency.isChecked()) {
            currency = "usd";
        } else {
            currency = "euro";
        }

        switchTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchTarget.isChecked()) {
                    tv1.setText("Amount");
                    debtOrSave = "save";
                } else {
                    tv1.setText("Total Debt");
                    debtOrSave = "debt";
                }
            }
        });

        switchCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (switchCurrency.isChecked()) {
                    currency = "usd";
                } else {
                    currency = "euro";
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeFirstSetup.this, ExpensesFirstSetup.class);
                DocumentReference documentReference = fStore.collection("users").document(userID);

                monthlyIncome = mIncome.getText().toString();
                amountToSave = targetAmount.getText().toString();
                ProfileFirstSetup.user.put("mIncome", monthlyIncome);
                ProfileFirstSetup.user.put("amountToSave", amountToSave);
                ProfileFirstSetup.user.put("currency", currency);
                ProfileFirstSetup.user.put("debtOrSave", debtOrSave);
                documentReference.update(ProfileFirstSetup.user);


                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(IncomeFirstSetup.this, pair);

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
        fade.excludeTarget(decor.findViewById(R.id.signupBg), true);
        fade.excludeTarget(decor.findViewById(R.id.profStart), true);

        getWindow().setEnterTransition(fade);
        getWindow().setExitTransition(fade);
    }
}