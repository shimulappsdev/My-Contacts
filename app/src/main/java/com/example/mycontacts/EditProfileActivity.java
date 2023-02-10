package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mycontacts.databinding.ActivityEditProfileBinding;
import com.example.mycontacts.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class EditProfileActivity extends AppCompatActivity {

    ActivityEditProfileBinding binding;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Uri updateImgUri;
    String updateImgUrl;
    String currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String myUser = intent.getStringExtra("myUid");


        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            currentUser = firebaseUser.getUid();
        }

        Log.i("TAG", "EditCurrent: "+currentUser);

        if (currentUser.equals(myUser)){
            databaseReference = FirebaseDatabase.getInstance().getReference("user").child(currentUser);
            databaseReference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    User user = snapshot.getValue(User.class);
                    binding.updateName.setText(user.getUser_name());
                    binding.updateEmail.setText(user.getUser_email());
                    binding.updatePhone.setText(user.getUser_phone());
                    binding.updatePassword.setText(user.getUser_password());
                    Glide.with(getApplicationContext()).load(user.getUser_profile()).placeholder(R.drawable.profilehint).into(binding.updateImage);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        binding.updateImgBtn.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_GET_CONTENT);
            intent1.setType("image/*");
            startActivityForResult(intent1,101);
        });

        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.updateProfileBtn.setOnClickListener(view -> {
            if (currentUser.equals(myUser)){
                String name = binding.updateName.getText().toString().trim();
                String phone = binding.updatePhone.getText().toString().trim();

                if (name == null){
                    binding.updateName.setError("");
                }else if (phone == null){
                    binding.updatePhone.setError("");
                }else {

                    databaseReference = FirebaseDatabase.getInstance().getReference("user");

                    Map<String, Object> userMap = new HashMap<>();
                    userMap.put("user_name",name);
                    userMap.put("user_phone",phone);

                    databaseReference.child(currentUser).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                startActivity(new Intent(EditProfileActivity.this, ProfileActivity.class));
                                finish();
                                Toast.makeText(EditProfileActivity.this, "Update User Successfully", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }

                if (updateImgUri != null){
                    storageReference = FirebaseStorage.getInstance().getReference("user").child(currentUser);
                    StorageReference storageReference1 = storageReference.child("profile").child(name+System.currentTimeMillis());
                    storageReference1.putFile(updateImgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                            if (task.isSuccessful()){
                                storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        updateImgUrl = String.valueOf(uri);
                                        Map<String, Object> userMap = new HashMap<>();
                                        userMap.put("user_profile",updateImgUrl);
                                        databaseReference.child(currentUser).updateChildren(userMap);
                                    }
                                });

                            }

                        }
                    });

                }
            }
        });

        binding.updateEmail.setOnClickListener(view -> {
            Toast.makeText(this, "Can't Edit", Toast.LENGTH_SHORT).show();
        });

        binding.updatePassword.setOnClickListener(view -> {
            Toast.makeText(this, "Can't Edit", Toast.LENGTH_SHORT).show();
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101){
            if (resultCode == RESULT_OK){
                if (data != null){
                    updateImgUri = data.getData();
                    binding.updateImage.setImageURI(updateImgUri);
                }
            }
        }
    }
}