package com.example.usiupeertutor;

import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class UserViewHolder extends RecyclerView.ViewHolder {
    TextView name, email,phone, skill;
    ImageView update, delete;
    LinearLayout userUpdateDelete;
    public UserViewHolder(@NonNull View itemView) {
        super(itemView);
        name= itemView.findViewById(R.id.list_name);
        email= itemView.findViewById(R.id.list_email);
        phone= itemView.findViewById(R.id.list_phone);
        skill= itemView.findViewById(R.id.list_skill);
        update=itemView.findViewById(R.id.list_update);
        delete=itemView.findViewById(R.id.list_delete);
        userUpdateDelete=itemView.findViewById(R.id.userUpdateDelete);
    }
}
