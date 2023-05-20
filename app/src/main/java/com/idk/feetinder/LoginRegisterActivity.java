// Catherine
package com.idk.feetinder;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginRegisterActivity extends AppCompatActivity {

    private Button login;
    private Button register;

    private FirebaseAuth auth;
    private DatabaseReference db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);
        getSupportActionBar().hide();

        login = findViewById(R.id.login_button);
        register = findViewById(R.id.register_button);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseDatabase.getInstance().getReference();

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(this.getApplicationContext().CONNECTIVITY_SERVICE);

        boolean connected = (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED);

        if(!connected){ // no network
            //no wifi screen
        }

        if(user != null){ // check if already logged in
            Intent intent = new Intent(LoginRegisterActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        login.setOnClickListener(view -> {
            Sounds.play(LoginRegisterActivity.this, R.raw.normal_button_tap);

            Intent intent = new Intent(LoginRegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });

        register.setOnClickListener(view -> {
            Sounds.play(LoginRegisterActivity.this, R.raw.normal_button_tap);

            Intent intent = new Intent(LoginRegisterActivity.this, RegistrationActivity.class);
            startActivity(intent);
            finish();
        });
    }
}