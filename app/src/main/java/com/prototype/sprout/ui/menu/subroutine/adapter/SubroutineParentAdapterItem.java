package com.prototype.sprout.ui.menu.subroutine.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStoreOwner;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.prototype.sprout.R;
import com.prototype.sprout.database.habits_with_subroutines.HabitWithSubroutines;
import com.prototype.sprout.database.habits_with_subroutines.HabitWithSubroutinesViewModel;
import com.prototype.sprout.database.habits_with_subroutines.Habits;
import com.prototype.sprout.database.habits_with_subroutines.Subroutines;

import java.util.List;

public class SubroutineParentAdapterItem extends RecyclerView.Adapter<SubroutineParentAdapterItem.ParentItemViewHolder> {

    private List<Habits> habitsOnReform;
    private LifecycleOwner lifecycleOwner;
    private HabitWithSubroutinesViewModel habitWithSubroutinesViewModel;

    //for continues scroll loop
    private RecyclerView.RecycledViewPool viewPool = new RecyclerView.RecycledViewPool();

    public SubroutineParentAdapterItem(ViewModelStoreOwner viewModelStoreOwner, LifecycleOwner lifecycleOwner, List<Habits> habitsOnReform) {
        this.lifecycleOwner = lifecycleOwner;
        this.habitsOnReform = habitsOnReform;
        this.habitWithSubroutinesViewModel = new ViewModelProvider(viewModelStoreOwner).get(HabitWithSubroutinesViewModel.class);
    }

    @NonNull
    @Override
    public ParentItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ParentItemViewHolder(
                LayoutInflater.from(parent.getContext()).inflate(R.layout.adapter_subroutine_parent_item, parent, false)
        );
    }

    @Override
    public void onBindViewHolder(@NonNull ParentItemViewHolder holder, int position) {
        long uid = holder.bindData(habitsOnReform.get(position));

        holder.childRecycleView.setLayoutManager(new LinearLayoutManager(holder.childRecycleView.getContext(), LinearLayoutManager.VERTICAL, false));

        List<Subroutines> habitWithSubroutines;

        habitWithSubroutines = habitWithSubroutinesViewModel.getAllSubroutinesOnReformHabit(uid);

        SubroutineChildAdapterItem childAdapterItem = new SubroutineChildAdapterItem(habitWithSubroutines);
        holder.childRecycleView.setAdapter(childAdapterItem);
        //Can be removed: for continuous flow of recycler
        holder.childRecycleView.setRecycledViewPool(viewPool);
        childAdapterItem.setHabitWithSubroutines(habitWithSubroutines);

        habitWithSubroutinesViewModel.getAllSubroutinesOnReformHabitLiveData(uid).observe(lifecycleOwner, new Observer<List<Subroutines>>() {
            @Override
            public void onChanged(List<Subroutines> subroutines) {
                childAdapterItem.setHabitWithSubroutines(subroutines);
            }
        });
    }

    @Override
    public int getItemCount() {
        return habitsOnReform.size();
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public void setHabitsOnReform(List<Habits> habitsOnReform) {
        this.habitsOnReform = habitsOnReform;
    }

    public class ParentItemViewHolder extends RecyclerView.ViewHolder {

        //Components Here
        Button habitsOnReformTitle;
        RecyclerView childRecycleView;

        public ParentItemViewHolder(@NonNull View itemView) {
            super(itemView);
            //Connecting ID
            habitsOnReformTitle = itemView.findViewById(R.id.habitOnReform_title);
            childRecycleView = itemView.findViewById(R.id.subroutine_child_recyclerview);
        }

        long bindData(Habits habitOnReform) {
            //Setting Data
            String buttonLabel = habitOnReform.getHabit() + " [ " + habitWithSubroutinesViewModel.getAllSubroutinesOnReformHabit(habitOnReform.getPk_habit_uid()).size() + " ]";

            habitsOnReformTitle.setText(buttonLabel);
            habitsOnReformTitle.setOnClickListener(view -> {
                childRecycleView.getVisibility();
                switch (childRecycleView.getVisibility()) {
                    case View.GONE:
                        childRecycleView.setVisibility(View.VISIBLE);
                        break;
                    default:
                        childRecycleView.setVisibility(View.GONE);
                        break;
                }
            });
            return habitOnReform.getPk_habit_uid();
        }
    }
}
