package com.example.reactiontest;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.ViewCompat;

import android.app.Activity;
import android.app.ActivityOptions;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.transition.Fade;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executor;

import javax.annotation.Nullable;

import de.hdodenhof.circleimageview.CircleImageView;

import static com.example.reactiontest.ProfileFirstSetup.user;

public class ProfileMain extends AppCompatActivity {
    TextView welcome;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    String userID;
    StorageReference storageReference;
    String uName;
    String theNewName;
    RelativeLayout relativeLayout;

    EditText newName;
    CircleImageView picProf;
    Button logout;
    Button saveAndBack;
    Button resetProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_main);

        startupFade();

        welcome = findViewById(R.id.welcome);
        newName = findViewById(R.id.new_name);
        picProf = findViewById(R.id.profile_image_edit);
        logout = findViewById(R.id.logout_button);
        saveAndBack = findViewById(R.id.save_button);
        resetProfile = findViewById(R.id.reset_profile);
        relativeLayout = findViewById(R.id.relativeLayout5);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        DocumentReference documentReference = fStore.collection("users").document(userID);
        documentReference.addSnapshotListener(ProfileMain.this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                if (documentSnapshot != null) {
                    uName = documentSnapshot.getString("uName");
                    welcome.setText("Hi " + uName);
                }
            }
        });

        StorageReference profileRef = storageReference.child("users/" + userID + "/profile.jpg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(picProf);
            }
        });

        saveAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileMain.this, ProfileActivity.class);

                if (!newName.getText().toString().equals("")) {
                    theNewName = newName.getText().toString();
                    DocumentReference documentReference = fStore.collection("users").document(userID);
                    ProfileFirstSetup.user.put("uName", theNewName);
                    documentReference.update(ProfileFirstSetup.user);
                }
                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileMain.this, pair);

                startActivity(intent, options.toBundle());
            }
        });

        resetProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileMain.this, ProfileFirstSetup.class);
                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileMain.this, pair);

                startActivity(intent, options.toBundle());
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fAuth.signOut();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileMain.this, pair);

                startActivity(intent, options.toBundle());

            }
        });

    }

    public void editPic(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @androidx.annotation.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();

                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        final StorageReference fileRef = storageReference.child("users/" + userID + "/profile.jpg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(picProf);
                    }
                });
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