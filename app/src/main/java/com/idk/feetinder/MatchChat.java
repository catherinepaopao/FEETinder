// Aadya
package com.idk.feetinder;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import java.util.ArrayList;
import java.util.List;

public class MatchChat extends AppCompatActivity {

    RecyclerView recyclerView;
    LinearLayoutManager layoutManager;
    List<ChatModelClass> userList;
    Adapter adapter;
    Button back;
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
        userList.add(new ChatModelClass(R.drawable.defaultpfp, "Default", "0:00AM", "Default Text"));
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