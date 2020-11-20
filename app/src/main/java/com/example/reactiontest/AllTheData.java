package com.example.reactiontest;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

public class AllTheData {
    public static String uName;
    public static String currency;
    public static String monthlyIncome;
    public static String debtOrSave;
    public static String targetAmount;
    public static String ex1;
    public static String ex2;
    public static String ex3;
    public static String ex4;
    public static String ex5;
    public static String ex6;
    public static String ex7;
    public static String ex8;
    public static String ex9;
    public static String ex10;
    public static String ex11;
    public static String totalExpenses;
    public static String monthlySave;
    public static String totalDays;

     FirebaseAuth fAuth;
     FirebaseFirestore fStore;
     String userID;

      {
        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener((Executor) this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    uName = documentSnapshot.getString("uName");
                    currency = documentSnapshot.getString("currency");
                    monthlyIncome = documentSnapshot.getString("mIncome");
                    debtOrSave = documentSnapshot.getString("debtOrSave");
                    targetAmount = documentSnapshot.getString("amountToSave");
                    ex1 = documentSnapshot.getString("eRent;");
                    ex2 = documentSnapshot.getString("eGroceries;");
                    ex3 = documentSnapshot.getString("eTransport;");
                    ex4 = documentSnapshot.getString("eUtilities;");
                    ex5 = documentSnapshot.getString("eMedical;");
                    ex6 = documentSnapshot.getString("eLoans;");
                    ex7 = documentSnapshot.getString("eClothing;");
                    ex8 = documentSnapshot.getString("eRestaurants;");
                    ex9 = documentSnapshot.getString("eEntertainment;");
                    ex10 = documentSnapshot.getString("eHoliday");
                    ex11 = documentSnapshot.getString("eOthers");
                    totalExpenses = documentSnapshot.getString("totalExpenses");

                }
            }
        });

          int a = Integer.parseInt(totalExpenses);
          int b = Integer.parseInt(IncomeFirstSetup.monthlyIncome);
          int res = b - a;
          monthlySave = Integer.valueOf(res).toString();

          int moneyPd = (int) Math.ceil((Integer.parseInt(monthlySave) * 1.0) / 30);
          int daysRemain = (int) Math.ceil((Integer.parseInt(IncomeFirstSetup.amountToSave) * 1.0) / moneyPd);
          totalDays = Integer.valueOf(daysRemain).toString();
    }
}
