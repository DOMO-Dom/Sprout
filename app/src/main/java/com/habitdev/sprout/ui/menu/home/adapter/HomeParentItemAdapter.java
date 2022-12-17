package com.habitdev.sprout.ui.menu.home.adapter;

import android.annotation.SuppressLint;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habitdev.sprout.R;
import com.habitdev.sprout.database.habit.model.Habits;
import com.habitdev.sprout.enums.AppColor;
import com.habitdev.sprout.enums.TimeMilestone;
import com.habitdev.sprout.utill.DateTimeElapsedUtil;
import com.habitdev.sprout.utill.HabitDiffUtil;

import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class HomeParentItemAdapter extends RecyclerView.Adapter<HomeParentItemAdapter.HabitViewHolder> {

    private List<Habits> oldHabitList;

    public HomeParentItemAdapter() {}

    public interface HomeParentItemOnClickListener {
        void onItemClick(int position);
        void onClickHabitModify(Habits habit, int position);
        void onClickHabitRelapse(Habits habit);
        void onClickHabitDrop(Habits habit);
    }

    private HomeParentItemOnClickListener homeParentItemOnclickListener;

    public void setHomeParentItemOnclickListener(HomeParentItemOnClickListener homeParentItemOnclickListener) {
        this.homeParentItemOnclickListener = homeParentItemOnclickListener;
    }

    public void setOldHabitList(List<Habits> oldHabitList) {
        this.oldHabitList = oldHabitList;
    }

    @NonNull
    @Override
    public HomeParentItemAdapter.HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new HomeParentItemAdapter.HabitViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_home_parent_habit_item, parent, false), homeParentItemOnclickListener
        );
    }

    @Override
    public void onBindViewHolder(@NonNull HomeParentItemAdapter.HabitViewHolder holder, int position) {
        holder.bindHabit(oldHabitList.get(position), homeParentItemOnclickListener);

        DateTimeElapsedUtil dateTimeElapsedUtil = new DateTimeElapsedUtil(oldHabitList.get(holder.getAbsoluteAdapterPosition()).getDate_started());
        dateTimeElapsedUtil.calculateElapsedDateTime();

        Timer timer = new Timer(); //create new instance of timer
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                new Handler(Looper.getMainLooper()).post(() -> {
                    dateTimeElapsedUtil.calculateElapsedDateTime();
                    holder.daysOfAbstinence.setText(dateTimeElapsedUtil.getResult());
                });
            }
        }, 0, 1000);

        holder.drop.setOnClickListener(v -> {
            homeParentItemOnclickListener.onClickHabitDrop(oldHabitList.get(holder.getAbsoluteAdapterPosition()));
            timer.cancel();
            timer.purge();
        });

        if (dateTimeElapsedUtil.getElapsed_day() >= TimeMilestone.AVG_HABIT_BREAK_DAY.getDays()) {
            holder.upVote.setVisibility(View.VISIBLE);
            holder.downVote.setVisibility(View.VISIBLE);
        } else {
            holder.upVote.setVisibility(View.GONE);
            holder.downVote.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return oldHabitList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void setNewHabitList(List<Habits> newHabitList) {
        DiffUtil.Callback DIFF_CALLBACK = new HabitDiffUtil(oldHabitList, newHabitList);
        DiffUtil.DiffResult DIFF_CALLBACK_RESULT = DiffUtil.calculateDiff(DIFF_CALLBACK);
        oldHabitList = newHabitList;
        DIFF_CALLBACK_RESULT.dispatchUpdatesTo(this);
    }

    public static class HabitViewHolder extends RecyclerView.ViewHolder {

        final RelativeLayout itemContainer;
        final LinearLayout itemLayout;
        final TextView habitHeader, habitDescription, dateStarted, completedSubroutine, daysOfAbstinence, totalRelapse;
        final Button upVote, downVote, modify, relapse, drop;
        final Drawable cloud, amethyst, sunflower, nephritis, bright_sky_blue, alzarin;

        public HabitViewHolder(@NonNull View itemView, HomeParentItemOnClickListener HomeParentItemOnclickListener) {
            super(itemView);

            itemContainer = itemView.findViewById(R.id.adapter_home_parent_item_container);
            itemLayout = itemContainer.findViewById(R.id.adapter_home_parent_item_layout);
            habitHeader = itemView.findViewById(R.id.header);
            habitDescription = itemView.findViewById(R.id.home_item_on_click_habit_description);
            dateStarted = itemView.findViewById(R.id.date_started);
            completedSubroutine = itemView.findViewById(R.id.completed_subroutine);
            daysOfAbstinence = itemView.findViewById(R.id.home_item_on_click_habit_total_days_of_abstinence);
            totalRelapse = itemView.findViewById(R.id.total_relapse);

            upVote = itemView.findViewById(R.id.home_upvote_btn);
            downVote = itemView.findViewById(R.id.home_downvote_btn);

            modify = itemView.findViewById(R.id.home_modify_btn);
            relapse = itemView.findViewById(R.id.home_relapse_btn);
            drop = itemView.findViewById(R.id.home_drop_btn);

            itemContainer.setOnClickListener(v -> {
                if (HomeParentItemOnclickListener != null) {
                    int position = getBindingAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        HomeParentItemOnclickListener.onItemClick(position);
                    }
                }
            });

            cloud = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_cloud_selector);
            amethyst = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_amethyst_selector);
            sunflower = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_sunflower_selector);
            nephritis = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_nephritis_selector);
            bright_sky_blue = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_brightsky_blue_selector);
            alzarin = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_parent_item_view_alzarin_selector);
        }

        @SuppressLint("ClickableViewAccessibility")
        void bindHabit(Habits habit, HomeParentItemOnClickListener homeParentItemOnClickListener) {

            if (habit.getColor().equals(AppColor.ALZARIN.getColor())) {
                itemContainer.setBackground(alzarin);
            } else if (habit.getColor().equals(AppColor.AMETHYST.getColor())) {
                itemContainer.setBackground(amethyst);
            } else if (habit.getColor().equals(AppColor.BRIGHT_SKY_BLUE.getColor())) {
                itemContainer.setBackground(bright_sky_blue);
            } else if (habit.getColor().equals(AppColor.NEPHRITIS.getColor())) {
                itemContainer.setBackground(nephritis);
            } else if (habit.getColor().equals(AppColor.SUNFLOWER.getColor())) {
                itemContainer.setBackground(sunflower);
            } else {
                itemContainer.setBackground(cloud);
            }

            itemContainer.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    if (motionEvent.getAction() == MotionEvent.ACTION_DOWN){
                        itemContainer.setPadding(padding_inPx(10), padding_inPx(10), padding_inPx(0), padding_inPx(0));
                    } else if (motionEvent.getAction() == MotionEvent.ACTION_UP || motionEvent.getAction() == MotionEvent.ACTION_CANCEL){
                       itemContainer.setPadding(padding_inPx(0), padding_inPx(0), padding_inPx(0), padding_inPx(0));
                    }
                    return false;
                }
            });

            habitHeader.setText(habit.getHabit());

            if (habit.getDescription().trim().isEmpty()) {
                habitDescription.setVisibility(View.GONE);
            } else {
                habitDescription.setText(habit.getDescription());
            }

            dateStarted.setText(habit.getDate_started());
            completedSubroutine.setText(String.valueOf(habit.getCompleted_subroutine()));
            totalRelapse.setText((String.format(Locale.getDefault(), "%d", habit.getRelapse())));

            upVote.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), "Upvote", Toast.LENGTH_SHORT).show();
            });

            downVote.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), "DownVote", Toast.LENGTH_SHORT).show();
            });

            if (habit.isModifiable()) {
                modify.setOnClickListener(view -> {
                    homeParentItemOnClickListener.onClickHabitModify(habit, getAbsoluteAdapterPosition());
                });
            } else {
                modify.setVisibility(View.GONE);
            }

            relapse.setOnClickListener(view -> {
                homeParentItemOnClickListener.onClickHabitRelapse(habit);
                totalRelapse.setText(String.valueOf(habit.getRelapse()));
            });
        }

        int padding_inPx (int dp){
            final float scale = itemView.getResources().getDisplayMetrics().density;
            return (int) (dp * scale + 0.5f);
        }
    }
}
