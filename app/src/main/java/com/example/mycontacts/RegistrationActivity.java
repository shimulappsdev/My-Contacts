package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.mycontacts.databinding.ActivityRegistrationBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    ActivityRegistrationBinding binding;
    FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistrationBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        firebaseAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference("user");

        binding.registerBtn.setOnClickListener(view -> {
            String name = binding.nameInput.getText().toString().trim();
            String email = binding.emailInput.getText().toString().trim();
            String phone = binding.phoneInput.getText().toString().trim();
            String password = binding.passwordInput.getText().toString().trim();
            if (name.equals("")){
                binding.nameInput.setError("Name Required");
            }else if (email.equals("")){
                binding.emailInput.setError("Email Required");
            }else if (phone.equals("")){
                binding.phoneInput.setError("Phone Required");
            }else if (password.equals("")){
                binding.passwordInput.setError("Password Required");
            }else {
                register(name, email, phone, password);
            }
        });

        binding.alreadyRegister.setOnClickListener(view -> {
            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
        });

    }

    private void register(String name, String email, String phone, String password) {

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.registerBtn.setVisibility(View.GONE);

        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                String userId = firebaseUser.getUid();
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("user_id",userId);
                userMap.put("user_name",name);
                userMap.put("user_email",email);
                userMap.put("user_phone",phone);
                userMap.put("user_password",password);
                userMap.put("user_profile","");

                databaseReference.child(userId).setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Toast.makeText(RegistrationActivity.this, "Register Success", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegistrationActivity.this, LoginActivity.class));
                            finish();
                        }else {
                            showAlert(task.getException().getLocalizedMessage());
                        }
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.registerBtn.setVisibility(View.VISIBLE);
                showAlert(e.getLocalizedMessage());

            }
        });
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
}