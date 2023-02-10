package com.example.mycontacts.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mycontacts.MainActivity;
import com.example.mycontacts.R;
import com.example.mycontacts.UpdateActivity;
import com.example.mycontacts.model.Contact;
import com.example.mycontacts.viewholder.DeleteViewHolder;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class DeleteAdapter extends RecyclerView.Adapter<DeleteViewHolder> {

    private Context context;
    private List<Contact> contactList;

    public DeleteAdapter(Context context, List<Contact> contactList) {
        this.context = context;
        this.contactList = contactList;
    }

    @NonNull
    @Override
    public DeleteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new DeleteViewHolder(LayoutInflater.from(context).inflate(R.layout.contact_delete_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull DeleteViewHolder holder, int position) {
        Contact contact = contactList.get(position);
        holder.contactNameTV.setText(contact.getContact_name());
        holder.contactEmailTV.setText(contact.getContact_email());
        holder.contactNumberTV.setText(contact.getContact_phone());
        Glide.with(context).load(contact.getContact_image()).placeholder(R.drawable.profilehint).into(holder.contactImageTV);

        String contactId = contact.getContact_id();

        SharedPreferences preferences = context.getSharedPreferences("contactPref", Context.MODE_PRIVATE);
        String selectContact = preferences.getString("contactId", "Contact"+System.currentTimeMillis());
        if (contactId.equals(selectContact)){
            holder.deleteContactLayout.setBackgroundResource(R.color.yellow);
        }

        holder.deleteBtn.setOnClickListener(view -> {
            FirebaseAuth firebaseAuth;
            FirebaseUser firebaseUser;
            String myUser;
            firebaseAuth = FirebaseAuth.getInstance();
            firebaseUser = firebaseAuth.getCurrentUser();

            if (firebaseUser != null) {
                myUser = firebaseUser.getUid();
                DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("user").child(myUser).child("contact").child(contactId);
                databaseReference.removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        String name = contact.getContact_name();
                        if (task.isSuccessful()){
                            Toast.makeText(context, name+" Delete Successfully", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            }
        });

    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }
}
