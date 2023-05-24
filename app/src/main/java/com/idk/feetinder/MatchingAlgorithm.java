package com.idk.feetinder;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MatchingAlgorithm {
    private static DatabaseReference currentUserDb = FirebaseDatabase.getInstance().getReference();

    public static int calculateCompatibilityScores(String[] user1Answers, String[] user2Answers) {
        int compatibilityScore = 0;

        if(user1Answers[0] != null && user2Answers[0] != null){
            if (user1Answers[0].equals(user2Answers[0])) {
                compatibilityScore++;
            }
        }

        // q2:How do you like your coffee?
        if(user1Answers[1] != null && user2Answers[1] != null){
            if (user1Answers[1].equals("Black") && user2Answers[1].equals("Tea") ||
                    user1Answers[1].equals("Black") && user2Answers[1].equals("Black") ||
                    user1Answers[1].equals("Tea") && user2Answers[1].equals("Tea") ||
                    user1Answers[1].equals("Tea") && user2Answers[1].equals("Black") ||
                    user1Answers[1].equals("Latte") &&
                            (user2Answers[1].equals("Latte") || user2Answers[1].equals("Cappuccino") || user2Answers[1].equals("Mocha")) ||
                    user1Answers[1].equals("Cappuccino") &&
                            (user2Answers[1].equals("Cappuccino") || user2Answers[1].equals("Latte") || user2Answers[1].equals("Mocha")) ||
                    user1Answers[1].equals("Mocha") &&
                            (user2Answers[1].equals("Mocha") || user2Answers[1].equals("Latte") || user2Answers[1].equals("Cappuccino"))) {
                compatibilityScore++;
            }
        }

        // q3:Ideal foot size?
        if(user1Answers[2] != null && user2Answers[2] != null){
                        /*int mainFootSize = convertFootSize(user1Answers[2]);
                        int userFootSize = convertFootSize(user2Answers[2]);
                        int footSizeDifference = Math.abs(mainFootSize - userFootSize);
                        if (footSizeDifference <= 5) { // Catherine edit, min foot size wouldn't work
                            compatibilityScore++;
                        }*/
            if(user1Answers[2].equals(user2Answers[2])){
                compatibilityScore++;
            }
        }

        // q4:What are the most important universal human rights?
        if(user1Answers[3] != null && user2Answers[3] != null){
            if (user1Answers[3].equals("Right to work") && user2Answers[3].equals("Right to education") ||
                    user1Answers[3].equals("Right to work") && user2Answers[3].equals("Right to work") ||
                    user1Answers[3].equals("Right to education") && user2Answers[3].equals("Right to education") ||
                    user1Answers[3].equals("Right to education") && user2Answers[3].equals("Right to work") ||
                    user1Answers[3].equals("Right to life and liberty") &&
                            (user2Answers[3].equals("Right to life and liberty") || user2Answers[3].equals("Right to privacy") || user2Answers[3].equals("Freedom of expression")) ||
                    user1Answers[3].equals("Right to privacy") &&
                            (user2Answers[3].equals("Right to privacy") || user2Answers[3].equals("Right to liberty") || user2Answers[3].equals("Freedom of expression")) ||
                    user1Answers[3].equals("Freedom of expression") &&
                            (user2Answers[3].equals("Freedom of expression") || user2Answers[3].equals("Right to privacy") || user2Answers[3].equals("Right to life and liberty"))) {
                compatibilityScore++;
            }
        }

        // q5:Favorite toe?
        if(user1Answers[4] != null && user2Answers[4] != null){
                        /* int mainFavoriteToe = convertToeNumber(user1Answers[4]);
                        int userFavoriteToe = convertToeNumber(user2Answers[4]);
                        int toeDifference = Math.abs(mainFavoriteToe - userFavoriteToe);
                        if (toeDifference <= 1) compatibilityScore++; */

            if(user1Answers[4].equals(user2Answers[4])){
                compatibilityScore++;
            }
        }

        return compatibilityScore;
    }

    //simple converting for strings for easier comparison in foot size function
    private static int convertFootSize(String footSize) {
        Map<String, Integer> footSizeMap = new HashMap<>();
        footSizeMap.put("6 inches", 6);
        footSizeMap.put("10 inches", 10);
        footSizeMap.put("13 inches", 13);
        footSizeMap.put("4 feet", 48);
        footSizeMap.put("26 yards", 936);
        return footSizeMap.get(footSize);
    }

    //same as above but for which toe user likes
    private static int convertToeNumber(String toe) {
        Map<String, Integer> toeNumberMap = new HashMap<>();
        toeNumberMap.put("Big toe", 5);
        toeNumberMap.put("Long toe", 4);
        toeNumberMap.put("Middle toe", 3);
        toeNumberMap.put("Ring toe", 2);
        toeNumberMap.put("Pinky toe", 1);
        return toeNumberMap.get(toe);
    }
}

    //get diff to comapre to other user diff
    /* private static int getMinimumFootSizeDifference() {
        int minDifference = Integer.MAX_VALUE;
        for (User user : userList) {
            int mainFootSize = convertFootSize(user1Answers[2]);
            int userFootSize = convertFootSize(user.getFootSizePreference());
            int footSizeDifference = Math.abs(mainFootSize - userFootSize);
            if (footSizeDifference < minDifference) {
                minDifference = footSizeDifference;
            }
        }
        return minDifference;
    } */

    //same as above but toe diffs
    /* private static int getMinimumToeDifference() {
        int minDifference = Integer.MAX_VALUE;
        for (User user : userList) {
            int mainFavoriteToe = convertToeNumber(mainUser.getFavoriteToe());
            int userFavoriteToe = convertToeNumber(user.getFavoriteToe());
            int toeDifference = Math.abs(mainFavoriteToe - userFavoriteToe);
            if (toeDifference < minDifference) {
                minDifference = toeDifference;
            }
        }
        return minDifference;
    } */