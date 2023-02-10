package com.example.mycontacts.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mycontacts.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView contactImageTV;
    public TextView contactNameTV, contactNumberTV, contactEmailTV;
    public ImageView callBtn, messageBtn;

    public ContactViewHolder(@NonNull View itemView) {
        super(itemView);

        contactImageTV = itemView.findViewById(R.id.contactImageTV);
        contactNameTV = itemView.findViewById(R.id.contactNameTV);
        contactNumberTV = itemView.findViewById(R.id.contactNumberTV);
        contactEmailTV = itemView.findViewById(R.id.contactEmailTV);
        callBtn = itemView.findViewById(R.id.callBtn);
        messageBtn = itemView.findViewById(R.id.messageBtn);
    }
}
