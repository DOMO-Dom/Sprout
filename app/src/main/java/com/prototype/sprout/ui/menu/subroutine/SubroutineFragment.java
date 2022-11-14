package com.prototype.sprout.ui.menu.subroutine;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.prototype.sprout.database.habits_with_subroutines.HabitWithSubroutinesViewModel;
import com.prototype.sprout.database.habits_with_subroutines.Habits;
import com.prototype.sprout.databinding.FragmentSubroutineBinding;
import com.prototype.sprout.ui.menu.subroutine.adapter.SubroutineParentAdapterItem;
import com.prototype.sprout.ui.menu.subroutine.ui.AddNewSubroutineFragment;

import java.util.List;

public class SubroutineFragment extends Fragment {

    private FragmentSubroutineBinding binding;

    private SubroutineParentAdapterItem parentAdapterItem;
    private List<Habits> habitsOnReform;
    private HabitWithSubroutinesViewModel habitWithSubroutinesViewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSubroutineBinding.inflate(inflater, container, false);

        habitWithSubroutinesViewModel = new ViewModelProvider(requireActivity()).get(HabitWithSubroutinesViewModel.class);
        binding.subroutineRecyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));

        habitsOnReform = habitWithSubroutinesViewModel.getAllHabitOnReform();
        parentAdapterItem = new SubroutineParentAdapterItem(requireActivity(), getViewLifecycleOwner(), habitsOnReform);
        binding.subroutineRecyclerView.setAdapter(parentAdapterItem);

        habitWithSubroutinesViewModel.getAllHabitOnReformLiveData().observe(getViewLifecycleOwner(), habitsOnReform -> {
            parentAdapterItem.setHabitsOnReform(habitsOnReform);
        });

        binding.fabSubroutine.setOnClickListener(view -> {
            FragmentManager fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(binding.subroutineFrameLayout.getId(), new AddNewSubroutineFragment())
                    .commit();
            binding.subroutineContainer.setVisibility(View.GONE);
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
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}