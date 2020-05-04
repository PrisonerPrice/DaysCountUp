package com.prisonerprice.dayscountup.view;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.prisonerprice.dayscountup.R;

public class EditActivity extends AppCompatActivity {

    public final static String EXTRA_TASK_ID = "EXTRA_TASK_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
    }
}
