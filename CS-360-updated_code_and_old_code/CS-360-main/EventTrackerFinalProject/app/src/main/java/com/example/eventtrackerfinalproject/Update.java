package com.example.eventtrackerfinalproject;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

public class Update extends AppCompatActivity {

    private EditText titleInput, descriptionInput;
    private Button updateButton, deleteButton, timeButton, dateButton;
    private String id, title, description, date, time;
    private ReminderManager reminderManager;
    private MainDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        // Initialize views
        initViews();

        // Initialize helpers
        reminderManager = new ReminderManager(this);
        db = new MainDatabase(this);

        // Set up listeners
        setupListeners();

        // Get and set intent data
        getAndSetIntentData();
    }

    private void initViews() {
        titleInput = findViewById(R.id.title_input_update);
        descriptionInput = findViewById(R.id.description_input_update);
        dateButton = findViewById(R.id.btn_Date_Update);
        timeButton = findViewById(R.id.btn_Time_Update);
        updateButton = findViewById(R.id.add_new_update);
        deleteButton = findViewById(R.id.edit_button);
    }

    private void setupListeners() {
        // Date and time pickers
        timeButton.setOnClickListener(v -> selectTime());
        dateButton.setOnClickListener(v -> selectDate());

        // Text watchers to enable/disable update button
        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                validateInputs();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        };
        
        titleInput.addTextChangedListener(textWatcher);
        descriptionInput.addTextChangedListener(textWatcher);

        // Update event
        updateButton.setOnClickListener(v -> updateEvent());

        // Delete event
        deleteButton.setOnClickListener(v -> confirmDelete());
    }

    private void getAndSetIntentData() {
        Intent intent = getIntent();
        if (intent.hasExtra(Constants.EXTRA_ID) && intent.hasExtra(Constants.EXTRA_TITLE) &&
            intent.hasExtra(Constants.EXTRA_DESCRIPTION) && intent.hasExtra(Constants.EXTRA_DATE) &&
            intent.hasExtra(Constants.EXTRA_TIME)) {

            // Get data from intent
            id = intent.getStringExtra(Constants.EXTRA_ID);
            title = intent.getStringExtra(Constants.EXTRA_TITLE);
            description = intent.getStringExtra(Constants.EXTRA_DESCRIPTION);
            date = intent.getStringExtra(Constants.EXTRA_DATE);
            time = intent.getStringExtra(Constants.EXTRA_TIME);

            // Set data to views
            titleInput.setText(title);
            descriptionInput.setText(description);
            dateButton.setText(date);
            timeButton.setText(time);
        } else {
            Toast.makeText(this, "No data to update", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateEvent() {
        String updatedTitle = titleInput.getText().toString().trim();
        String updatedDescription = descriptionInput.getText().toString().trim();
        String updatedDate = dateButton.getText().toString().trim();
        String updatedTime = timeButton.getText().toString().trim();

        // Update database
        db.updateData(id, updatedTitle, updatedDescription, updatedDate, updatedTime);

        // Set alarm
        try {
            reminderManager.addToQueue(new Reminder(id, updatedTitle, updatedDate, updatedDescription, updatedTime));
            reminderManager.processNextReminder();
            Toast.makeText(this, "Event updated", Toast.LENGTH_SHORT).show();
        } catch (ParseException e) {
            Toast.makeText(this, "Failed to set alarm", Toast.LENGTH_SHORT).show();
            e.printStackTrace();
        }

        finish();
    }

    private void confirmDelete() {
        new AlertDialog.Builder(this)
                .setTitle("Delete " + title + "?")
                .setMessage("Are you sure you want to delete " + title + "?")
                .setPositiveButton("Yes", (dialog, which) -> {
                    db.deleteOneRow(id);
                    finish();
                })
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .create()
                .show();
    }

    private void validateInputs() {
        String title = titleInput.getText().toString().trim();
        String description = descriptionInput.getText().toString().trim();
        String date = dateButton.getText().toString().trim();
        String time = timeButton.getText().toString().trim();

        updateButton.setEnabled(!title.isEmpty() && !description.isEmpty() &&
                !date.isEmpty() && !time.isEmpty());
    }

    private void selectTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, selectedHour, selectedMinute) -> {
                    time = String.format("%02d:%02d", selectedHour, selectedMinute);
                    timeButton.setText(DateUtils.formatTime(selectedHour, selectedMinute));
                }, hour, minute, false);
        timePickerDialog.show();
    }

    private void selectDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    date = String.format("%d-%d-%d", selectedDay, selectedMonth + 1, selectedYear);
                    dateButton.setText(date);
                }, year, month, day);
        datePickerDialog.show();
    }
}
