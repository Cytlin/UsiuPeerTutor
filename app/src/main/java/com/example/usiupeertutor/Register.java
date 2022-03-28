package com.example.usiupeertutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class Register extends AppCompatActivity {
    EditText fullName,email,password,phone, skillSet;
    Button registerBtn,goToLogin;
    boolean valid = true;
    FirebaseAuth fAuth;
    FirebaseFirestore fStore;
    CheckBox isTutorBox, isStudentBox;
    String userId;
    FirebaseUser user;
    //realtime
    FirebaseDatabase database;
    DatabaseReference myref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        fAuth= FirebaseAuth.getInstance();
        fStore= FirebaseFirestore.getInstance();
        userId= fAuth.getCurrentUser().getUid();
        user = fAuth.getCurrentUser();

        //realtime
        database= FirebaseDatabase.getInstance();
        myref= database.getReference();

        fullName = findViewById(R.id.registerName);
        email = findViewById(R.id.registerEmail);
        password = findViewById(R.id.registerPassword);
        phone = findViewById(R.id.registerPhone);
        registerBtn = findViewById(R.id.registerBtn);
        goToLogin = findViewById(R.id.gotoLogin);
        isTutorBox= findViewById(R.id.isTutor);
        isStudentBox= findViewById(R.id.isStudent);
        skillSet=findViewById(R.id.skillSet);

        //checkbox logic
        isStudentBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    isTutorBox.setChecked(false);
                }
            }
        });
        isTutorBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (compoundButton.isChecked()){
                    isStudentBox.setChecked(false);
                }
            }
        });

        registerBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkField(fullName);
                checkField(email);
                checkField(password);
                checkField(phone);

                //checkbox validation
                if(!(isStudentBox.isChecked()||isTutorBox.isChecked())) {
                    Toast.makeText(Register.this, "Select the Account", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (valid){
                    //Start registration process
                    fAuth.createUserWithEmailAndPassword(email.getText().toString(),password.getText().toString()).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            FirebaseUser user= fAuth.getCurrentUser();
                            Toast.makeText(Register.this, "Account Created", Toast.LENGTH_SHORT).show();
                            DocumentReference df= fStore.collection("Users").document(user.getUid());
                            Map<String,Object> userInfo= new HashMap<>();
                            userInfo.put("FullName",fullName.getText().toString());
                            userInfo.put("UserEmail",email.getText().toString());
                            userInfo.put("PhoneNumber",phone.getText().toString());
                            userInfo.put("SkillSet",skillSet.getText().toString());
                            //specify if the user is admin
                            //userInfo.put("isAdmin", "0");
                            //userInfo.put("isUser", "1");



                            if(isTutorBox.isChecked()){
                                userInfo.put("isTutor","1");
                            }
                            if(isStudentBox.isChecked()){
                                userInfo.put("isStudent", "1");
                            }
                            df.set(userInfo);

                            //Save to realtime
                            String userName=fullName.getText().toString();
                            String userEmail=email.getText().toString();
                            String userPhone=phone.getText().toString();
                            String userSkillSet=skillSet.getText().toString();
                            //.child(user.getUid())
                            myref.child("Users").child(userId).setValue(userName).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Toast.makeText(Register.this, "Saved to realtime successfully", Toast.LENGTH_SHORT).show();
                                    }else{
                                        Toast.makeText(Register.this, "Saved to realtime failed", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });


                            if(isTutorBox.isChecked()){
                                startActivity(new Intent(getApplicationContext(), TutorProfile.class));
                                finish();
                            }
                            if(isStudentBox.isChecked()){
                                startActivity(new Intent(getApplicationContext(), StudentActivity.class));
                                finish();
                            }
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Register.this, "Failed to Create Account", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(),Login.class));
            }
        });
    }
    public boolean checkField(EditText textField){
        if(textField.getText().toString().isEmpty()){
            textField.setError("Error");
            valid = false;
        }else {
            valid = true;
        }

        return valid;
    }
}