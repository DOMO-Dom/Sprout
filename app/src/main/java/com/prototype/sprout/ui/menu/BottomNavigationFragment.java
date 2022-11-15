package com.prototype.sprout.ui.menu;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.prototype.sprout.R;
import com.prototype.sprout.databinding.FragmentBottomNavigationBinding;
import com.prototype.sprout.ui.menu.analytic.AnalyticFragment;
import com.prototype.sprout.ui.menu.home.HomeFragment;
import com.prototype.sprout.ui.menu.journal.JournalFragment;
import com.prototype.sprout.ui.menu.setting.SettingFragment;
import com.prototype.sprout.ui.menu.subroutine.SubroutineFragment;

import nl.joery.animatedbottombar.AnimatedBottomBar;

public class BottomNavigationFragment extends Fragment {

    private FragmentBottomNavigationBinding binding;
    private FragmentManager fragmentManager;
    private Fragment Home;
    private Fragment Subroutine;
    private Fragment Analytics;
    private Fragment Settings;
    private Fragment Journal;
    private int last_menu_selected, last_selected_index;

    public BottomNavigationFragment() {
        //Required Empty Constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentBottomNavigationBinding.inflate(inflater, container, false);

        Home = new HomeFragment();
        Subroutine = new SubroutineFragment();
        Analytics = new AnalyticFragment();
        Journal = new JournalFragment();
        Settings = new SettingFragment();

        SwipeListener swipeListener = new SwipeListener();

        if (savedInstanceState == null) {
            binding.bottomBar.selectTabById(R.id.tab_home, true);
            fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(binding.fragmentContainer.getId(), Home)
                    .commit();
            Toast.makeText(requireContext(), "NULL", Toast.LENGTH_SHORT).show();
            binding.bottomBar.selectTabAt(0, true);
        } else {
            setMenu(last_menu_selected, last_selected_index);
        }

        binding.bottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab LastTab, int newIndex, @NonNull AnimatedBottomBar.Tab newTab) {
                last_menu_selected = newTab.getId();
                last_selected_index = newIndex;
                setMenu(newTab.getId(), newIndex);
            }

            @Override
            public void onTabReselected(int lastIndex, @NonNull AnimatedBottomBar.Tab lastTab) {

            }
        });
        return binding.getRoot();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("last_menu_selected", last_menu_selected);
        outState.putInt("last_selected_index", last_selected_index);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        setSavedInstance(savedInstanceState);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setSavedInstance(savedInstanceState);
    }

    private void setSavedInstance(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            last_menu_selected = savedInstanceState.getInt("last_menu_selected");
            last_selected_index = savedInstanceState.getInt("last_selected_index");
        }
    }

    private void setMenu(int id, int tabID) {
        Fragment fragment;
        switch (id) {
            case R.id.tab_subroutine:
                fragment = Subroutine;
                break;
            case R.id.tab_analytic:
                fragment = Analytics;
                break;
            case R.id.tab_journal:
                fragment = Journal;
                break;
            case R.id.tab_settings:
                fragment = Settings;
                break;
            case R.id.tab_home:
            default:
                fragment = Home;
                break;
        }

        if (fragment != null) {
            fragmentManager = getChildFragmentManager();
            fragmentManager.beginTransaction().replace(binding.fragmentContainer.getId(), fragment)
                    .commit();
        }

        binding.bottomBar.selectTabAt(tabID, true);
    }

    /**
     * Listener on Touch Swipe Action
     */
    private class SwipeListener implements View.OnTouchListener {

        private final int SWIPE_THRESHOLD = 100;
        private final int SWIPE_VELOCITY_THRESHOLD = 100;
        private GestureDetector gestureDetector;

        public SwipeListener() {
            GestureDetector.SimpleOnGestureListener listener = new GestureDetector.SimpleOnGestureListener() {

                @Override
                public boolean onDown(MotionEvent e) {
                    return true;
                }

                @Override
                public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

                    boolean result = false;

                    try {
                        float diffY = e2.getY() - e1.getY();
                        float diffX = e2.getX() - e1.getX();
                        if (Math.abs(diffX) > Math.abs(diffY)) {
                            if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                                if (diffX > 0) {
                                    onSwipeRight();
                                } else {
                                    onSwipeLeft();
                                }
                            }
                            result = true;
                        } else if (Math.abs(diffY) > SWIPE_THRESHOLD && Math.abs(velocityY) > SWIPE_VELOCITY_THRESHOLD) {
                            if (diffY > 0) {
                                onSwipeBottom();
                            } else {
                                onSwipeTop();
                            }
                        }
                        result = true;

                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }

                    return result;
                }
            };

            gestureDetector = new GestureDetector(listener);
            gestureDetector.setContextClickListener(listener);
            binding.bottomNavView.setOnTouchListener(this);
        }

        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            return gestureDetector.onTouchEvent(motionEvent);
        }

        void onSwipeRight() {
            Toast.makeText(requireContext(), "Right Swipe", Toast.LENGTH_SHORT).show();
        }

        void onSwipeLeft() {
            Toast.makeText(requireContext(), "Left Swipe", Toast.LENGTH_SHORT).show();
        }

        void onSwipeTop() {
            Toast.makeText(requireContext(), "Top Swipe", Toast.LENGTH_SHORT).show();
        }

        void onSwipeBottom() {
            Toast.makeText(requireContext(), "Bottom Swipe", Toast.LENGTH_SHORT).show();
        }
    }
}