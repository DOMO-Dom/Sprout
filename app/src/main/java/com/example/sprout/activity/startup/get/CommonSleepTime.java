package com.example.sprout.activity.startup.get;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprout.activity.startup.Greetings;
import com.example.sprout.databinding.ActivityStartupGetCommonSleepTimeBinding;

public class CommonSleepTime extends AppCompatActivity {

    ActivityStartupGetCommonSleepTimeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartupGetCommonSleepTimeBinding.inflate(getLayoutInflater());
        View bindingRoot = binding.getRoot();
        setContentView(bindingRoot);

        setInitialTime();

        binding.btnContinue.setOnClickListener(view -> {
            Bundle bundle = getIntent().getBundleExtra("bundle"); // get the nested bundle, or it'll be getIntent().getExtras().getBundle("bundle")
            bundle.putInt("sleepHour", binding.SleepTimePicker.getHour());
            bundle.putInt("sleepMinute", binding.SleepTimePicker.getMinute());
            startActivity((new Intent(this, Greetings.class).putExtra("bundle", bundle)));
        });
    }

    private void setInitialTime() {
        binding.SleepTimePicker.setHour(20);
        binding.SleepTimePicker.setMinute(15);
    }
}