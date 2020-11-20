package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.annotation.Nullable;

public class ProgressMain extends AppCompatActivity {
    GraphView graphView;
    Button button;
    LinearLayout linearLayout;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_progress_main);

        startupFade();

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        graphView = findViewById(R.id.graph);
        button = findViewById(R.id.back_to_m);
        linearLayout = findViewById(R.id.linearLayout33);


        graphView.getViewport().setScalable(true);
        graphView.getGridLabelRenderer().setHorizontalAxisTitle("Days");
        graphView.getGridLabelRenderer().setHorizontalAxisTitleColor(Color.argb(255,0,163,44));
        graphView.getGridLabelRenderer().setHorizontalAxisTitleTextSize(50);
        graphView.getGridLabelRenderer().setVerticalAxisTitle("Money");
        graphView.getGridLabelRenderer().setVerticalAxisTitleColor(Color.argb(255,0,163,44));
        graphView.getGridLabelRenderer().setVerticalAxisTitleTextSize(50);

        graphView.getViewport().setMinX(20);
        graphView.getViewport().setMaxX(50);
        graphView.getViewport().setMinY(2);
        graphView.getViewport().setMaxY(15);
        graphView.getViewport().setYAxisBoundsManual(true);
        graphView.getViewport().setXAxisBoundsManual(true);


        //final Calendar calendar = Calendar.getInstance();

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(ProgressMain.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {

                    int days = Integer.parseInt(documentSnapshot.getString("daysToReachGoal"));
                    int goal = Integer.parseInt(documentSnapshot.getString("amountToSave"));


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

                    LineGraphSeries<DataPoint> tryStuff = new LineGraphSeries<>();

                    long maximum = (max.getTime() - reg.getTime()) / 86400000;

                    long stop = calendar.getTime().getTime();

                    double count = 0;
                    for (long i = reg.getTime(); i <= today.getTime(); i = i + 86399999) {
                        tryStuff.appendData(new DataPoint(i, count), true, days);
                        count = count + epd;
                        graphView.addSeries(tryStuff);
                    }

//                    int current = (int) Math.floor(count);
//
//                    ProfileFirstSetup.user.put("current", Integer.valueOf(current).toString());

                    documentReference.update(ProfileFirstSetup.user);

                    LineGraphSeries<DataPoint> goals = new LineGraphSeries<DataPoint>(new DataPoint[] {
                            new DataPoint(0, goal),
                            new DataPoint(Long.MAX_VALUE, goal)
                    });
                    goals.setColor(Color.CYAN);
                    graphView.addSeries(goals);


                    graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(ProgressMain.this));
                    graphView.getViewport().setXAxisBoundsManual(true);
                    graphView.getGridLabelRenderer().setNumHorizontalLabels(2);
                    graphView.getViewport().setMinX(reg.getTime());
                    graphView.getViewport().setMaxX(max.getTime());
                    graphView.getViewport().setMaxY(goal + 200);
                    graphView.getGridLabelRenderer().setHumanRounding(false);

                }
            }
        });


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProgressMain.this, ProfileActivity.class);
                documentReference.update(ProfileFirstSetup.user);
                Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProgressMain.this, pair);

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