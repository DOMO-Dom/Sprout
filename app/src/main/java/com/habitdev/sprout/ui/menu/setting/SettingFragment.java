package com.habitdev.sprout.ui.menu.setting;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.habitdev.sprout.database.user.UserViewModel;
import com.habitdev.sprout.databinding.FragmentSettingBinding;
import com.habitdev.sprout.ui.menu.setting.ui.AboutUsFragment;
import com.habitdev.sprout.ui.menu.setting.ui.LearnMoreFragment;
import com.habitdev.sprout.ui.menu.setting.ui.ProfileFragment;
import com.habitdev.sprout.ui.menu.setting.ui.TechStackInfoFragment;
import com.habitdev.sprout.ui.menu.setting.ui.TerminalFragment;
import com.habitdev.sprout.ui.menu.setting.ui.ThemeFragment;

public class SettingFragment extends Fragment {

    private FragmentSettingBinding binding;
    private FragmentManager fragmentManager;
    private UserViewModel userViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSettingBinding.inflate(inflater, container, false);

        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);

        userViewModel.getUserNickname().observe(getViewLifecycleOwner(), nickname -> {
            binding.nickname.setText(nickname);
        });

        fragmentManager = getChildFragmentManager();

        binding.editProfile.setOnClickListener(v -> {
            ProfileFragment profileFragment = new ProfileFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), profileFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);
        });

        binding.selectThemeBtn.setOnClickListener(v -> {
            ThemeFragment themeFragment = new ThemeFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), themeFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);
        });

        binding.aboutUsBtn.setOnClickListener(v -> {
            AboutUsFragment aboutUsFragment = new AboutUsFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), aboutUsFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);
        });

        binding.learnMoreBtn.setOnClickListener(v -> {
            LearnMoreFragment learnMoreFragment = new LearnMoreFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), learnMoreFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);

        });

        binding.techStackInfoBtn.setOnClickListener(v -> {
            TechStackInfoFragment techStackInfoFragment = new TechStackInfoFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), techStackInfoFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);
        });

        binding.terminalBtn.setOnClickListener(view -> {
            TerminalFragment terminalFragment = new TerminalFragment();
            fragmentManager.beginTransaction()
                    .replace(binding.settingFrameLayout.getId(), terminalFragment)
                    .commit();
            binding.settingContainer.setVisibility(View.GONE);
        });

        onBackPress();
        return binding.getRoot();
    }

    private void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                //Do Something
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentManager = null;
        userViewModel = null;
        binding = null;
    }
}