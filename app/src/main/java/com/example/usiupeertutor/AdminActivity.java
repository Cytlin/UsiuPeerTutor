package com.example.usiupeertutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class AdminActivity extends AppCompatActivity {
    TextView textView3;
    Button button, button2;
    DrawerLayout drawerLayout;
    ActionBarDrawerToggle actionBarDrawerToggle;
    NavigationView nav_view;
    FirebaseAuth fAuth;
    //FirebaseFirestore fStore;
    FirebaseUser user;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        fAuth= FirebaseAuth.getInstance();
        //fStore= FirebaseFirestore.getInstance();
        userId= fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);
        textView3=findViewById(R.id.textView3);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        nav_view=findViewById(R.id.nav_view);

        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent1 = new Intent(AdminActivity.this, ReportActivity.class);
                startActivity(intent1);
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent2 = new Intent(AdminActivity.this, UserListActivity.class);
                startActivity(intent2);
            }
        });
    }
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id=item.getItemId();
                if (id == R.id.nav_users){
                    Toast.makeText(AdminActivity.this, "Users", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(AdminActivity.this, UserListActivity.class);
                    startActivity(intent1);
                }
                if (id == R.id.nav_report){
                    Toast.makeText(AdminActivity.this, "Generating report...", Toast.LENGTH_LONG).show();
                    Intent intent1 = new Intent(AdminActivity.this, ReportActivity.class);
                    startActivity(intent1);
                }
                if (id == R.id.nav_changePass){
                    Toast.makeText(AdminActivity.this, "Change Password", Toast.LENGTH_LONG).show();
                    //Intent intent1 = new Intent(TuteeActivity.this,TutorActivity.class);
                    //startActivity(intent1);
                    EditText resetPassword = new EditText(AdminActivity.this);
                    AlertDialog.Builder passwordResetDialog = new AlertDialog.Builder(AdminActivity.this);
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
                                    Toast.makeText(AdminActivity.this, "Password Reset Successfully", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(AdminActivity.this, "Password Reset Failed", Toast.LENGTH_SHORT).show();
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
                    Toast.makeText(AdminActivity.this, "Log out", Toast.LENGTH_LONG).show();
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