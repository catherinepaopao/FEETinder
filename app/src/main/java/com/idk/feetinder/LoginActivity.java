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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    private Button login;
    private Button back;
    private EditText email;
    private EditText password;

    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener firebaseAuthStateListener;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();
        firebaseAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user != null){
                    String uid = user.getUid();
                    db.addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) { // check for unfinished sign up ADD BDAY LATER
                            if(snapshot.exists()){
                                if(!snapshot.child("Users").hasChild(uid) || !snapshot.child("Users").child(uid).hasChild("Gender")){
                                    Intent intent = new Intent(LoginActivity.this, GetUserInfoActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if(!snapshot.child("Users").child(uid).hasChild("QuestionAnswers") ||
                                        snapshot.child("Users").child(uid).child("QuestionAnswers").getChildrenCount() < 5){
                                    Intent intent = new Intent(LoginActivity.this, UserQuestionnaireActivity.class);
                                    startActivity(intent);
                                    finish();
                                } else if(!snapshot.child("Users").child(uid).hasChild("Name")){
                                    Intent intent = new Intent(LoginActivity.this, GetUserBioActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(LoginActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
                        }
                    });

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        };

        login = findViewById(R.id.login);
        back = findViewById(R.id.back_button);
        email = findViewById(R.id.email_login);
        password = findViewById(R.id.password_login);


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userEmail = email.getText().toString();
                String userPass = password.getText().toString();

                if(userEmail.isEmpty() || userPass.isEmpty()){
                    Toast.makeText(LoginActivity.this, "enter credentials", Toast.LENGTH_SHORT).show();
                } else {
                    auth.signInWithEmailAndPassword(userEmail, userPass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(!task.isSuccessful()){
                                Toast.makeText(LoginActivity.this, "bad credentials", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, LoginRegisterActivity.class);
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