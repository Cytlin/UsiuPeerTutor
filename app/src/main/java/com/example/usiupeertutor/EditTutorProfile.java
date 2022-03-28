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
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;
import java.util.Map;

import static android.content.ContentValues.TAG;

public class EditTutorProfile extends AppCompatActivity {
    EditText profileName, profileEmail, profileNumber, profileSkill;
    ImageView profileImage;
    Button saveBtn;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    String userId;
    FirebaseUser user;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView nav_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_tutor_profile);
        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userId= fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        storageReference= FirebaseStorage.getInstance().getReference();

        profileName= findViewById(R.id.profileName);
        profileEmail= findViewById(R.id.profileEmail);
        profileNumber= findViewById(R.id.profileNumber);
        profileSkill= findViewById(R.id.profileSkill);
        profileImage= findViewById(R.id.profileImage);
        saveBtn= findViewById(R.id.saveBtn);

        StorageReference profileRef= storageReference.child("users/"+fAuth.getCurrentUser().getUid() +"/profile.jpeg");
        profileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Picasso.get().load(uri).into(profileImage);

            }
        });

        profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //open gallery
                Intent openGalleryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(openGalleryIntent, 1000);
                Toast.makeText(EditTutorProfile.this, "Profile Image Clicked", Toast.LENGTH_SHORT).show();
            }
        });

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(profileName.getText().toString().isEmpty()||profileEmail.getText().toString().isEmpty()||profileNumber.getText().toString().isEmpty()||profileSkill.getText().toString().isEmpty()){
                    Toast.makeText(EditTutorProfile.this, "Fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                String email =profileEmail.getText().toString();
                user.updateEmail(email).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        DocumentReference docref= fStore.collection("Users").document(user.getUid());
                        Map<String,Object> edited = new HashMap<>();
                        edited.put("UserEmail", email);
                        edited.put("FullName", profileName.getText().toString());
                        edited.put("PhoneNumber", profileNumber.getText().toString());
                        edited.put("SkillSet", profileSkill.getText().toString());
                        docref.update(edited).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(EditTutorProfile.this, "Profile Updated", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(getApplicationContext(),TutorProfile.class));
                                finish();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(EditTutorProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                        Toast.makeText(EditTutorProfile.this, "Email Updated", Toast.LENGTH_SHORT).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(EditTutorProfile.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

            }
        });

        // drawer layout instance to toggle the menu icon to open
        // drawer and back button to close drawer
        drawerLayout= findViewById(R.id.my_drawer_layout);
        nav_view=findViewById(R.id.nav_view);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        // pass the Open and Close toggle for the drawer layout listener
        // to toggle the button
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        // to make the Navigation drawer icon always appear on the action bar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Intent data = getIntent();
        String fullName=data.getStringExtra("fullName");
        String email=data.getStringExtra("email");
        String phone=data.getStringExtra("phone");
        String skill =data.getStringExtra("skill");

        profileName.setText(fullName);
        profileEmail.setText(email);
        profileNumber.setText(phone);
        profileSkill.setText(skill);

        Log.d(TAG, "onCreate"+ fullName + " " + email + " " + phone+ " " + skill);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==1000){
            if(resultCode== Activity.RESULT_OK){
                Uri imageUri = data.getData();
                //profileImage.setImageURI(imageUri);
                uploadImageToFirebase(imageUri);
            }
        }
    }

    private void uploadImageToFirebase(Uri imageUri) {
        //upload image firebase storage
        final StorageReference fileRef = storageReference.child("users/"+fAuth.getCurrentUser().getUid() +"/profile.jpeg");
        fileRef.putFile(imageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                fileRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Picasso.get().load(uri).into(profileImage);
                        Toast.makeText(EditTutorProfile.this, "Profile photo updated", Toast.LENGTH_SHORT).show();
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(EditTutorProfile.this, "Upload Fail", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // override the onOptionsItemSelected()
    // function to implement
    // the item click listener callback
    // to open and close the navigation
    // drawer when the icon is clicked
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.nav_profile){
                    Toast.makeText(EditTutorProfile.this, "Your Profile", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(EditTutorProfile.this, StudentProfile.class);
                    startActivity(intent1);
                }
                if (id == R.id.nav_changePass){
                    Toast.makeText(EditTutorProfile.this, "Change Password", Toast.LENGTH_LONG).show();
                    //Intent intent1 = new Intent(TuteeActivity.this,TutorActivity.class);
                    //startActivity(intent1);
                    EditText resetPassword = new EditText(EditTutorProfile.this);
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(EditTutorProfile.this);
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
                                    Toast.makeText(EditTutorProfile.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(EditTutorProfile.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
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
                if(id==R.id.nav_chat){
                    Toast.makeText(EditTutorProfile.this, "Chats", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(getApplicationContext(),Users.class));
                    finish();
                }
                if(id==R.id.nav_logout){
                    Toast.makeText(EditTutorProfile.this, "Log out", Toast.LENGTH_LONG).show();
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