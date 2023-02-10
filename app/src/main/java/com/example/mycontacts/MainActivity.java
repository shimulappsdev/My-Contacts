package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mycontacts.adapter.ContactAdapter;
import com.example.mycontacts.databinding.ActivityMainBinding;
import com.example.mycontacts.model.Contact;
import com.example.mycontacts.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    ActivityMainBinding binding;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    FirebaseUser firebaseUser;
    List<Contact> contactList;
    String currentUser;
    int countContact;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        contactList = new ArrayList<>();


        binding.profile.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
            intent.putExtra("count",countContact);
            startActivity(intent);
        });

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            currentUser = firebaseUser.getUid();
        }

        binding.addBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, AddActivity.class));
        });

        binding.progressBar.setVisibility(View.VISIBLE);

        if (currentUser != null){
            binding.profile.setImageResource(R.drawable.profilehint);
                DatabaseReference database = FirebaseDatabase.getInstance().getReference("user");
                database.child(currentUser).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        User user = snapshot.getValue(User.class);
                        String name = user.getUser_name();
                        Glide.with(getApplicationContext()).load(user.getUser_profile()).placeholder(R.drawable.profilehint).into(binding.profile);
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
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
                ContactAdapter adapter = new ContactAdapter(getApplicationContext(), contactList);
                binding.contactRecyclerView.setAdapter(adapter);
                binding.progressBar.setVisibility(View.GONE);
                countContact = contactList.size();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        permission();
    }

    private void permission() {

        Dexter.withContext(this)
                .withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                .withListener(new PermissionListener() {
                    @Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
                    @Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
                    @Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
                }).check();

    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Exit");
        builder.setMessage("What's in your mind ?");
        builder.setIcon(R.drawable.logo);
        builder.setPositiveButton("Exit", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Toast.makeText(getApplicationContext(), "Exit", Toast.LENGTH_SHORT).show();
                moveTaskToBack(true);
                android.os.Process.killProcess(android.os.Process.myPid());
                System.exit(1);
            }
        });
        builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}