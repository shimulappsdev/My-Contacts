package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mycontacts.databinding.ActivityProfileBinding;
import com.example.mycontacts.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileActivity extends AppCompatActivity {

    ActivityProfileBinding binding;
    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    String currentUser;
    String myUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String count = String.valueOf(intent.getIntExtra("count",0));
        binding.contactCount.setText(count);

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        if (firebaseUser != null){
            currentUser = firebaseUser.getUid();
        }

            databaseReference = FirebaseDatabase.getInstance().getReference("user").child(currentUser);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    User user = snapshot.getValue(User.class);
                    myUser = user.getUser_id();
                    binding.profileName.setText(user.getUser_name());
                    binding.profileEmail.setText(user.getUser_email());
                    binding.profilePhone.setText(user.getUser_phone());
                    Glide.with(getApplicationContext()).load(user.getUser_profile()).placeholder(R.drawable.profilehint).into(binding.profileImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });



        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.setting.setOnClickListener(view -> {
            String myUid = myUser;
            Intent intent1 = new Intent(ProfileActivity.this, EditProfileActivity.class);
            intent1.putExtra("myUid",myUid);
            startActivity(intent1);
        });

        binding.goToAdd.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, AddActivity.class));
        });

        binding.goToDelete.setOnClickListener(view -> {
            startActivity(new Intent(ProfileActivity.this, DeleteActivity.class));
        });

        binding.logOutBtn.setOnClickListener(view -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Logout");
            builder.setMessage("What do you want?");
            builder.setIcon(R.drawable.exit);
            builder.setPositiveButton("Logout", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    firebaseAuth.signOut();
                    startActivity(new Intent(ProfileActivity.this, LoginActivity.class));
                }
            });
            builder.setNegativeButton("Stay", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();

        });
    }
}