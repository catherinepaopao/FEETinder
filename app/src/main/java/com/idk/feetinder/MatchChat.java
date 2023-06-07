// Aadya
package com.idk.feetinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class MatchChat extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ChatModelClass> userList;
    Adapter adapter;
    Button back;
    DatabaseReference db = FirebaseDatabase.getInstance().getReference();
    String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_match_chat);
        getSupportActionBar().hide();

        back = findViewById(R.id.back);
        initData();
        initRecyclerView();

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Sounds.play(MatchChat.this, R.raw.normal_button_tap);
                finish();
            }
        });
    }

    private void initData() {
        userList = new ArrayList<>();
        userList.add(new ChatModelClass(R.drawable.defaultpfp, "placeholder", "", ""));
        if(db == null){
            System.out.println("booooo");
            return;
        }
        DatabaseReference matchDb = db.child("Users").child(userId).child("Matches");
        matchDb.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                if(snapshot.exists()){
                    if(snapshot.getKey() != null){
                        String matchId = snapshot.getKey();
                        userList.add(new ChatModelClass(R.drawable.defaultpfp, "Match id: " + matchId, "", ""));
                        db.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if(snapshot.exists()){
                                    String matchName = (String) snapshot.child("Users").child(matchId).child("Name").getValue();
                                    userList.add(new ChatModelClass(R.drawable.defaultpfp, matchName, "", ""));
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });

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

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(RecyclerView.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new Adapter(userList);
        recyclerView.setAdapter((adapter));
        adapter.notifyDataSetChanged();
    }
}