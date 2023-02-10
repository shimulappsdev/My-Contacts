package com.example.mycontacts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.mycontacts.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    ActivityLoginBinding binding;
    FirebaseAuth myAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        myAuth = FirebaseAuth.getInstance();

        binding.loginBtn.setOnClickListener(view -> {

            String email = binding.userEmail.getText().toString().trim();
            String password = binding.userPassword.getText().toString().trim();

            if (email.equals("")){
                binding.userEmail.setError("email can't be empty!");
            }
            else if (password.equals("")){
                binding.userPassword.setError("password can't be empty!");
            }else {

                userSignIn(email,password);

            }



//            startActivity(new Intent(LoginActivity.this, MainActivity.class));
//            finish();
        });

        binding.notRegister.setOnClickListener(view -> {
            startActivity(new Intent(LoginActivity.this, RegistrationActivity.class));
        });
    }

    private void userSignIn(String email, String password) {

        binding.progressBar.setVisibility(View.VISIBLE);
        binding.loginBtn.setVisibility(View.GONE);

        myAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(LoginActivity.this, "Successfully Log In", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(LoginActivity.this,MainActivity.class));
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                binding.progressBar.setVisibility(View.GONE);
                binding.loginBtn.setVisibility(View.VISIBLE);
                showAlert(e.getLocalizedMessage());
            }
        });

    }

    private void showAlert(String errorMsg) {

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