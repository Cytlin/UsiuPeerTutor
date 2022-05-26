package com.example.usiupeertutor;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class UserListActivity extends AppCompatActivity {
    FirebaseDatabase firebaseDatabase;
    DatabaseReference dRef;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_list);
        recyclerView= findViewById(R.id.recyclerView);
        firebaseDatabase= FirebaseDatabase.getInstance();
        dRef= firebaseDatabase.getReference("UsersAdmin");
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setHasFixedSize(true);
        loadData(recyclerView);
    }
    public void loadData(RecyclerView rView){
        FirebaseRecyclerAdapter<UserAdmin, UserViewHolder> firebaseRecyclerAdapter=
                new FirebaseRecyclerAdapter<UserAdmin, UserViewHolder>(
                        UserAdmin.class,
                        R.layout.user_list,
                        UserViewHolder.class,
                        dRef

                ) {
            @Override
            protected void populateViewHolder(UserViewHolder userViewHolder, UserAdmin userAdmin, int i) {
                userViewHolder.name.setText(userAdmin.getfName());
                userViewHolder.email.setText(userAdmin.getUserEmail());
                userViewHolder.phone.setText(userAdmin.getUserPhone());
                userViewHolder.skill.setText(userAdmin.getUserSkillSet());

                userViewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {

                        userViewHolder.userUpdateDelete.setVisibility(View.VISIBLE);
                        userViewHolder.update.setVisibility(View.VISIBLE);
                        return false;
                    }
                });

                userViewHolder.update.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        View rootLay = LayoutInflater.from(getApplicationContext()).inflate(R.layout.update, null);
                        EditText update_name=rootLay.findViewById(R.id.up_name);
                        EditText update_email=rootLay.findViewById(R.id.up_email);
                        EditText update_phone=rootLay.findViewById(R.id.up_phone);
                        EditText update_skill=rootLay.findViewById(R.id.up_skill);
                        update_name.setText(userAdmin.getfName());
                        update_email.setText(userAdmin.getUserEmail());
                        update_phone.setText(userAdmin.getUserPhone());
                        update_skill.setText(userAdmin.getUserSkillSet());
                        DatabaseReference databaseReference=FirebaseDatabase.getInstance().getReference().child("UsersAdmin").child(userAdmin.getfName());
                        AlertDialog.Builder builder = new AlertDialog.Builder(UserListActivity.this);
                        builder.setTitle("Update" + userAdmin.getfName());
                        builder.setView(rootLay);
                        builder.setPositiveButton("UPDATE", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String name= update_name.getText().toString();
                                String email= update_email.getText().toString();
                                String phone= update_phone.getText().toString();
                                String skill= update_skill.getText().toString();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for(DataSnapshot d:snapshot.getChildren()){
                                            if(d.exists()){
                                                databaseReference.child("fName").setValue(name);
                                                databaseReference.child("userEmail").setValue(email);
                                                databaseReference.child("userPhone").setValue(phone);
                                                databaseReference.child("userSkillSet").setValue(skill);
                                            }else{
                                                Toast.makeText(UserListActivity.this, "Data does not exists", Toast.LENGTH_SHORT);

                                            }
                                        }

                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                        Toast.makeText(UserListActivity.this, error.getDetails(), Toast.LENGTH_SHORT);

                                    }
                                });

                            }

                        });
                        AlertDialog dialog= builder.create();
                        dialog.show();
                    }

                });
                userViewHolder.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DatabaseReference deleteRef= FirebaseDatabase.getInstance().getReference().child("UsersAdmin").child(userAdmin.getfName());
                        deleteRef.removeValue();
                    }
                });

            }
        };

        rView.setAdapter(firebaseRecyclerAdapter);


    }
}