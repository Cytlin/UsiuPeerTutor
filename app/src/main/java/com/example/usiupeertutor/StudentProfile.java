package com.example.usiupeertutor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class StudentProfile extends AppCompatActivity {
    TextView profileName, profileEmail, profileNumber;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView nav_view;
    ImageView profileImage;
    Button changeProfile;
    StorageReference storageReference;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_profile);
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userId= fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();
        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid() +"/profile.jpeg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);

            }
        });

        profileName= findViewById(R.id.profileName);
        profileEmail= findViewById(R.id.profileEmail);
        profileNumber= findViewById(R.id.profileNumber);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        nav_view=findViewById(R.id.nav_view);
        profileImage= findViewById(R.id.profileImage);
        changeProfile= findViewById(R.id.changeProfile);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DocumentReference documentReference = fStore.collection("Users").document(userId);
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                profileName.setText(documentSnapshot.getString("FullName"));
                profileEmail.setText(documentSnapshot.getString("UserEmail"));
                profileNumber.setText(documentSnapshot.getString("PhoneNumber"));
            }
        });

        changeProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                /*Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);*/
                Intent  i = new Intent(view.getContext(),EditStudentProfile.class);
                i.putExtra("fullName", profileName.getText().toString());
                i.putExtra("email", profileEmail.getText().toString());
                i.putExtra("phone", profileNumber.getText().toString());
                startActivity(i);
            }
        });
    }


    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.nav_profile){
                    Toast.makeText(StudentProfile.this, "Your Profile", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(StudentProfile.this, TutorProfile.class);
                    startActivity(intent1);
                }
                if (id == R.id.nav_changePass){
                    Toast.makeText(StudentProfile.this, "Change Password", Toast.LENGTH_LONG).show();
                    //Intent intent1 = new Intent(TuteeActivity.this,TutorActivity.class);
                    //startActivity(intent1);
                    EditText resetPassword = new EditText(StudentProfile.this);
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(StudentProfile.this);
                    passwordResetDialog.setTitle("Reset Password?");
                    passwordResetDialog.setMessage("Enter your new password ");
                    passwordResetDialog.setView(resetPassword);
                    passwordResetDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //extract email and send reset link
                            String newPass= resetPassword.getText().toString();
                            user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(StudentProfile.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(StudentProfile.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
                                }
                            });


                        }
                    });
                    passwordResetDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            //close the dialog

                        }
                    });
                    passwordResetDialog.create().show();
                }
                if(id==R.id.nav_logout){
                    Toast.makeText(StudentProfile.this, "Log out", Toast.LENGTH_LONG).show();
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(getApplicationContext(),Login.class));
                    finish();
                }

                return true;
            }
        });

        if (actionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}