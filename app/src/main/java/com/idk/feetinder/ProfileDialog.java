// Catherine
package com.idk.feetinder;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.view.View;

import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class ProfileDialog extends Dialog {
    String userId;
    int compat;
    final Context context = getContext();
    public ProfileDialog(@NonNull Context context, String uid, int compatScore) {
        super(context);
        setContentView(R.layout.profile_dialog);

        userId = uid;
        compat = compatScore;
    }

    public void showDialog(){
        TextView name = findViewById(R.id.profile_name);
        TextView bio = findViewById(R.id.profile_bio);
        Button returnButton = findViewById(R.id.return_button);
        TextView questionAnswers = findViewById(R.id.question_responses);
        ImageView profilePicture = findViewById(R.id.profile_picture);
        TextView compatDisplay = findViewById(R.id.compat_score);

        compatDisplay.setText("Predicted\ncompatibility:\n" + compat*-1 + "/5");

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                name.setText((String) snapshot.child("Users").child(userId).child("Name").getValue());
                bio.setText((String) snapshot.child("Users").child(userId).child("Bio").getValue());
                String pfp = (String) snapshot.child("Users").child(userId).child("ProfilePicture").getValue();

                if(isValidContextForGlide(context)){
                    if(pfp != null){
                        Glide.with(getContext()).load(pfp).into(profilePicture);
                    }
                }
                
                questionAnswers.setText("Question Answers: \n");
                formatQuestionAnswers(currentUserDb, questionAnswers);
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
                Sounds.play(getContext(), R.raw.normal_button_tap);
                dismiss();
            }
        });

    }

    private void formatQuestionAnswers(DatabaseReference userDb, TextView display) {
        DatabaseReference questionUserDb = userDb.child("Users").child(userId).child("QuestionAnswers");
        questionUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(int i = 0; i<5; i++){ // get all questions
                    String question = QuestionAnswer.question[i];
                    String questionAnswer = (String) snapshot.child("Q" + (i+1)).getValue();

                    String textDisplay = display.getText() + question + " " + questionAnswer + "\n";
                    display.setText(textDisplay);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getContext(), "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });
    }

    public static boolean isValidContextForGlide(final Context context) {
        if (context == null) {
            return false;
        }
        if (context instanceof Activity) {
            final Activity activity = (Activity) context;
            if (activity.isDestroyed() || activity.isFinishing()) {
                return false;
            }
        }
        return true;
    }
}
