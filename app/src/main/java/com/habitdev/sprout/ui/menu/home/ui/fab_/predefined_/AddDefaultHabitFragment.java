package com.habitdev.sprout.ui.menu.home.ui.fab_.predefined_;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.habitdev.sprout.R;
import com.habitdev.sprout.database.habit.HabitWithSubroutinesViewModel;
import com.habitdev.sprout.database.habit.model.Habits;
import com.habitdev.sprout.database.habit.model.Subroutines;
import com.habitdev.sprout.databinding.FragmentAddDefaultHabitBinding;
import com.habitdev.sprout.enums.AppColor;
import com.habitdev.sprout.enums.HomeConfigurationKeys;
import com.habitdev.sprout.ui.menu.home.adapter.HomeAddDefaultHabitParentItemAdapter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AddDefaultHabitFragment extends Fragment {

    private FragmentAddDefaultHabitBinding binding;
    private static HabitWithSubroutinesViewModel habitWithSubroutinesViewModel;
    private static List<Habits> habitsList;
    private static Habits habit;
    private static List<Subroutines> subroutinesList;

    private static int current_selected_color;
    private static int old_selected_color;
    private static String color = AppColor.CLOUDS.getColor();

    private static Bundle savedInstanceState;
    private static boolean isOnRemoved;

    public interface OnAddDefaultReturnHome {
        void onAddDefaultHabitClickReturnHome();
    }

    private OnAddDefaultReturnHome mOnAddDefaultReturnHome;

    public void setmOnAddDefaultReturnHome(OnAddDefaultReturnHome mOnAddDefaultReturnHome) {
        this.mOnAddDefaultReturnHome = mOnAddDefaultReturnHome;
    }

    public AddDefaultHabitFragment() {
        habitsList = new ArrayList<>();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAddDefaultHabitBinding.inflate(inflater, container, false);
        habitWithSubroutinesViewModel = new ViewModelProvider(requireActivity()).get(HabitWithSubroutinesViewModel.class);

        if (savedInstanceState != null)
            AddDefaultHabitFragment.savedInstanceState = savedInstanceState;

        binding.habitDescriptionLbl.setVisibility(View.INVISIBLE);
        binding.subroutineLbl.setVisibility(View.INVISIBLE);

        colorSelect();
        setHabitColor();
        upDateHabitList();
        addHabitOnReform();
        onBackPress();
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (savedInstanceState != null) {
            current_selected_color = savedInstanceState.getInt(HomeConfigurationKeys.SELECTED_COLOR.getValue(), 0);
            old_selected_color = savedInstanceState.getInt(HomeConfigurationKeys.OLD_SELECTED_COLOR.getValue(), 0);
            clearSelected();
            setSelected_color();

            if (savedInstanceState.containsKey(HomeConfigurationKeys.SELECTED_HABIT.getValue())) {
                habit = (Habits) savedInstanceState.getSerializable(HomeConfigurationKeys.SELECTED_HABIT.getValue());
                binding.addFromDefaultHabitItems.setText(habit.getHabit());
                setContentView();
                binding.habitDescriptionLbl.setVisibility(View.VISIBLE);
                binding.subroutineLbl.setVisibility(View.VISIBLE);
            }
        }
    }

    private void upDateHabitList() {
        habitWithSubroutinesViewModel.getAllHabitListLiveData().observe(getViewLifecycleOwner(), habits -> {
            List<Habits> habitsLiveData = new ArrayList<>();
            for (Habits habit : habits)
                if (!habit.isOnReform() && !habit.isModifiable())
                    habitsLiveData.add(habit);
            habitsList = habitsLiveData;
            setDropDown();
        });
    }

    private void setDropDown() {
        List<String> habitTitles = new ArrayList<>();
        for (Habits habits : habitsList) habitTitles.add(habits.getHabit());

        if (habitTitles.isEmpty()) {
            if (binding.addFromDefaultHabitTextInputLayout.getVisibility() == View.VISIBLE)
                binding.addFromDefaultHabitTextInputLayout.setVisibility(View.GONE);
        } else {
            if (binding.addFromDefaultHabitTextInputLayout.getVisibility() == View.GONE)
                binding.addFromDefaultHabitTextInputLayout.setVisibility(View.VISIBLE);

            ArrayAdapter<String> adapterItem;
            adapterItem = new ArrayAdapter<>(requireContext(), R.layout.adapter_home_parent_habit_drop_down_item, habitTitles);
            binding.addFromDefaultHabitItems.setAdapter(adapterItem);

            binding.addFromDefaultHabitItems.setOnItemClickListener((adapterView, view, pos, id) -> {
                habit = habitsList.get(pos);
                setContentView();
            });
        }

        binding.addFromDefaultHabitItems.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (s.toString().trim().isEmpty()) {
                    binding.habitDescriptionLbl.setVisibility(View.INVISIBLE);
                    binding.subroutineLbl.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().trim().isEmpty()) {
                    binding.habitDescriptionLbl.setVisibility(View.INVISIBLE);
                    binding.subroutineCountLbl.setVisibility(View.INVISIBLE);
                    binding.habitDescription.setVisibility(View.INVISIBLE);
                    binding.subroutineLbl.setVisibility(View.INVISIBLE);
                    binding.subroutineCountLbl.setVisibility(View.INVISIBLE);
                    binding.habitSubroutinesRecyclerView.setVisibility(View.INVISIBLE);
                    habit = null;
                } else {
                    binding.habitDescriptionLbl.setVisibility(View.VISIBLE);
                    binding.subroutineCountLbl.setVisibility(View.VISIBLE);
                    binding.habitDescription.setVisibility(View.VISIBLE);
                    binding.subroutineLbl.setVisibility(View.VISIBLE);
                    binding.subroutineCountLbl.setVisibility(View.VISIBLE);
                    binding.habitSubroutinesRecyclerView.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void setContentView() {
        binding.habitDescription.setText(habit.getDescription());
        subroutinesList = habitWithSubroutinesViewModel.getAllSubroutinesOfHabit(habit.getPk_habit_uid());

        HomeAddDefaultHabitParentItemAdapter subroutineItemAdapter = new HomeAddDefaultHabitParentItemAdapter();
        subroutineItemAdapter.setOldSubroutineList(subroutinesList);

        binding.habitSubroutinesRecyclerView.setAdapter(subroutineItemAdapter);
        subroutineItemAdapter.setNewSubroutineList(subroutinesList);
        binding.subroutineCountLbl.setText(String.format(Locale.getDefault(), "%d", subroutinesList.size()));
    }

    private void addHabitOnReform() {
        binding.addHabitOnReformBtn.setOnClickListener(view -> {
            if (habit != null) {
                habitWithSubroutinesViewModel.updateHabit(new Habits(
                        habit.getPk_habit_uid(),
                        habit.getHabit(),
                        habit.getDescription(),
                        color,
                        true,
                        habit.isModifiable(),
                        habit.getAbstinence(),
                        0,
                        new SimpleDateFormat("EEEE, dd MMMM yyyy hh:mm:ss a", Locale.getDefault())
                                .format(new Date()),
                        subroutinesList.size(),
                        0
                ));
                habitWithSubroutinesViewModel.getAllHabitListLiveData().removeObservers(getViewLifecycleOwner());

                if (savedInstanceState != null) savedInstanceState = null;
                current_selected_color = 0;

                isOnRemoved = true;

                if (mOnAddDefaultReturnHome != null)
                    mOnAddDefaultReturnHome.onAddDefaultHabitClickReturnHome();
            } else {
                Toast.makeText(requireActivity(), "There is no habit selected", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void colorSelect() {
        /*
            No need to toggle hide because on edit mode, gotta disable
         */
//        binding.addFromDefaultHabitColorSelector.setOnClickListener(v -> {
//            if (binding.addFromDefaultHabitMiscellaneous.getVisibility() == View.GONE) {
//                binding.addFromDefaultHabitMiscellaneous.setVisibility(View.VISIBLE);
//            } else {
//                binding.addFromDefaultHabitMiscellaneous.setVisibility(View.GONE);
//            }
//        });

        binding.cloudMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(0);
            setSelected_color();
        });

        binding.alzarinMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(1);
            setSelected_color();
        });

        binding.amethystMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(2);
            setSelected_color();
        });

        binding.brightskyBlueMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(3);
            setSelected_color();
        });

        binding.nephritisMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(4);
            setSelected_color();
        });

        binding.sunflowerMisc.setOnClickListener(v -> {
            updateSelectedColorIndex(5);
            setSelected_color();
        });
    }

    private void setHabitColor() {
        if (habit != null) {
            if (habit.getColor().equals(AppColor.ALZARIN.getColor())) {
                current_selected_color = 1;
                setSelected_color();
            } else if (habit.getColor().equals(AppColor.AMETHYST.getColor())) {
                current_selected_color = 2;
                setSelected_color();
            } else if (habit.getColor().equals(AppColor.BRIGHT_SKY_BLUE.getColor())) {
                current_selected_color = 3;
                setSelected_color();
            } else if (habit.getColor().equals(AppColor.NEPHRITIS.getColor())) {
                current_selected_color = 4;
                setSelected_color();
            } else if (habit.getColor().equals(AppColor.SUNFLOWER.getColor())) {
                current_selected_color = 5;
                setSelected_color();
            } else {
                old_selected_color = 1;
                setSelected_color();
            }
        } else {
            old_selected_color = 1;
            setSelected_color();
        }
    }

    private void setSelected_color() {
        if (old_selected_color != current_selected_color) {
            int ic_check = R.drawable.ic_check;
            switch (current_selected_color) {
                case 1:
                    binding.alzarinSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_alzarin));
                    color = AppColor.ALZARIN.getColor();
                    break;
                case 2:
                    binding.amethystSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_amethyst));
                    color = AppColor.AMETHYST.getColor();
                    break;
                case 3:
                    binding.brightskyBlueSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_brightsky_blue));
                    color = AppColor.BRIGHT_SKY_BLUE.getColor();
                    break;
                case 4:
                    binding.nephritisSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_nephritis));
                    color = AppColor.NEPHRITIS.getColor();
                    break;
                case 5:
                    binding.sunflowerSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_sunflower));
                    color = AppColor.SUNFLOWER.getColor();
                    break;
                case 0:
                    binding.cloudSelected.setImageResource(ic_check);
                    setBackgroundColorIndicator(ContextCompat.getDrawable(requireContext(), R.drawable.background_color_indicator_clouds));
                    color = AppColor.CLOUDS.getColor();
                    break;
            }

            switch (old_selected_color) {
                case 1:
                    binding.alzarinSelected.setImageResource(R.color.TRANSPARENT);
                    break;
                case 2:
                    binding.amethystSelected.setImageResource(R.color.TRANSPARENT);
                    break;
                case 3:
                    binding.brightskyBlueSelected.setImageResource(R.color.TRANSPARENT);
                    break;
                case 4:
                    binding.nephritisSelected.setImageResource(R.color.TRANSPARENT);
                    break;
                case 5:
                    binding.sunflowerSelected.setImageResource(R.color.TRANSPARENT);
                    break;
                case 0:
                    binding.cloudSelected.setImageResource(R.color.TRANSPARENT);
                    break;
            }
        }
    }

    private void updateSelectedColorIndex(int newSelected) {
        old_selected_color = current_selected_color;
        current_selected_color = newSelected;
    }

    private void setBackgroundColorIndicator(Drawable backgroundNoteIndicator) {
        binding.addFromDefaultHabitColorSelector.setBackground(backgroundNoteIndicator);
    }

    private void clearSelected() {
        binding.alzarinSelected.setImageResource(R.color.TRANSPARENT);
        binding.amethystSelected.setImageResource(R.color.TRANSPARENT);
        binding.brightskyBlueSelected.setImageResource(R.color.TRANSPARENT);
        binding.nephritisSelected.setImageResource(R.color.TRANSPARENT);
        binding.sunflowerSelected.setImageResource(R.color.TRANSPARENT);
        binding.cloudSelected.setImageResource(R.color.TRANSPARENT);
    }

    private void onBackPress() {
        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (savedInstanceState != null) savedInstanceState = null;
                current_selected_color = 0;

                isOnRemoved = true;

                if (mOnAddDefaultReturnHome != null)
                    mOnAddDefaultReturnHome.onAddDefaultHabitClickReturnHome();
            }

        };
        requireActivity().getOnBackPressedDispatcher().addCallback(getViewLifecycleOwner(), callback);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(HomeConfigurationKeys.SELECTED_COLOR.getValue(), current_selected_color);
        outState.putInt(HomeConfigurationKeys.OLD_SELECTED_COLOR.getValue(), old_selected_color);

        if (habit != null) {
            outState.putSerializable(HomeConfigurationKeys.SELECTED_HABIT.getValue(), habit);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (!isOnRemoved) {
//            Log.d("tag", "onPause: added shared pref");
            SharedPreferences sharedPreferences = requireActivity().getSharedPreferences(HomeConfigurationKeys.HOME_ADD_DEFAULT_SHAREDPREF.getValue(), Context.MODE_PRIVATE);
            sharedPreferences.edit()
                    .putInt(HomeConfigurationKeys.SELECTED_COLOR.getValue(), current_selected_color)
                    .putInt(HomeConfigurationKeys.OLD_SELECTED_COLOR.getValue(), old_selected_color)
                    .apply();

//            Log.d("tag", "onPause: " + current_selected_color + ":" + old_selected_color);

            if (habit != null) {
                Log.d("tag", "onPause: " + binding.addFromDefaultHabitItems.getText().toString().trim());

                String json_habit = new Gson().toJson(habit);
                sharedPreferences.edit().
                        putString(HomeConfigurationKeys.SELECTED_HABIT.getValue(), json_habit)
                        .apply();
            }
        } else {
//            Log.d("tag", "onAddDefaultHabitClickReturnHome: Onclear");
            SharedPreferences sharedPreferences = requireContext().getSharedPreferences(HomeConfigurationKeys.HOME_ADD_DEFAULT_SHAREDPREF.getValue(), Context.MODE_PRIVATE);
            sharedPreferences.edit().clear().apply();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
//        Log.d("tag", "onResume: loaded shared pref");
        SharedPreferences sharedPreferences = requireContext().getSharedPreferences(HomeConfigurationKeys.HOME_ADD_DEFAULT_SHAREDPREF.getValue(), Context.MODE_PRIVATE);
        if (sharedPreferences.contains(HomeConfigurationKeys.SELECTED_COLOR.getValue()) && sharedPreferences.contains(HomeConfigurationKeys.OLD_SELECTED_COLOR.getValue())) {
            current_selected_color = sharedPreferences.getInt(HomeConfigurationKeys.SELECTED_COLOR.getValue(), 0);
            old_selected_color = sharedPreferences.getInt(HomeConfigurationKeys.OLD_SELECTED_COLOR.getValue(), 0);
            clearSelected();
            setSelected_color();

            if (sharedPreferences.contains(HomeConfigurationKeys.SELECTED_HABIT.getValue())) {
                String json_habit = sharedPreferences.getString(HomeConfigurationKeys.SELECTED_HABIT.getValue(), "");
                habit = new Gson().fromJson(json_habit, Habits.class);
                setContentView();
                binding.habitDescriptionLbl.setVisibility(View.VISIBLE);
                binding.subroutineLbl.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mOnAddDefaultReturnHome = null;
        habitWithSubroutinesViewModel = null;
        habit = null;
        habitsList = null;
        binding = null;
    }
}