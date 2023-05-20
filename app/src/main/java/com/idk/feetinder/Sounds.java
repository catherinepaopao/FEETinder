package com.idk.feetinder;

import android.content.Context;
import android.media.MediaPlayer;

public class Sounds {
    public static void play(Context context, int sound){
        MediaPlayer player = MediaPlayer.create(context, sound);
        player.start();

        player.setOnCompletionListener(MediaPlayer::reset);
    }
}
