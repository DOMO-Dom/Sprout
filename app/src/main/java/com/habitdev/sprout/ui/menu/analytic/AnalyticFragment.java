package com.habitdev.sprout.ui.menu.analytic;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.snackbar.Snackbar;
import com.habitdev.sprout.R;
import com.habitdev.sprout.database.habit.HabitWithSubroutinesViewModel;
import com.habitdev.sprout.database.habit.model.Habits;
import com.habitdev.sprout.database.user.UserViewModel;
import com.habitdev.sprout.database.user.model.User;
import com.habitdev.sprout.databinding.FragmentAnalyticBinding;
import com.habitdev.sprout.ui.menu.analytic.adapter.AnalyticParentItemAdapter;
import com.habitdev.sprout.ui.menu.analytic.ui.AnalyticItemOnClickFragment;
import com.habitdev.sprout.utill.DateTimeElapsedUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class AnalyticFragment extends Fragment implements AnalyticParentItemAdapter.onItemClick {

    private FragmentAnalyticBinding binding;
    private UserViewModel userViewModel;
    private HabitWithSubroutinesViewModel habitWithSubroutinesViewModel;
    private List<Habits> habitsList;
    private final AnalyticParentItemAdapter analyticParentItemAdapter = new AnalyticParentItemAdapter();
    private final AnalyticItemOnClickFragment analyticItemOnClickFragment = new AnalyticItemOnClickFragment();

    public AnalyticFragment() {
        habitsList = new ArrayList<>();
        analyticParentItemAdapter.setmOnItemClick(this);
    }

    @Override
    public void analyticOnItemClick(int position) {
        Log.d("tag", "analyticOnItemClick: " + habitsList.get(position).toString());
        show(position);
        analyticItemOnClickFragment.setHabit(habitsList.get(position));
        analyticItemOnClickFragment.setPosition(position);
        getChildFragmentManager()
                .beginTransaction()
                .addToBackStack(AnalyticFragment.this.getTag())
                .replace(binding.analyticFrameLayout.getId(), analyticItemOnClickFragment)
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_MATCH_ACTIVITY_OPEN)
                .commit();
        binding.analysisContainer.setVisibility(View.GONE);
    }

    private void show(int position){
        Snackbar.make(binding.getRoot(), habitsList.get(position).getHabit() , Snackbar.LENGTH_INDEFINITE)
                .setAction("Dismiss", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //auto dismiss when clicked

                    }
                })
                .setActionTextColor(ContextCompat.getColor(requireContext(), R.color.PETER_RIVER))
                .setTextColor(getResources().getColor(R.color.NIGHT))
                .setBackgroundTint(getResources().getColor(R.color.CLOUDS))
                .show();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAnalyticBinding.inflate(inflater, container, false);
        userViewModel = new ViewModelProvider(requireActivity()).get(UserViewModel.class);
        habitWithSubroutinesViewModel = new ViewModelProvider(requireActivity()).get(HabitWithSubroutinesViewModel.class);
        setDateSinceInstalledElapsedTime();
        setRecyclerViewAdapter();
        onBackPress();
        return binding.getRoot();
    }

    private void setDateSinceInstalledElapsedTime() {
        User user = userViewModel.getUserByUID(1);
        String date = user.getDateInstalled();
        DateTimeElapsedUtil dateTimeElapsedUtil = new DateTimeElapsedUtil(date);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (binding != null) {
                    new Handler(Looper.getMainLooper()).post(() -> {
                        dateTimeElapsedUtil.calculateElapsedDateTime();
                        binding.dateSinceInstalled.setText(dateTimeElapsedUtil.getResult());
                    });
                } else {
                    timer.cancel();
                    timer.purge();
                }
            }
        }, 0, 1000);
    }

    private void setRecyclerViewAdapter() {
        habitsList = habitWithSubroutinesViewModel.getAllHabitOnReform();
        analyticParentItemAdapter.setOldHabitsList(habitsList);
        analyticParentItemAdapter.setHabitWithSubroutinesViewModel(habitWithSubroutinesViewModel);
        analyticParentItemAdapter.setmOnItemClick(this);
        binding.analyticHabitOnReformRecyclerView.setAdapter(analyticParentItemAdapter);
        setRecyclerViewAdapterObserver();
    }

    private void setRecyclerViewAdapterObserver() {
        habitWithSubroutinesViewModel.getAllHabitOnReformLiveData().observe(getViewLifecycleOwner(), new Observer<List<Habits>>() {
            @Override
            public void onChanged(List<Habits> habits) {
                analyticParentItemAdapter.setNewHabitList(habits);
            }
        });
    }

    private void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                requireActivity().moveTaskToBack(true);
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        userViewModel = null;
        habitWithSubroutinesViewModel.getAllHabitOnReformLiveData().removeObservers(getViewLifecycleOwner());
        habitWithSubroutinesViewModel = null;
        binding = null;
    }
}