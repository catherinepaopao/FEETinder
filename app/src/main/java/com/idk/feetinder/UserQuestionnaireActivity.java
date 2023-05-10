// Robbie
package com.idk.feetinder;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class UserQuestionnaireActivity extends AppCompatActivity implements View.OnClickListener{

    TextView totalQuestionsTextView;
    TextView questionTextView;
    Button ansA,ansB,ansC,ansD,ansE;
    Button submitBtn;

    int score = 0;
    int totalQuestion = QuestionAnswer.question.length;
    int currentQuestionIndex = 0;
    String selectedAnswer = "";
    private FirebaseAuth auth;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_questionnaire);
        getSupportActionBar().hide();

        auth = FirebaseAuth.getInstance();

        userId = auth.getCurrentUser().getUid();
        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();

        totalQuestionsTextView = findViewById(R.id.profile_questions);
        questionTextView = findViewById(R.id.question);
        ansA = findViewById(R.id.ans_A);
        ansB = findViewById(R.id.ans_B);
        ansC = findViewById(R.id.ans_C);
        ansD = findViewById(R.id.ans_D);
        ansE = findViewById(R.id.ans_E);
        submitBtn = findViewById(R.id.submit_btn);

        ansA.setOnClickListener(this);
        ansB.setOnClickListener(this);
        ansC.setOnClickListener(this);
        ansD.setOnClickListener(this);
        ansE.setOnClickListener(this);
        submitBtn.setOnClickListener(this);

        loadNewQuestion();

    }

    @Override
    public void onClick(View view) {

        ansA.setBackgroundColor(Color.GRAY);
        ansB.setBackgroundColor(Color.GRAY);
        ansC.setBackgroundColor(Color.GRAY);
        ansD.setBackgroundColor(Color.GRAY);
        ansE.setBackgroundColor(Color.GRAY);

        Button clickedButton = (Button) view;
        if(clickedButton.getId() == R.id.submit_btn) {
            if(selectedAnswer.equals("")){
                Toast.makeText(UserQuestionnaireActivity.this, "Please select an answer!", Toast.LENGTH_SHORT).show();
            } else {
                DatabaseReference currentQuestion = FirebaseDatabase.getInstance().getReference()
                        .child("Users").child(userId).child("QuestionAnswers").child("Q" + (currentQuestionIndex+1));
                currentQuestion.setValue(selectedAnswer);
                currentQuestionIndex++;

                selectedAnswer = "";
                loadNewQuestion();
            }
        } else {
            selectedAnswer = clickedButton.getText().toString();
            clickedButton.setBackgroundColor(Color.MAGENTA);
        }
    }

    void loadNewQuestion() {

        if (currentQuestionIndex == totalQuestion) {
            Intent intent = new Intent(UserQuestionnaireActivity.this, GetUserBioActivity.class);
            startActivity(intent);
            finish();
        } else {
            questionTextView.setText(QuestionAnswer.question[currentQuestionIndex]);
            ansA.setBackgroundColor(Color.GRAY);
            ansB.setBackgroundColor(Color.GRAY);
            ansC.setBackgroundColor(Color.GRAY);
            ansD.setBackgroundColor(Color.GRAY);
            ansE.setBackgroundColor(Color.GRAY);
            ansA.setText(QuestionAnswer.choices[currentQuestionIndex][0]);
            ansB.setText(QuestionAnswer.choices[currentQuestionIndex][1]);
            ansC.setText(QuestionAnswer.choices[currentQuestionIndex][2]);
            ansD.setText(QuestionAnswer.choices[currentQuestionIndex][3]);
            ansE.setText(QuestionAnswer.choices[currentQuestionIndex][4]);
        }
    }


}