package com.example.sprout.activity.startup;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.sprout.databinding.ActivityStartupIntroductionBinding;

public class Introduction extends AppCompatActivity {

    ActivityStartupIntroductionBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartupIntroductionBinding.inflate(getLayoutInflater());
        View bindingRoot = binding.getRoot();
        setContentView(bindingRoot);

//        binding.btnContinue.setOnClickListener(view -> startActivity((new Intent(this, Initial2.class))));
    }
}
