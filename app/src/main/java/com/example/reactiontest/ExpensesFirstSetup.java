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
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ExpensesFirstSetup extends AppCompatActivity {
    String eRent;
    String eGroceries;
    String eTransport;
    String eUtilities;
    String eMedical;
    String eLoans;
    String eClothing;
    String eRestaurants;
    String eEntertainment;
    String eHoliday;
    String eOthers;
    String totalExpenses;
    String monthlySave;
    String daysToReachGoal;

    EditText rent;
    EditText groceries;
    EditText transport;
    EditText utilities;
    EditText medical;
    EditText loans;
    EditText clothing;
    EditText restaurants;
    EditText entertainment;
    EditText holiday;
    EditText others;

    LinearLayout linearLayout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_first_setup);

        startupFade();

        button = findViewById(R.id.buttonEnext);

        rent = findViewById(R.id.rent);
        groceries = findViewById(R.id.groceries);
        transport = findViewById(R.id.transport);
        utilities = findViewById(R.id.utilities);
        medical = findViewById(R.id.medical);
        loans = findViewById(R.id.loans);
        clothing = findViewById(R.id.clothing);
        restaurants = findViewById(R.id.restaurants);
        entertainment = findViewById(R.id.entertainment);
        holiday = findViewById(R.id.holidays);
        others = findViewById(R.id.others);
        linearLayout = findViewById(R.id.relativeLayout3);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesFirstSetup.this, PlanMain.class);

                if (!rent.getText().toString().equals("")) {
                    eRent = rent.getText().toString();
                } else {
                    eRent = "0";
                }
                if (!groceries.getText().toString().equals("")) {
                    eGroceries = groceries.getText().toString();
                } else {
                    eGroceries = "0";
                }
                if (!transport.getText().toString().equals("")) {
                    eTransport = transport.getText().toString();
                } else {
                    eTransport = "0";
                }
                if (!utilities.getText().toString().equals("")) {
                    eUtilities = utilities.getText().toString();
                } else {
                    eUtilities = "0";
                }
                if (!medical.getText().toString().equals("")) {
                    eMedical = medical.getText().toString();
                } else {
                    eMedical = "0";
                }
                if (!loans.getText().toString().equals("")) {
                    eLoans = loans.getText().toString();
                } else {
                    eLoans = "0";
                }
                if (!clothing.getText().toString().equals("")) {
                    eClothing = clothing.getText().toString();
                } else {
                    eClothing = "0";
                }
                if (!restaurants.getText().toString().equals("")) {
                    eRestaurants = restaurants.getText().toString();
                } else {
                    eRestaurants = "0";
                }
                if (!entertainment.getText().toString().equals("")) {
                    eEntertainment = entertainment.getText().toString();
                } else {
                    eEntertainment = "0";
                }
                if (!holiday.getText().toString().equals("")) {
                    eHoliday = holiday.getText().toString();
                } else {
                    eHoliday = "0";
                }
                if (!others.getText().toString().equals("")) {
                    eOthers = others.getText().toString();
                } else {
                    eOthers = "0";
                }

                totalExpenses = Integer.valueOf(Integer.parseInt(eRent) + Integer.parseInt(eGroceries) + Integer.parseInt(eTransport) + Integer.parseInt(eUtilities) + Integer.parseInt(eMedical)
                        + Integer.parseInt(eLoans) + Integer.parseInt(eClothing) + Integer.parseInt(eRestaurants) + Integer.parseInt(eEntertainment) + Integer.parseInt(eHoliday)
                        + Integer.parseInt(eOthers)).toString();


                int a = Integer.parseInt(totalExpenses);
                int b = Integer.parseInt(IncomeFirstSetup.monthlyIncome);
                int res = b - a;
                monthlySave = Integer.valueOf(res).toString();

                double moneyPd = (Integer.parseInt(monthlySave) * 1.0) / 30;
                int daysRemain = (int) Math.ceil((Integer.parseInt(IncomeFirstSetup.amountToSave) * 1.0) / moneyPd);
                daysToReachGoal = Integer.valueOf(daysRemain).toString();

                Date today = new Date();
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yy");
                String dayOfRegistration = dateFormat.format(today);


                DocumentReference documentReference = fStore.collection("users").document(userID);

                ProfileFirstSetup.user.put("eRent", eRent);
                ProfileFirstSetup.user.put("eGroceries", eGroceries);
                ProfileFirstSetup.user.put("eTransport", eTransport);
                ProfileFirstSetup.user.put("eUtilities", eUtilities);
                ProfileFirstSetup.user.put("eMedical", eMedical);
                ProfileFirstSetup.user.put("eLoans", eLoans);
                ProfileFirstSetup.user.put("eClothing", eClothing);
                ProfileFirstSetup.user.put("eRestaurants", eRestaurants);
                ProfileFirstSetup.user.put("eEntertainment", eEntertainment);
                ProfileFirstSetup.user.put("eHoliday", eHoliday);
                ProfileFirstSetup.user.put("eOthers", eOthers);
                ProfileFirstSetup.user.put("totalExpenses", totalExpenses);
                ProfileFirstSetup.user.put("monthlySave", monthlySave);
                ProfileFirstSetup.user.put("daysToReachGoal", daysToReachGoal);
                ProfileFirstSetup.user.put("isSetup", "yes");
                ProfileFirstSetup.user.put("dateOfReg", today);
                ProfileFirstSetup.user.put("earningsPerDay", Double.valueOf(moneyPd).toString());


                documentReference.update(ProfileFirstSetup.user);

                Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ExpensesFirstSetup.this, pair);

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