package com.example.mycontacts.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycontacts.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class DeleteViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView contactImageTV;
    public TextView contactNameTV, contactNumberTV, contactEmailTV;
    public ImageView deleteBtn;
    public ConstraintLayout deleteContactLayout;

    public DeleteViewHolder(@NonNull View itemView) {
        super(itemView);
        contactImageTV = itemView.findViewById(R.id.contactImageTV);
        contactNameTV = itemView.findViewById(R.id.contactNameTV);
        contactNumberTV = itemView.findViewById(R.id.contactNumberTV);
        contactEmailTV = itemView.findViewById(R.id.contactEmailTV);
        deleteBtn = itemView.findViewById(R.id.deleteBtn);
        deleteContactLayout = itemView.findViewById(R.id.deleteContactLayout);
    }
}
