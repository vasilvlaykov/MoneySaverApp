package com.example.reactiontest;

import androidx.annotation.Nullable;
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

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFirstSetup extends AppCompatActivity {
    EditText name;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userID;
    String userName;
    Button button;
    static Map<String, Object> user = new HashMap<>();
    CircleImageView profilePic;
    RelativeLayout relativeLayout;
    boolean isImageSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_first_setup);

        startupFade();

        name = findViewById(R.id.user_name);
        button = findViewById(R.id.buttonPnext);
        profilePic = findViewById(R.id.profile_image_edit);
        relativeLayout = findViewById(R.id.relativeLayout1);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        userID = fAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(ProfileFirstSetup.this, IncomeFirstSetup.class);

                userName = name.getText().toString();
                DocumentReference documentReference = fStore.collection("users").document(userID);
                ProfileFirstSetup.user.put("uName", userName);
                documentReference.set(ProfileFirstSetup.user);

                Pair pair = new Pair<View, String>(relativeLayout, ViewCompat.getTransitionName(relativeLayout));

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(ProfileFirstSetup.this, pair);

                startActivity(intent, options.toBundle());
            }
        });
    }

    public void changeProfilePic(View view) {
        Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(openGalleryIntent, 1000);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1000) {
            if (resultCode == Activity.RESULT_OK) {
                Uri imageUri = data.getData();
                //profilePic.setImageURI(imageUri);

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
                        Picasso.get().load(uri).into(profilePic);
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