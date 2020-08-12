package com.example.android.aptdemo;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import com.example.android.apt_annotation.*;
import com.example.android.api.Injection;

public class MainActivity extends AppCompatActivity {

    @BindViewCompiler(R.id.testTextView)
    TextView testTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Injection.inject(this);
    }

    @ClickViewCompiler(R.id.testBtn)
    void addNumber() {
        String curNumberStr = testTextView.getText().toString();
        testTextView.setText(String.valueOf(Integer.parseInt(curNumberStr) + 1));
    }
}