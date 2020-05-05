package com.prisonerprice.dayscountup.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;

import com.google.android.material.snackbar.Snackbar;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.viewmodel.EditViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class EditActivity extends AppCompatActivity {

    public final static String EXTRA_TASK_ID = "extrataskid";
    public final static String INSTANCE_TASK_ID = "instancetaskid";
    public final static Long A_DAY_IN_MILI = 24 * 3600 * 1000L;

    private final static String TAG = EditActivity.class.getSimpleName();
    private final static int DEFAULT_TASK_ID = -1;
    private final DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    private EditText taskDescEditText;
    private EditText monthEditText;
    private EditText dayEditText;
    private EditText yearEditText;
    private Switch hundredSwitch;
    private Switch anniversarySwitch;
    private EditText customDaysEditText;
    private Button submitBtn;
    private final EditViewModel editViewModel = new EditViewModel(this);

    private int mTaskId = DEFAULT_TASK_ID;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        // initViews
        taskDescEditText = findViewById(R.id.et_task_desc);
        monthEditText = findViewById(R.id.et_month);
        dayEditText = findViewById(R.id.et_day);
        yearEditText = findViewById(R.id.et_year);
        hundredSwitch = findViewById(R.id.switch_100);
        anniversarySwitch = findViewById(R.id.switch_anniversary);
        customDaysEditText = findViewById(R.id.et_custom_days);
        submitBtn = findViewById(R.id.btn_edit_activity);
        submitBtn.setOnClickListener(v -> onSaveButtonClicked());

        // Read savedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        // read intent from MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            submitBtn.setText(R.string.update_btn);
            if (mTaskId == DEFAULT_TASK_ID) {
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                editViewModel.getTask(mTaskId).observe(this, task -> {
                    editViewModel.getTask(mTaskId).removeObservers(this);
                    populateUI(task);
                });
            }
        }
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    public void populateUI(Task task) {
        if (task == null) return;

        taskDescEditText.setText(task.getDescription());

        Date date = task.getUpdatedAt();
        String dateStrings[] = format.format(date).split("-");
        monthEditText.setText(dateStrings[0]);
        dayEditText.setText(dateStrings[1]);
        yearEditText.setText(dateStrings[2]);

        ArrayList<Date> dates = task.getRemainderDates();
        for(Date d : dates) {
            if (d.getTime() - date.getTime() == A_DAY_IN_MILI)
                hundredSwitch.setChecked(true);
            else if (format.format(d).substring(0, 5).equals(format.format(date).substring(0, 5)))
                anniversarySwitch.setChecked(true);
            else
                customDaysEditText.setText((d.getTime() - date.getTime()) / A_DAY_IN_MILI + "");
        }
    }

    public void onSaveButtonClicked() {
        boolean dateFormatIsCorrect = true;
        String desc = taskDescEditText.getText().toString();

        String dateString = monthEditText.getText().toString() + "-" + dayEditText.getText().toString() + "-" + yearEditText.getText().toString();
        Date date;
        try {
            date = format.parse(dateString);
        } catch (ParseException e) {
            date = null;
            dateFormatIsCorrect = false;
            Snackbar.make(submitBtn, "Wrong Date format, please enter correct date", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show();
            e.printStackTrace();
        }

        if (dateFormatIsCorrect) {
            ArrayList<Date> dates = new ArrayList<>();
            if (hundredSwitch.isChecked())
                dates.add(new Date(date.getTime() + 100 * A_DAY_IN_MILI));
            if (anniversarySwitch.isChecked()) {
                String oldYear = format.format(date).substring(6, 10);
                String newYear = (Integer.parseInt(oldYear) + 1) + "";
                try {
                    dates.add(format.parse(format.format(date).substring(0, 6) + newYear));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            final Task task = new Task(desc, date, dates);
            editViewModel.insertOrUpdateTask(this, task, mTaskId);
        }

    }
}
