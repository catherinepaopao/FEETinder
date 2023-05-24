// Catherine
package com.idk.feetinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Deque;
import java.util.LinkedList;
import java.util.PriorityQueue;

public class MainActivity extends AppCompatActivity {

    private View card;
    private View backCard;
    private TextView logOut;
    private TextView chat;
    private TextView cardText;

    private TextView greeting;
    private TextView profile;
    private boolean homeTaken = false;
    private boolean isFirstCard = true;
    private boolean firstCheck = true;
    private float xDown = 0;
    private float xHomeCard;
    private float xHomeText;
    private final int SWIPE_THRESHOLD = 350;

    private FirebaseAuth auth;

    /* private PriorityQueue<String[]> usersToSwipe; */
    private Deque<String[]> usersToSwipe;
    private String currentMatchId;
    private String[] currentMatchInfo;
    private String userId;

    private BottomAppBar bottomAppBar;

    private DatabaseReference currentUserDb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screenSize = getScreenWidth(MainActivity.this);
        getSupportActionBar().hide();

        bottomAppBar = findViewById(R.id.bottomAppBar);
        card = findViewById(R.id.card_box);
        backCard = findViewById(R.id.card_behind);
        cardText = findViewById(R.id.card_text);
        logOut = bottomAppBar.findViewById(R.id.log_out);
        chat = bottomAppBar.findViewById(R.id.chat);
        greeting = findViewById(R.id.greeting);
        profile = bottomAppBar.findViewById(R.id.home_button);

        auth = FirebaseAuth.getInstance();

        userId = auth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference();

        /* if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            usersToSwipe = new PriorityQueue<>(Comparator.comparingInt(a -> Integer.parseInt(a[1])));
        } */
        usersToSwipe = new LinkedList<String[]>();
        getPotentialMatches();

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    if(!snapshot.child("Users").hasChild(userId) || !snapshot.child("Users").child(userId).hasChild("Gender")
                            || !snapshot.child("Users").child(userId).hasChild("QuestionAnswers") ||
                            snapshot.child("Users").child(userId).child("QuestionAnswers").getChildrenCount() < 5
                            || !snapshot.child("Users").child(userId).hasChild("Name")){
                        if(firstCheck){
                            firstCheck = false;
                            Intent intent = new Intent(MainActivity.this, GetUserInfoActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    } else {
                        String name = (String) snapshot.child("Users").child(userId).child("Name").getValue();
                        greeting.setText(name + "'s Matches!");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(MainActivity.this, R.raw.logout_button);
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(MainActivity.this, R.raw.logout_button);
                Intent intent = new Intent(MainActivity.this, MatchChat.class);
                startActivity(intent);
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(MainActivity.this, R.raw.normal_button_tap);
                Intent intent = new Intent(MainActivity.this, GetUserBioActivity.class);
                startActivity(intent);
            }
        });

        ObjectAnimator animationCard = ObjectAnimator.ofFloat(card, "translationX", screenSize);
        animationCard.setDuration(250);
        ObjectAnimator animationText = ObjectAnimator.ofFloat(cardText, "translationX", screenSize);
        animationText.setDuration(250);


        card.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if(currentMatchId != null || isFirstCard){
                    switch(motionEvent.getActionMasked()){
                        case MotionEvent.ACTION_UP:
                            if(card.getX() >= (xHomeCard +SWIPE_THRESHOLD)){ // swipe right
                                Sounds.play(MainActivity.this, R.raw.swipe_right);
                                if(!isFirstCard){
                                    currentUserDb.child("Users").child(currentMatchId).child("Swipes")
                                            .child("Like").child(userId).setValue(true); // store to database

                                    checkMatch(currentMatchId);
                                }
                                animationCard.setFloatValues(screenSize);
                                //animationText.setFloatValues(screenSize);
                                animationCard.start();
                                //animationText.start();
                            } else if((card.getX() + SWIPE_THRESHOLD) <= xHomeCard){ // swipe left
                                Sounds.play(MainActivity.this, R.raw.swipe_left);
                                if(!isFirstCard){
                                    currentUserDb.child("Users").child(currentMatchId).child("Swipes")
                                            .child("Dislike").child(userId).setValue(true); // store to database
                                }
                                animationCard.setFloatValues(screenSize*-1);
                                //animationText.setFloatValues(screenSize*-1);
                                animationCard.start();
                                //animationText.start();
                            }

                            if(animationCard.isRunning()){
                                Utils.delay(500, new Utils.DelayCallback() {
                                    @Override
                                    public void afterDelay() {
                                        if(isFirstCard){
                                            isFirstCard = false;
                                        }
                                        getNextProfile();
                                        animationCard.end();
                                        //animationText.end();
                                        card.setX(xHomeCard);
                                        //cardText.setX(xHomeText);
                                    }
                                });
                            }

                            card.setX(xHomeCard);
                            //cardText.setX(xHomeText);
                            break;

                        case MotionEvent.ACTION_DOWN:
                            if(!homeTaken){
                                xHomeCard = card.getX();
                                xHomeText = cardText.getX();
                                homeTaken = true;
                            }

                            xDown = motionEvent.getX();
                            break;

                        case MotionEvent.ACTION_MOVE:
                            float xMoved = motionEvent.getX();
                            float xDistance = xMoved- xDown;

                            card.setX(card.getX() + xDistance);
                            //cardText.setX(cardText.getX() + xDistance);

                            if(card.getX() >= xHomeCard){ // lean right
                                backCard.setBackgroundColor(Color.parseColor("#2fad39"));
                            } else if(card.getX() <= xHomeCard){ // lean left
                                backCard.setBackgroundColor(Color.parseColor("#ad2f2f"));
                            } else {
                                backCard.setBackgroundColor(Color.parseColor("#e8dab3"));
                            }

                            break;

                    }
                }
                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onClick() {
                if(currentMatchId != null){
                    Sounds.play(MainActivity.this, R.raw.click_profile_card);
                    ProfileDialog profileDialog = new ProfileDialog(MainActivity.this, currentMatchId, Integer.parseInt(currentMatchInfo[1]));
                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    profileDialog.showDialog();
                }
            }
        });
    }

    private void checkMatch(String matchId) {
        DatabaseReference currentConnectionsDb = currentUserDb.child("Users").child(userId).child("Swipes").child("Like").child(matchId);
        currentConnectionsDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){ // both swiped
                    currentUserDb.child("Users").child(userId).child("Matches").child(matchId).setValue("true");
                    currentUserDb.child("Users").child(matchId).child("Matches").child(userId).setValue("true"); // add to both matches
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void getPotentialMatches() {
        DatabaseReference potentialMatchDb = FirebaseDatabase.getInstance().getReference().child("Users");
        potentialMatchDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists() && !snapshot.child("Swipes").child("Like").hasChild(userId)
                        && !snapshot.child("Swipes").child("Dislike").hasChild(userId)){ // don't repeat cards
                    if(!snapshot.getKey().equals(userId)){ // not current user
                        addUserToQueue(userId, snapshot.getKey());
                    }
                }
            }
            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {
            }
            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    private void addUserToQueue(String uid1, String uid2) {
        DatabaseReference theDb = FirebaseDatabase.getInstance().getReference();
        theDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String[] user1Answers = new String[5];
                String[] user2Answers = new String[5];

                if (snapshot.child("Users").child(uid1).child("QuestionAnswers").getChildrenCount() == 5) {
                    for (int i = 0; i < 5; i++) {
                        user1Answers[i] = snapshot.child("Users").child(uid1).child("QuestionAnswers").child("Q" + (i + 1)).getValue().toString();
                    }
                }

                if (snapshot.child("Users").child(uid2).child("QuestionAnswers").getChildrenCount() == 5) {
                    for (int i = 0; i < 5; i++) {
                        user2Answers[i] = snapshot.child("Users").child(uid2).child("QuestionAnswers").child("Q" + (i + 1)).getValue().toString();
                    }
                }

                int compatScore = MatchingAlgorithm.calculateCompatibilityScores(user1Answers, user2Answers);

                String[] newEntry = {uid2,
                        String.valueOf((-1*compatScore))};
                usersToSwipe.add(newEntry);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.widthPixels;
    }

    private void getNextProfile(){ // display next user for swiping
        currentMatchInfo = usersToSwipe.poll();

        if(isFirstCard){
            return;
        }

        if(currentMatchInfo == null){
            currentMatchId = null;
            cardText.setText("No matches at this time!");
            return;
        }

        currentMatchId = currentMatchInfo[0];

        while(currentMatchId.equals(userId) || currentMatchId.equals("Uid")){
            currentMatchInfo = usersToSwipe.poll();

            if(currentMatchInfo == null){
                currentMatchId = null;
                cardText.setText("No matches at this time!");
                return;
            }

            currentMatchId = currentMatchInfo[0];
        }

        DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();
        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // initial card display
                if(currentMatchId != null){
                    cardText.setText((String) snapshot.child("Users").child(currentMatchId).child("Name").getValue());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });
    }
}