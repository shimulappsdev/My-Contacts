package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.mycontacts.adapter.ContactAdapter;
import com.example.mycontacts.adapter.DeleteAdapter;
import com.example.mycontacts.databinding.ActivityDeleteBinding;
import com.example.mycontacts.model.Contact;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class DeleteActivity extends AppCompatActivity {

    ActivityDeleteBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    List<Contact> contactList;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeleteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contactList = new ArrayList<>();

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            currentUser = firebaseUser.getUid();
        }

        databaseReference = FirebaseDatabase.getInstance().getReference("user");
        databaseReference.child(currentUser).child("contact").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                contactList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Contact contact = dataSnapshot.getValue(Contact.class);
                    if (contact != null){
                        contactList.add(contact);
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
                DeleteAdapter adapter = new DeleteAdapter(getApplicationContext(), contactList);
                binding.deleteRecyclerView.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

    }
}