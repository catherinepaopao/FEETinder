// Catherine
package com.idk.feetinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegistrationActivity extends AppCompatActivity {

    private Button register;
    private Button back;
    private EditText email;
    private EditText password;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    Intent intent = new Intent(RegistrationActivity.this, GetUserInfoActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        register = findViewById(R.id.register);
        back = findViewById(R.id.back_button);
        email = findViewById(R.id.email_register);
        password = findViewById(R.id.password_register);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPass = password.getText().toString();

                if(userEmail.isEmpty() || userPass.isEmpty()){
                    Sounds.play(RegistrationActivity.this, R.raw.alert_error);

                    Toast.makeText(RegistrationActivity.this, "enter stuff", Toast.LENGTH_SHORT).show();
                } else {
                    auth.createUserWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(RegistrationActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Sounds.play(RegistrationActivity.this, R.raw.alert_error);
                                Toast.makeText(RegistrationActivity.this, "couldn't sign up", Toast.LENGTH_SHORT).show();
                            } else {
                                Sounds.play(RegistrationActivity.this, R.raw.login_button);
                            }
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(RegistrationActivity.this, R.raw.normal_button_tap);

                Intent intent = new Intent(RegistrationActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        auth.addAuthStateListener(firebaseAuthStateListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        auth.removeAuthStateListener(firebaseAuthStateListener);
    }
}