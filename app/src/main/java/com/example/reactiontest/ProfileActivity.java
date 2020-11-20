package com.example.reactiontest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.Date;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileActivity extends AppCompatActivity {
    TextView name;
    TextView sign1;
    TextView sign2;
    TextView current;
    TextView goal;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    ProgressBar bar;
    CircleImageView profilePicture;
    LinearLayout linearLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        startupFade();

        name = findViewById(R.id.main_name);
        sign1 = findViewById(R.id.euro_l);
        sign2 = findViewById(R.id.euro_text_r);
        current = findViewById(R.id.sum_left);
        goal = findViewById(R.id.sum_right);
        bar = findViewById(R.id.progressBar);
        profilePicture = findViewById(R.id.profile_image);
        linearLayout = findViewById(R.id.linearLayout2);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        try {
            storageReference = FirebaseStorage.getInstance().getReference();
            StorageReference profileRef = storageReference.child("users/" + userID + "/profile.jpg");
            profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Picasso.get().load(uri).into(profilePicture);
                }
            });
        } catch (Exception e) {
            Picasso.get().load(R.drawable.user_profile).into(profilePicture);
        }

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(ProfileActivity.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    if (documentSnapshot.getString("currency").equals("euro")) {
                        sign1.setText("€");
                        sign2.setText("€");
                    } else if (documentSnapshot.getString("currency").equals("usd")) {
                        sign1.setText("$");
                        sign2.setText("$");
                    }


                    //********************************************************************************************************************
                    //********************************************************************************************************************
                    int days = Integer.parseInt(documentSnapshot.getString("daysToReachGoal"));


                    int a = Integer.parseInt(documentSnapshot.getString("totalExpenses"));
                    int b = Integer.parseInt(documentSnapshot.getString("mIncome"));
                    int res = b - a;
                    String monthlySave = Integer.valueOf(res).toString();

                    double moneyPd = (Integer.parseInt(monthlySave) * 1.0) / 30;
                    int daysRemain = (int) Math.ceil((Integer.parseInt(documentSnapshot.getString("amountToSave")) * 1.0) / moneyPd);
                    String daysToReachGoal = Integer.valueOf(daysRemain).toString();

                    ProfileFirstSetup.user.put("earningsPerDay", Double.valueOf(moneyPd).toString());

                    documentReference.update(ProfileFirstSetup.user);


                    double epd = moneyPd;

                    Calendar calendar = Calendar.getInstance();

                    Date reg = documentSnapshot.getDate("dateOfReg");
                    calendar.add(Calendar.DATE, 1);
                    Date today = calendar.getTime();
                    calendar.add(Calendar.DATE, 2);
                    Date max = new Date();
                    max.setTime(reg.getTime() + (days * 86400000));
                    calendar.add(Calendar.DATE, 3);

                    double count = 0;
                    for (long i = reg.getTime() + 86399999; i < today.getTime(); i = i + 86399999) {
                        count = count + epd;
                    }

                    int current1 = (int) Math.floor(count);

                    ProfileFirstSetup.user.put("current", Integer.valueOf(current1).toString());

                    documentReference.update(ProfileFirstSetup.user);
                    //********************************************************************************************************************
                    //********************************************************************************************************************


                    name.setText(documentSnapshot.getString("uName"));
                    goal.setText(documentSnapshot.getString("amountToSave"));
                    current.setText(documentSnapshot.getString("current"));
                    bar.setMax(Integer.parseInt(documentSnapshot.getString("amountToSave")));
                    bar.setProgress(current1);
                }
            }
        });

    }

    public void toProfileEdit(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProfileMain.class);
        Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pair);

        startActivity(intent, options.toBundle());
    }

    public void goToPlan(View view) {
        Intent intent = new Intent(ProfileActivity.this, PlanMain.class);
        Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pair);

        startActivity(intent, options.toBundle());
    }

    public void goToEditIncome(View view) {
        Intent intent = new Intent(ProfileActivity.this, IncomeMain.class);
        Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pair);

        startActivity(intent, options.toBundle());
    }

    public void goToEditExpenses(View view) {
        Intent intent = new Intent(ProfileActivity.this, ExpensesMain.class);
        Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pair);

        startActivity(intent, options.toBundle());
    }

    public void goToProgress(View view) {
        Intent intent = new Intent(ProfileActivity.this, ProgressMain.class);
        Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

        ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileActivity.this, pair);

        startActivity(intent, options.toBundle());
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