package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.mycontacts.databinding.ActivityUpdateBinding;
import com.example.mycontacts.model.Contact;
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

public class UpdateActivity extends AppCompatActivity {

    ActivityUpdateBinding binding;
    DatabaseReference databaseReference;
    StorageReference storageReference;
    FirebaseUser firebaseUser;
    Uri updateImgUri;
    String updateImgUrl;
    String myUser;
    Intent intent;
    String contactId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUpdateBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        contactId = intent.getStringExtra("key");

        binding.updateImage.setImageResource(R.drawable.profilehint);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        if (firebaseUser != null){
            myUser = firebaseUser.getUid();
        }
        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(myUser);
        if (contactId != null){
            databaseReference.child("contact").child(contactId).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    Contact contact = snapshot.getValue(Contact.class);
                        if (contactId.equals(contact.getContact_id())){
                            binding.updateName.setText(contact.getContact_name());
                            binding.updateEmail.setText(contact.getContact_email());
                            binding.updatePhone.setText(contact.getContact_phone());
                            binding.updateShortDes.setText(contact.getContact_des());
                            Glide.with(getApplicationContext()).load(contact.getContact_image()).placeholder(R.drawable.profilehint).into(binding.updateImage);
                        }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }

        binding.backBtn.setOnClickListener(view -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });

        binding.updateImgBtn.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,101);
        });

        binding.updateContactBtn.setOnClickListener(view -> {
            String newName = binding.updateName.getText().toString().trim();
            String newEmail = binding.updateEmail.getText().toString().trim();
            String newNumber = binding.updatePhone.getText().toString().trim();
            String newDescription = binding.updateShortDes.getText().toString().trim();

            if (updateImgUri != null){
                storageReference = FirebaseStorage.getInstance().getReference("contactPicture").child(myUser);
                StorageReference storageReference1 = storageReference.child(newName+System.currentTimeMillis());
                storageReference1.putFile(updateImgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()){
                            storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    updateImgUrl = String.valueOf(uri);
                                    Map<String, Object> userMap = new HashMap<>();
                                    userMap.put("contact_image",updateImgUrl);
                                    databaseReference.child("contact").child(contactId).updateChildren(userMap);
                                }
                            });
                        }
                    }
                });
            }

            if (newName == null){
                binding.updateName.setError("");
            }else if (newEmail == null){
                binding.updateEmail.setError("");
            }else if (newNumber == null){
                binding.updatePhone.setError("");
            }else if (newDescription == null){
                binding.updateShortDes.setError("");
            }else {
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("contact_name",newName);
                userMap.put("contact_email",newEmail);
                userMap.put("contact_phone",newNumber);
                userMap.put("contact_des",newDescription);
                databaseReference.child("contact").child(contactId).updateChildren(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(UpdateActivity.this, "Contact Update Successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                            finish();
                        }
                    }
                });
            }
        });

        binding.callBtn.setOnClickListener(view -> {
            String contactNumber = binding.updatePhone.getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_DIAL, Uri.fromParts("tel", contactNumber, null));
            startActivity(intent);
        });

        binding.msgBtn.setOnClickListener(view -> {
            String contactNumber = binding.updatePhone.getText().toString().trim();
            Intent sendIntent = new Intent(Intent.ACTION_VIEW, Uri.fromParts("sms", contactNumber, null));
            sendIntent.putExtra("sms_body", "");
            startActivity(sendIntent);
        });

        binding.emailBtn.setOnClickListener(view -> {
            String contactEmail = binding.updateEmail.getText().toString().trim();
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[] {contactEmail});
            intent.putExtra(Intent.EXTRA_SUBJECT, "My subject");
            startActivity(Intent.createChooser(intent, "Email via..."));

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