package com.habitdev.sprout.ui.onBoarding;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.habitdev.sprout.R;
import com.habitdev.sprout.databinding.FragmentIntroductionBinding;

public class IntroductionFragment extends Fragment {

    //View Binding
    FragmentIntroductionBinding binding;

    public IntroductionFragment() {}

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentIntroductionBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        binding.btnContinue.setOnClickListener(view ->
                Navigation.findNavController(view).navigate(R.id.action_navigate_from_introduction_to_greetings, getArguments()));
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}