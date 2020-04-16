package com.example.serwe;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;

import com.example.serwe.Common.Common;

public class TempActivity extends AppCompatActivity {

    TextView textView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_temp);
        textView = findViewById(R.id.textView);
        textView.setText(Common.currentUser.getName());
    }
}
