package com.idk.feetinder;

import androidx.appcompat.app.AppCompatActivity;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    View card;
    TextView cardText;
    int cardNum = 1;
    boolean homeTaken = false;
    float xDown = 0;
    float xHomeCard;
    float xHomeText;
    final int SWIPE_THRESHOLD = 350;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        int screenSize = getScreenWidth(MainActivity.this);

        card = findViewById(R.id.card_box);
        cardText = findViewById(R.id.card_text);

        ObjectAnimator animationCard = ObjectAnimator.ofFloat(card, "translationX", screenSize);
        animationCard.setDuration(250);
        ObjectAnimator animationText = ObjectAnimator.ofFloat(cardText, "translationX", screenSize);
        animationText.setDuration(250);


        card.setOnTouchListener(new OnSwipeTouchListener(MainActivity.this){
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                switch(motionEvent.getActionMasked()){
                    case MotionEvent.ACTION_UP:
                        if(card.getX() >= (xHomeCard +SWIPE_THRESHOLD)){
                            animationCard.setFloatValues(screenSize);
                            animationText.setFloatValues(screenSize);
                            animationCard.start();
                            animationText.start();
                            cardNum++;
                            cardText.setText("CARD " + cardNum);
                        } else if((card.getX() + SWIPE_THRESHOLD) <= xHomeCard){
                            animationCard.setFloatValues(screenSize*-1);
                            animationText.setFloatValues(screenSize*-1);
                            animationCard.start();
                            animationText.start();
                            cardNum--;
                            cardText.setText("CARD " + cardNum);
                        }

                        if(animationCard.isRunning()){
                            Utils.delay(500, new Utils.DelayCallback() {
                                @Override
                                public void afterDelay() {
                                    animationCard.end();
                                    animationText.end();
                                    card.setX(xHomeCard);
                                    cardText.setX(xHomeText);
                                }
                            });
                        }

                        card.setX(xHomeCard);
                        cardText.setX(xHomeText);
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
                        cardText.setX(cardText.getX() + xDistance);

                        break;

                }
                return super.onTouch(view, motionEvent);
            }

            @Override
            public void onClick() {
                Toast.makeText(MainActivity.this, "clicked", Toast.LENGTH_SHORT).show();
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
}