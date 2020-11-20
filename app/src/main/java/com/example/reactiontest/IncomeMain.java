package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Date;

import javax.annotation.Nullable;

public class IncomeMain extends AppCompatActivity {
    SwitchCompat sCurrency;
    SwitchCompat sTarget;
    TextView monthlyIncome;
    TextView goalAmount;
    Button button;
    RelativeLayout relativeLayout;

    String debtOrSave;
    String currency;

    AlertDialog dialog1;
    EditText editText1;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_income_main);

        startupFade();

        sCurrency = findViewById(R.id.switch_currency);
        sTarget = findViewById(R.id.target_switch);
        monthlyIncome = findViewById(R.id.income_text);
        goalAmount = findViewById(R.id.amount_text);
        button = findViewById(R.id.save_income);
        relativeLayout = findViewById(R.id.relativeLayout6);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        dialog1 = new AlertDialog.Builder(this).create();
        editText1 = new EditText(this);
        editText1.setInputType(InputType.TYPE_CLASS_NUMBER);

        dialog1.setTitle(" Edit the amount ");
        dialog1.setView(editText1);

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(IncomeMain.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    monthlyIncome.setText(documentSnapshot.getString("mIncome"));
                    goalAmount.setText(documentSnapshot.getString("amountToSave"));

                    Date date = documentSnapshot.getDate("dateOfReg");


                    int a = Integer.parseInt(documentSnapshot.getString("totalExpenses"));
                    int b = Integer.parseInt(documentSnapshot.getString("mIncome"));
                    int res = b - a;
                    String monthlySave = Integer.valueOf(res).toString();

                    double moneyPd = (Integer.parseInt(monthlySave) * 1.0) / 30;
                    int daysRemain = (int) Math.ceil((Integer.parseInt(documentSnapshot.getString("amountToSave")) * 1.0) / moneyPd);
                    String daysToReachGoal = Integer.valueOf(daysRemain).toString();

                    ProfileFirstSetup.user.put("earningsPerDay", Double.valueOf(moneyPd).toString());
                    ProfileFirstSetup.user.put("monthlySave", monthlySave);


                    if ("usd".equals(documentSnapshot.getString("currency"))) {
                        sCurrency.setChecked(true);
                    } else if ("euro".equals(documentSnapshot.getString("currency"))) {
                        sCurrency.setChecked(false);
                    }

                    if ("save".equals(documentSnapshot.getString("debtOrSave"))) {
                        sTarget.setChecked(true);
                    } else if ("debt".equals(documentSnapshot.getString("debtOrSave"))) {
                        sTarget.setChecked(false);
                    }

                }
            }
        });

        sTarget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sTarget.isChecked()) {
                    debtOrSave = "save";
                } else {
                    debtOrSave = "debt";
                }
            }
        });

        sCurrency.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (sCurrency.isChecked()) {
                    currency = "usd";
                } else {
                    currency = "euro";
                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(IncomeMain.this, ProfileActivity.class);

                if (monthlyIncome.getText().toString().equals("")) {
                    monthlyIncome.setText("0");
                }

                if (goalAmount.getText().toString().equals("")) {
                    goalAmount.setText("0");
                }

                if (sTarget.isChecked()) {
                    debtOrSave = "save";
                } else {
                    debtOrSave = "debt";
                }

                if (sCurrency.isChecked()) {
                    currency = "usd";
                } else {
                    currency = "euro";
                }

                DocumentReference documentReference = fStore.collection("users").document(userID);


                ProfileFirstSetup.user.put("mIncome", monthlyIncome.getText().toString());
                ProfileFirstSetup.user.put("amountToSave", goalAmount.getText().toString());
                ProfileFirstSetup.user.put("currency", currency);
                ProfileFirstSetup.user.put("debtOrSave", debtOrSave);

                documentReference.update(ProfileFirstSetup.user);

                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(IncomeMain.this, pair);

                startActivity(intent, options.toBundle());
            }
        });

    }


    public void editMonthlyIncome(View view) {
        editText1.setText(monthlyIncome.getText());

        dialog1.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                monthlyIncome.setText(editText1.getText());
            }
        });

        dialog1.show();
    }

    public void editAmount(View view) {
        editText1.setText(goalAmount.getText());

        dialog1.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                goalAmount.setText(editText1.getText());
            }
        });

        dialog1.show();
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