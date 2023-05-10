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

import java.util.Deque;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    private View card;
    private View backCard;
    private TextView logOut;
    private TextView cardText;

    private TextView greeting;
    private TextView profile;
    private boolean homeTaken = false;
    private boolean isFirstCard = true;
    private float xDown = 0;
    private float xHomeCard;
    private float xHomeText;
    private final int SWIPE_THRESHOLD = 350;

    private FirebaseAuth auth;

    Deque<String> usersToSwipe;
    String currentMatchId;
    String userId;

    BottomAppBar bottomAppBar;

    DatabaseReference currentUserDb;

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
        greeting = findViewById(R.id.greeting);
        profile = bottomAppBar.findViewById(R.id.home_button);

        auth = FirebaseAuth.getInstance();

        userId = auth.getCurrentUser().getUid();
        currentUserDb = FirebaseDatabase.getInstance().getReference();

        usersToSwipe = new LinkedList<String>();
        getPotentialMatches();

        currentUserDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String name = (String) snapshot.child("Users").child(userId).child("Name").getValue();

                greeting.setText(name + "'s Matches!");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(MainActivity.this, "read failed: " + error.getCode(), Toast.LENGTH_SHORT);
            }
        });

        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                auth.signOut();
                Intent intent = new Intent(MainActivity.this, LoginRegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                                if(!isFirstCard){
                                    currentUserDb.child("Users").child(currentMatchId).child("Swipes")
                                            .child("Like").child(userId).setValue(true); // store to database

                                    checkMatch();
                                }
                                animationCard.setFloatValues(screenSize);
                                //animationText.setFloatValues(screenSize);
                                animationCard.start();
                                //animationText.start();
                            } else if((card.getX() + SWIPE_THRESHOLD) <= xHomeCard){ // swipe left
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
                    ProfileDialog profileDialog = new ProfileDialog(MainActivity.this, currentMatchId);
                    profileDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    profileDialog.showDialog();
                }
            }
        });
    }

    private void checkMatch() {
        DatabaseReference currentConnectionsDb = currentUserDb.child("Users").child(userId).child("Swipes").child("Like").child(currentMatchId);
        currentConnectionsDb.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){ // both swiped
                    currentUserDb.child("Users").child(userId).child("Matches").child(currentMatchId).setValue("true");
                    currentUserDb.child("Users").child(currentMatchId).child("Matches").child(userId).setValue("true"); // add to both matches
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
                        usersToSwipe.add(snapshot.getKey());
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

    private static int getScreenWidth(Context context) {
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        display.getMetrics(metrics);

        return metrics.widthPixels;
    }

    private void getNextProfile(){ // display next user for swiping
        currentMatchId = usersToSwipe.poll();

        if(isFirstCard){
            return;
        }

        if(currentMatchId == null){
            cardText.setText("No matches at this time!");
            return;
        }

        while(currentMatchId.equals(userId) || currentMatchId.equals("Uid")){
            currentMatchId = usersToSwipe.poll();

            if(currentMatchId == null){
                cardText.setText("No matches at this time!");
                return;
            }
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