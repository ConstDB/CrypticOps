package com.example.crypticops.ui.activity;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.crypticops.R;
import com.example.crypticops.core.util.backendTester;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        backendTester.runAllTests(this);
    }
}