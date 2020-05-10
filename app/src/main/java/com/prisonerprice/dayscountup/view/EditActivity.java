package com.prisonerprice.dayscountup.view;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkerParameters;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.maltaisn.icondialog.IconDialog;
import com.maltaisn.icondialog.IconDialogSettings;
import com.maltaisn.icondialog.data.Icon;
import com.maltaisn.icondialog.pack.IconDrawableLoader;
import com.maltaisn.icondialog.pack.IconPack;
import com.prisonerprice.dayscountup.App;
import com.prisonerprice.dayscountup.R;
import com.prisonerprice.dayscountup.database.Task;
import com.prisonerprice.dayscountup.notification.NotificationDelayCalculator;
import com.prisonerprice.dayscountup.notification.NotificationWorker;
import com.prisonerprice.dayscountup.utils.Utils;
import com.prisonerprice.dayscountup.viewmodel.EditViewModel;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import static com.prisonerprice.dayscountup.notification.NotificationDelayCalculator.*;

public class EditActivity extends AppCompatActivity implements IconDialog.Callback{

    public final static String EXTRA_TASK_ID = "extrataskid";
    public final static String INSTANCE_TASK_ID = "instancetaskid";
    public final static Long A_DAY_IN_MILI = 24 * 3600 * 1000L;
    public final static int ICON_DEFAULT_ID = 1036;

    private final static String TAG = EditActivity.class.getSimpleName();
    private final static int DEFAULT_TASK_ID = -1;
    private final DateFormat format = new SimpleDateFormat("MM-dd-yyyy", Locale.getDefault());

    private EditText taskDescEditText;
    private EditText monthEditText;
    private EditText dayEditText;
    private EditText yearEditText;
    private SwitchMaterial hundredSwitch;
    private SwitchMaterial anniversarySwitch;
    private EditText customDaysEditText;
    private Button submitBtn;
    private ImageView iconImage;
    private SwitchMaterial notificationSwitch;

    private final EditViewModel editViewModel = new EditViewModel(this);

    private int mTaskId = DEFAULT_TASK_ID;

    private final static String ICON_DIALOG_TAG = "icon-dialog";

    private int iconID = ICON_DEFAULT_ID;
    private IconPack iconPack;

    private final static String CHANNEL_ID = "CHANNEL_ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);

        createNotificationChannel();

        iconPack = ((App) getApplication()).getIconPack();

        // initViews
        taskDescEditText = findViewById(R.id.et_task_desc);
        monthEditText = findViewById(R.id.et_month);
        dayEditText = findViewById(R.id.et_day);
        yearEditText = findViewById(R.id.et_year);
        hundredSwitch = findViewById(R.id.switch_100);
        anniversarySwitch = findViewById(R.id.switch_anniversary);
        customDaysEditText = findViewById(R.id.et_custom_days);
        submitBtn = findViewById(R.id.btn_edit_activity);
        iconImage = findViewById(R.id.iv_icon);
        notificationSwitch = findViewById(R.id.switch_allow_notification);

        iconImage.setImageDrawable(iconPack.getIcon(1036).getDrawable());

        // Read savedInstanceState
        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskId = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_TASK_ID);
        }

        // read intent from MainActivity
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {
            submitBtn.setText(R.string.update_btn);
            if (mTaskId == DEFAULT_TASK_ID) {
                /*
                mTaskId = intent.getIntExtra(EXTRA_TASK_ID, DEFAULT_TASK_ID);
                editViewModel.getTask(mTaskId).observe(this, task -> {
                    editViewModel.getTask(mTaskId).removeObservers(this);
                    populateUI(task);
                });

                 */
                populateUI(editViewModel.getBufferTask());
            }
        }

        submitBtn.setOnClickListener(v -> onSaveButtonClicked());

        // If dialog is already added to fragment manager, get it. If not, create a new instance.
        IconDialog dialog = (IconDialog) getSupportFragmentManager().findFragmentByTag(ICON_DIALOG_TAG);
        IconDialog iconDialog = dialog != null ? dialog
                : IconDialog.newInstance(new IconDialogSettings.Builder().build());

        iconImage.setOnClickListener(v -> {
            // Open icon dialog
            iconDialog.show(getSupportFragmentManager(), ICON_DIALOG_TAG);
        });

    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskId);
        super.onSaveInstanceState(outState);
    }

    // Read data from db and populate the UI
    public void populateUI(Task task) {
        Log.d(TAG, "Running into method populateUI");
        if (task == null) return;
        Log.d(TAG, "Populate the UI");

        taskDescEditText.setText(task.getDescription());

        Date date = task.getUpdatedAt();
        String dateStrings[] = format.format(date).split("-");
        monthEditText.setText(dateStrings[0]);
        dayEditText.setText(dateStrings[1]);
        yearEditText.setText(dateStrings[2]);

        iconID = task.getIconID();
        if (iconID != ICON_DEFAULT_ID) {
            Icon icon = iconPack.getIcon(iconID);
            iconImage.setImageDrawable(icon.getDrawable());
        }

        if (task.getAllowNotification() == 1) notificationSwitch.setChecked(true);
        else notificationSwitch.setChecked(false);

        if (task.getCelebrate100Days() == 1) hundredSwitch.setChecked(true);
        else hundredSwitch.setChecked(false);

        if (task.getCelebrateAnniversary() == 1) anniversarySwitch.setChecked(true);
        else anniversarySwitch.setChecked(false);

        int customCelebrateDay = task.getCustomCelebrateDay();
        customDaysEditText.setText(customCelebrateDay + "");

    }

    // Save data back to the database
    public void onSaveButtonClicked() {

        Task bufferTask = editViewModel.getBufferTask();
        Task newTask = new Task(bufferTask);

        boolean dateFormatIsCorrect = true;
        boolean inputFormatIsCorrect = true;

        if (taskDescEditText.getText() != null) {
            newTask.setDescription(taskDescEditText.getText().toString());
        } else {
            inputFormatIsCorrect = false;
        }

        String dateString = "";

        try {
            int month = Integer.parseInt(monthEditText.getText().toString());
            int day = Integer.parseInt(dayEditText.getText().toString());
            int year = Integer.parseInt(yearEditText.getText().toString());

            dateString = month + "-" + day + "-" + year;

            if (month < 0 || month > 12) dateFormatIsCorrect = false;
            if (day < 0 || day > 31) dateFormatIsCorrect = false;
            if (year < 0) dateFormatIsCorrect = false;
            if (!Utils.isLeapYear(year) && month == 2 && day == 29) dateFormatIsCorrect = false;

        } catch (Exception e) {
            dateFormatIsCorrect = false;
            e.printStackTrace();
        }

        try {
            Date newDate = format.parse(dateString);
            newTask.setUpdatedAt(newDate);
        } catch (ParseException e) {
            dateFormatIsCorrect = false;
            e.printStackTrace();
        }

        if (customDaysEditText.getText() != null && Long.parseLong(customDaysEditText.getText().toString()) <= 0L)
            inputFormatIsCorrect = false;

        if (dateFormatIsCorrect && inputFormatIsCorrect) {
            if (hundredSwitch.isChecked()) newTask.setCelebrate100Days(1);
            else newTask.setCelebrate100Days(0);

            if (anniversarySwitch.isChecked()) newTask.setCelebrateAnniversary(1);
            else newTask.setCelebrateAnniversary(0);

            if (customDaysEditText.getText() != null) newTask.setCustomCelebrateDay(Integer.parseInt(customDaysEditText.getText().toString()));
            else newTask.setCustomCelebrateDay(0);

            if (notificationSwitch.isChecked()) {
                newTask.setAllowNotification(1);
                Long currTimestamp = System.currentTimeMillis();

                // Create new notifications
                if (bufferTask == null) {
                    if (newTask.getCelebrate100Days() == 1) {
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT", getResources().getString(R.string.notification_text_100_days))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();

                        createOneTimeNotification(data, calculateDays(newTask.getUpdatedAt(), 100), "100DAY-" + newTask.getTimestamp());
                    }
                    if (newTask.getCelebrateAnniversary() == 1) {
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT", getResources().getString(R.string.notification_text_anniversary))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();

                        List<Long> delays = calculateAnniversary(newTask.getUpdatedAt());
                        for(int i = 0; i < 10; i++) {
                            createOneTimeNotification(data, delays.get(i), i + "-YEAR-" + newTask.getTimestamp());
                        }
                    }
                    if (customDaysEditText.getText() != null) {
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT",
                                        getResources().getString(R.string.notification_text_custom_prefix) +
                                                newTask.getCustomCelebrateDay() +
                                                getResources().getString(R.string.notification_text_custom_tail))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();
                        createOneTimeNotification(data, calculateDays(newTask.getUpdatedAt(), newTask.getCustomCelebrateDay()),newTask.getCustomCelebrateDay() + "-Custom-" + newTask.getTimestamp());
                    }
                }

                // add/update/delete old notifications
                else {
                    Log.d(TAG, newTask.toString());
                    newTask.setAllowNotification(0);
                    if (bufferTask.getCelebrate100Days() == 0 && newTask.getCelebrate100Days() == 1) {
                        // add new notification
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT", getResources().getString(R.string.notification_text_100_days))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();
                        createOneTimeNotification(data, calculateDays(newTask.getUpdatedAt(), 100), "100DAY-" + newTask.getTimestamp());
                    }
                    if (bufferTask.getCelebrate100Days() == 1 && newTask.getCelebrate100Days() == 0) {
                        // delete old notification
                        WorkManager.getInstance(this).cancelUniqueWork("100DAY-" + bufferTask.getTimestamp());
                    }

                    if (bufferTask.getCelebrateAnniversary() == 0 && newTask.getCelebrateAnniversary() == 1) {
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT", getResources().getString(R.string.notification_text_anniversary))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();
                        List<Long> delays = calculateAnniversary(newTask.getUpdatedAt());
                        for(int i = 0; i < 10; i++) {
                            createOneTimeNotification(data, delays.get(i), i + "-YEAR-" + newTask.getTimestamp());
                        }
                    }
                    if (bufferTask.getCelebrateAnniversary() == 1 && newTask.getCelebrateAnniversary() == 0) {
                        for(int i = 0; i < 10; i++) {
                            WorkManager.getInstance(this).cancelUniqueWork(i + "-YEAR-" + bufferTask.getTimestamp());
                        }
                    }

                    if (bufferTask.getCustomCelebrateDay() == 0 && newTask.getCustomCelebrateDay() > 0) {
                        Data data = new Data.Builder()
                                .putString("NOTIFICATION_TITLE", newTask.getDescription())
                                .putString("NOTIFICATION_TEXT",
                                        getResources().getString(R.string.notification_text_custom_prefix) +
                                                newTask.getCustomCelebrateDay() +
                                                getResources().getString(R.string.notification_text_custom_tail))
                                .putInt("NOTIFICATION_DRAWABLE", iconID)
                                .build();
                        createOneTimeNotification(data, calculateDays(newTask.getUpdatedAt(), newTask.getCustomCelebrateDay()), newTask.getCustomCelebrateDay() + "-Custom-" + newTask.getTimestamp());
                    }
                    if (bufferTask.getCustomCelebrateDay() > 0 && newTask.getCustomCelebrateDay() == 0) {
                        WorkManager.getInstance(this).cancelUniqueWork(bufferTask.getCustomCelebrateDay() + "-Custom-" + bufferTask.getTimestamp());
                    }
                }
            }
            // delete all old notifications
            else {
                if (bufferTask != null) {
                    if (bufferTask.getCelebrate100Days() == 1) {
                        WorkManager.getInstance(this).cancelUniqueWork("100DAY-" + bufferTask.getTimestamp());
                    }
                    if (bufferTask.getCelebrateAnniversary() == 1) {
                        for(int i = 0; i < 10; i++) {
                            WorkManager.getInstance(this).cancelUniqueWork(i + "-YEAR-" + bufferTask.getTimestamp());
                        }
                    }
                    if (bufferTask.getCustomCelebrateDay() > 0) {
                        WorkManager.getInstance(this).cancelUniqueWork(bufferTask.getCustomCelebrateDay() + "-Custom-" + bufferTask.getTimestamp());
                    }
                }
            }

            // insert or update new task object into the database
                // do nothing
            if (bufferTask != null && bufferTask.equals(newTask)) { }
                // use all new things
            else {
                final Task task = new Task(
                        newTask.getId(),
                        newTask.getDescription(),
                        newTask.getUpdatedAt(),
                        iconID,
                        newTask.getAllowNotification(),
                        newTask.getTimestamp(),
                        newTask.getCelebrate100Days(),
                        newTask.getCelebrateAnniversary(),
                        newTask.getCustomCelebrateDay());
                editViewModel.insertOrUpdateTask(this, task, newTask.getId());
                Log.d(TAG, task.toString());
            }
        }

        else {
            if (!dateFormatIsCorrect)
                Snackbar.make(submitBtn, "Wrong Date format, please enter a valid date", Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
            else
                Snackbar.make(submitBtn, "Wrong Input format, please enter a valid input", Snackbar.LENGTH_SHORT)
                        .setAction("Action", null).show();
        }
    }

    @Nullable
    @Override
    public IconPack getIconDialogIconPack() {
        return iconPack;
    }

    @Override
    public void onIconDialogIconsSelected(@NonNull IconDialog dialog, @NonNull List<Icon> icons) {
        // Show a toast with the list of selected icon IDs.
        StringBuilder sb = new StringBuilder();
        for (Icon icon : icons) {
            iconImage.setImageDrawable(icon.getDrawable());
            iconID = icon.getId();
            sb.append(iconID);
            sb.append(", ");
        }
        sb.delete(sb.length() - 2, sb.length());
        Snackbar.make(iconImage, "Icon selected: " + sb, Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    @Override
    public void onIconDialogCancelled() {}

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "channel_name";
            String description = "channel_description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    public void createOneTimeNotification(Data data, Long delay, String uniqueWorkName) {
        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(NotificationWorker.class)
                .setInitialDelay(delay, TimeUnit.SECONDS)
                .setInputData(data)
                .build();
        WorkManager.getInstance(this).enqueueUniqueWork(uniqueWorkName, ExistingWorkPolicy.REPLACE, oneTimeWorkRequest);
    }
}
