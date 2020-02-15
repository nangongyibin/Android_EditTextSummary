package com.example.edittextsummary;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.util.Log;
import android.view.View;

import com.ngyb.edittextsummary.DropEditText;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DropEditText et = findViewById(R.id.et);
        et.setOnListChange(new DropEditText.OnListChange() {
            @Override
            public void change(String value) {
                Log.e(TAG, "change: " );
            }

            @Override
            public void newest(String value) {
                Log.e(TAG, "newest: " );
            }

            @Override
            public void sendMessage(String message) {
                Log.e(TAG, "sendMessage: "+message );
            }
        });
    }
}
