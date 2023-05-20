// Catherine
package com.idk.feetinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.Date;

public class GetUserInfoActivity extends AppCompatActivity {

    private RadioGroup genderSelect;
    private Button continueButton;
    private EditText enterBday;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_get_user_info);
        getSupportActionBar().hide();

        genderSelect = findViewById(R.id.gender_select);
        enterBday = findViewById(R.id.enter_birthday);
        continueButton = findViewById(R.id.continueButton);
        auth = FirebaseAuth.getInstance();

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int selected = genderSelect.getCheckedRadioButtonId();
                RadioButton checkedButton = findViewById(selected);

                if(checkedButton == null){
                    Sounds.play(GetUserInfoActivity.this, R.raw.alert_error);
                    Toast.makeText(GetUserInfoActivity.this, "must select gender", Toast.LENGTH_SHORT).show();
                    return;
                }

                if(!isBdayValid(enterBday.getText().toString())){
                    Sounds.play(GetUserInfoActivity.this, R.raw.alert_error);
                    Toast.makeText(GetUserInfoActivity.this, "invalid birthday", Toast.LENGTH_SHORT).show();
                    return;
                }

                String userId = auth.getCurrentUser().getUid();
                DatabaseReference currentUserGender = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(userId).child("Gender");

                currentUserGender.setValue(checkedButton.getText());

                Sounds.play(GetUserInfoActivity.this, R.raw.normal_button_tap);

                Intent intent = new Intent(GetUserInfoActivity.this, UserQuestionnaireActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private boolean isBdayValid(String bday) {
        if(bday.contains(".") || bday.contains(" ") || bday.contains("-")){
            return false;
        }

        String[] bdayParts = bday.split("/");

        if(bdayParts.length != 3){
            return false;
        }

        Date date = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);

        try {
            int month = Integer.parseInt(bdayParts[0]);
            int day = Integer.parseInt(bdayParts[1]);
            int year = Integer.parseInt(bdayParts[2]);

            if(!(month > 0 && month <= 12)){
                return false;
            }

            if(!(year > 1800 && year < cal.get(Calendar.YEAR))){
                return false;
            }

            if(month == 1 || month == 3 || month == 5 || month == 7 || month == 8 || month == 10 || month == 12){
                if(!(day <= 31 && day > 0)){
                    return false;
                }
            } else if(month == 4 || month == 6 || month == 9 || month == 11){
                if(!(day <= 30 && day > 0)){
                    return false;
                }
            } else {
                if(!(day <= 29 && day > 0)){
                    return false;
                }
            }
        }
        catch( Exception e ) {
            return false;
        }

        return true;
    }
}