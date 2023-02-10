package com.example.mycontacts.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycontacts.DeleteActivity;
import com.example.mycontacts.MainActivity;
import com.example.mycontacts.R;
import com.example.mycontacts.UpdateActivity;
import com.example.mycontacts.model.Contact;
import com.example.mycontacts.viewholder.ContactViewHolder;

import java.util.List;

public class ContactAdapter extends RecyclerView.Adapter<ContactViewHolder> {

    private Context context;
    private List<Contact> contactList;

    public ContactAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public ContactViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ContactViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull ContactViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        String phone = contact.getContact_phone();

        holder.contactNameTV.setText(contact.getContact_name());
        holder.contactEmailTV.setText(contact.getContact_email());
        holder.contactNumberTV.setText(contact.getContact_phone());
        Glide.with(context).load(contact.getContact_image()).placeholder(R.drawable.profilehint).into(holder.contactImageTV);

        String contactId = contact.getContact_id();

        holder.callBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", phone, null));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.messageBtn.setOnClickListener(view -> {
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", phone, null));
            sendIntent.putExtra("sms_body", "");
            sendIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(sendIntent);
        });

        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(context, UpdateActivity.class);
            intent.putExtra("key",contact.getContact_id());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        });

        holder.itemView.setOnLongClickListener(view -> {
            Intent intent = new Intent(context, DeleteActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);

            SharedPreferences preferences = context.getSharedPreferences("contactPref", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString("contactId", contactId);
            editor.apply();

            return true;
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
