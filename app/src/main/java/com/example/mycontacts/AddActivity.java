package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.Toast;

import com.example.mycontacts.databinding.ActivityAddBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Map;

public class AddActivity extends AppCompatActivity {

    ActivityAddBinding binding;
    FirebaseUser firebaseUser;
    DatabaseReference databaseReference;
    Uri contactImgUri;
    String contactImgUrl;
    StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());


        binding.backBtn.setOnClickListener(view -> {
            finish();
        });

        binding.addImg.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");
            startActivityForResult(intent,101);
        });

        binding.addContactBtn.setOnClickListener(view -> {

            String contactName = binding.contactName.getText().toString().trim();
            String contactEmail = binding.contactEmail.getText().toString().trim();
            String contactPhone = binding.contactNumber.getText().toString().trim();
            String contactDes = binding.contactShortDes.getText().toString().trim();

            if (contactName.equals("")){
                binding.contactName.setError("");
            }else if (contactEmail.equals("")){
                binding.contactEmail.setError("");
            }else if (contactPhone.equals("")){
                binding.contactNumber.setError("");
            }else {

                addContact(contactName, contactEmail, contactPhone, contactDes);

            }

        });

    }

    private void addContact(String contactName, String contactEmail, String contactPhone, String contactDes) {

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        String myUserId = firebaseUser.getUid();

        databaseReference = FirebaseDatabase.getInstance().getReference("user").child(myUserId);
        storageReference = FirebaseStorage.getInstance().getReference("contactPicture").child(myUserId);

        String contactId = databaseReference.push().getKey();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put("contact_id",contactId);
        userMap.put("contact_name",contactName);
        userMap.put("contact_email",contactEmail);
        userMap.put("contact_phone",contactPhone);
        userMap.put("contact_des",contactDes);
        userMap.put("contact_image","");

        databaseReference.child("contact").child(contactId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){

                    Toast.makeText(AddActivity.this, "Add to Contact Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(AddActivity.this, MainActivity.class));
                    finish();
                }else {
                    showAlert(task.getException().getLocalizedMessage());
                }
            }
        });

        if (contactImgUri != null){
            StorageReference storageReference1 = storageReference.child(contactName+System.currentTimeMillis());
            storageReference1.putFile(contactImgUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (task.isSuccessful()){
                        storageReference1.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                contactImgUrl = String.valueOf(uri);
                                Map<String, Object> userMap = new HashMap<>();
                                userMap.put("contact_image",contactImgUrl);
                                databaseReference.child("contact").child(contactId).updateChildren(userMap);
                            }
                        });
                    }
                }
            });
        }



    }

    private void showAlert(String errorMsg){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Caution!");
        builder.setMessage(errorMsg);
        builder.setIcon(android.R.drawable.stat_notify_error);

        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 101){
            if (resultCode == RESULT_OK){
                if (data != null){
                    contactImgUri = data.getData();
                    binding.contactImage.setImageURI(contactImgUri);
                }
            }
        }

    }
}