// Catherine
package com.idk.feetinder;

import android.app.Dialog;
import android.content.Context;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileDialog extends Dialog {
    String userId;
    public ProfileDialog(@NonNull Context context, String uid) {
        super(context);
        setContentView(R.layout.profile_dialog);

        userId = uid;
    }

    public void showDialog(){
        TextView name = findViewById(R.id.profile_name);
        TextView bio = findViewById(R.id.profile_bio);
        Button returnButton = findViewById(R.id.return_button);
        TextView questionAnswers = findViewById(R.id.question_responses);
        ImageView profilePicture = findViewById(R.id.profile_picture);

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText((String) snapshot.child("Users").child(userId).child("Name").getValue());
                bio.setText((String) snapshot.child("Users").child(userId).child("Bio").getValue());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });

        show();

        returnButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

    }
}
