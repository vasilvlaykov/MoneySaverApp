package com.example.reactiontest;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.ActivityOptions;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ExpensesMain extends AppCompatActivity {
    TextView tRent;
    TextView tGroc;
    TextView tTrans;
    TextView tUtil;
    TextView tMed;
    TextView tLoans;
    TextView tCloth;
    TextView tRest;
    TextView tEnt;
    TextView tHol;
    TextView tOthers;
    Button button;
    LinearLayout linearLayout;

    AlertDialog dialog;
    EditText editText;

    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expenses_main);

        startupFade();

        tRent = findViewById(R.id.tv_rent);
        tGroc = findViewById(R.id.tv_groc);
        tTrans = findViewById(R.id.tv_trans);
        tUtil = findViewById(R.id.tv_util);
        tMed = findViewById(R.id.tv_med);
        tLoans = findViewById(R.id.tv_loans);
        tCloth = findViewById(R.id.tv_cloth);
        tRest = findViewById(R.id.tv_rest);
        tEnt = findViewById(R.id.tv_ent);
        tHol = findViewById(R.id.tv_hol);
        tOthers = findViewById(R.id.tv_others);
        button = findViewById(R.id.s_b_expenses_main);
        linearLayout = findViewById(R.id.linearLayout9);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();

        dialog = new AlertDialog.Builder(this).create();
        editText = new EditText(this);
        editText.setInputType(InputType.TYPE_CLASS_NUMBER);

        dialog.setTitle(" Edit the amount ");
        dialog.setView(editText);

        final DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(ExpensesMain.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value != null) {
                    tRent.setText(value.getString("eRent"));
                    tGroc.setText(value.getString("eGroceries"));
                    tTrans.setText(value.getString("eTransport"));
                    tUtil.setText(value.getString("eUtilities"));
                    tMed.setText(value.getString("eMedical"));
                    tLoans.setText(value.getString("eLoans"));
                    tCloth.setText(value.getString("eClothing"));
                    tRest.setText(value.getString("eRestaurants"));
                    tEnt.setText(value.getString("eEntertainment"));
                    tHol.setText(value.getString("eHoliday"));
                    tOthers.setText(value.getString("eOthers"));
                }
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ExpensesMain.this, ProfileActivity.class);

                if (tRent.getText().toString().equals("")) {
                    tRent.setText("0");
                }

                if (tGroc.getText().toString().equals("")) {
                    tGroc.setText("0");
                }

                if (tTrans.getText().toString().equals("")) {
                    tTrans.setText("0");
                }

                if (tUtil.getText().toString().equals("")) {
                    tUtil.setText("0");
                }

                if (tMed.getText().toString().equals("")) {
                    tMed.setText("0");
                }

                if (tLoans.getText().toString().equals("")) {
                    tLoans.setText("0");
                }

                if (tCloth.getText().toString().equals("")) {
                    tCloth.setText("0");
                }

                if (tRest.getText().toString().equals("")) {
                    tRest.setText("0");
                }

                if (tEnt.getText().toString().equals("")) {
                    tEnt.setText("0");
                }

                if (tHol.getText().toString().equals("")) {
                    tHol.setText("0");
                }

                if (tOthers.getText().toString().equals("")) {
                    tOthers.setText("0");
                }

                String total = Integer.valueOf(Integer.parseInt(tRent.getText().toString()) + Integer.parseInt(tGroc.getText().toString()) + Integer.parseInt(tTrans.getText().toString()) + Integer.parseInt(tUtil.getText().toString()) +
                        Integer.parseInt(tMed.getText().toString()) + Integer.parseInt(tLoans.getText().toString()) + Integer.parseInt(tCloth.getText().toString()) + Integer.parseInt(tRest.getText().toString()) + Integer.parseInt(tEnt.getText().toString()) +
                        Integer.parseInt(tHol.getText().toString()) + Integer.parseInt(tOthers.getText().toString())).toString();

                ProfileFirstSetup.user.put("eRent", tRent.getText().toString());
                ProfileFirstSetup.user.put("eGroceries", tGroc.getText().toString());
                ProfileFirstSetup.user.put("eTransport", tTrans.getText().toString());
                ProfileFirstSetup.user.put("eUtilities", tUtil.getText().toString());
                ProfileFirstSetup.user.put("eMedical", tMed.getText().toString());
                ProfileFirstSetup.user.put("eLoans", tLoans.getText().toString());
                ProfileFirstSetup.user.put("eClothing", tCloth.getText().toString());
                ProfileFirstSetup.user.put("eRestaurants", tRest.getText().toString());
                ProfileFirstSetup.user.put("eEntertainment", tEnt.getText().toString());
                ProfileFirstSetup.user.put("eHoliday", tHol.getText().toString());
                ProfileFirstSetup.user.put("eOthers", tOthers.getText().toString());
                ProfileFirstSetup.user.put("totalExpenses", total);

                documentReference.update(ProfileFirstSetup.user);

                Pair pair = new Pair<View, String>(linearLayout, ViewCompat.getTransitionName(linearLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ExpensesMain.this, pair);

                startActivity(intent, options.toBundle());
            }
        });

    }

    public void editRent(View view) {
        editText.setText(tRent.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tRent.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editGroc(View view) {
        editText.setText(tGroc.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tGroc.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editTrans(View view) {
        editText.setText(tTrans.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tTrans.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editUtil(View view) {
        editText.setText(tUtil.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tUtil.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editMed(View view) {
        editText.setText(tMed.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tMed.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editLoans(View view) {
        editText.setText(tLoans.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tLoans.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editCloth(View view) {
        editText.setText(tCloth.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tCloth.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editRest(View view) {
        editText.setText(tRest.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tRest.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editEnt(View view) {
        editText.setText(tEnt.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tEnt.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editHol(View view) {
        editText.setText(tHol.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tHol.setText(editText.getText());
            }
        });

        dialog.show();
    }

    public void editOthers(View view) {
        editText.setText(tOthers.getText());

        dialog.setButton(DialogInterface.BUTTON_POSITIVE, "SAVE", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                tOthers.setText(editText.getText());
            }
        });

        dialog.show();
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