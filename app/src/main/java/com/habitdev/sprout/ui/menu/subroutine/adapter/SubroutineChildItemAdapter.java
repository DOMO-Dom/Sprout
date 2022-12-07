package com.habitdev.sprout.ui.menu.subroutine.adapter;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.habitdev.sprout.R;
import com.habitdev.sprout.database.habit.model.Subroutines;
import com.habitdev.sprout.enums.AppColor;
import com.habitdev.sprout.utill.SubroutineDiffUtil;

import java.util.List;

public class SubroutineChildItemAdapter extends RecyclerView.Adapter<SubroutineChildItemAdapter.ChildItemViewHolder> {

    List<Subroutines> oldSubroutineList;

    public SubroutineChildItemAdapter() {

    }

    public void setOldSubroutineList(List<Subroutines> oldSubroutineList) {
        this.oldSubroutineList = oldSubroutineList;
    }

    @NonNull
    @Override
    public SubroutineChildItemAdapter.ChildItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ChildItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_subroutine_child_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull SubroutineChildItemAdapter.ChildItemViewHolder holder, int position) {
        holder.bindDate(oldSubroutineList.get(position));
    }

    @Override
    public int getItemCount() {
        return oldSubroutineList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setNewSubroutineList(List<Subroutines> newSubroutineList) {
        DiffUtil.Callback DIFF_CALLBACK = new SubroutineDiffUtil(oldSubroutineList, newSubroutineList);
        DiffUtil.DiffResult DIFF_CALLBACK_RESULT = DiffUtil.calculateDiff(DIFF_CALLBACK);
        this.oldSubroutineList = newSubroutineList;
        DIFF_CALLBACK_RESULT.dispatchUpdatesTo(this);
    }

    public static class ChildItemViewHolder extends RecyclerView.ViewHolder{

        RelativeLayout itemLayout;
        TextView Title, Description;
        Button UpVote, DownVote, MarkAsDone;
        Drawable cloud, amethyst, sunflower, nephritis, bright_sky_blue, alzarin;

        public ChildItemViewHolder(@NonNull View itemView) {
            super(itemView);
            itemLayout = itemView.findViewById(R.id.subroutine_child_item_layout);
            Title = itemView.findViewById(R.id.subroutine);
            Description = itemView.findViewById(R.id.home_item_on_click_habit_description);

            UpVote = itemView.findViewById(R.id.btn_upvote_subroutine);
            DownVote = itemView.findViewById(R.id.btn_downvote_subroutine);
            MarkAsDone = itemView.findViewById(R.id.mark_as_done);

            cloud = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_cloud_selector);
            amethyst = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_amethyst_selector);
            sunflower = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_sunflower_selector);
            nephritis = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_nephritis_selector);
            bright_sky_blue = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_brightsky_blue_selector);
            alzarin = ContextCompat.getDrawable(itemView.getContext(), R.drawable.background_btn_alzarin_selector);
        }

        void bindDate(Subroutines subroutine) {
            if (subroutine.getColor().equals(AppColor.ALZARIN.getColor())) {
                itemLayout.setBackground(alzarin);
            } else if (subroutine.getColor().equals(AppColor.AMETHYST.getColor())) {
                itemLayout.setBackground(amethyst);
            } else if (subroutine.getColor().equals(AppColor.BRIGHT_SKY_BLUE.getColor())) {
                itemLayout.setBackground(bright_sky_blue);
            } else if (subroutine.getColor().equals(AppColor.NEPHRITIS.getColor())) {
                itemLayout.setBackground(nephritis);
            } else if (subroutine.getColor().equals(AppColor.SUNFLOWER.getColor())) {
                itemLayout.setBackground(sunflower);
            } else {
                itemLayout.setBackground(cloud);
            }

            Title.setText(subroutine.getSubroutine());
            Description.setText(subroutine.getDescription());

            UpVote.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), "UpVote", Toast.LENGTH_SHORT).show();
            });

            DownVote.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), "DownVote", Toast.LENGTH_SHORT).show();
            });

            MarkAsDone.setOnClickListener(view -> {
                Toast.makeText(itemView.getContext(), "Marked as Done", Toast.LENGTH_SHORT).show();
            });
        }
    }
}
