package com.habitdev.sprout.database.habit;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.habitdev.sprout.database.habit.model.Habits;
import com.habitdev.sprout.database.habit.model.Subroutines;

import java.util.List;

@Dao
public interface HabitWithSubroutinesDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertHabit(Habits habits);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertSubroutines(List<Subroutines> subroutines);

    @Query("SELECT * FROM Habits WHERE on_reform = 1")
    List<Habits> getAllHabitOnReform();

    @Query("SELECT * FROM Habits WHERE on_reform = 1")
    LiveData<List<Habits>> getAllHabitOnReformLiveData();

    @Transaction
    @Query("SELECT * FROM Habits INNER JOIN subroutines ON Habits.pk_habit_uid = subroutines.fk_habit_uid WHERE Habits.on_reform = 1")
    List<HabitWithSubroutines> getAllHabitsOnReformWithSubroutines();

    @Transaction
    @Query("SELECT * FROM Habits INNER JOIN subroutines ON Habits.pk_habit_uid = subroutines.fk_habit_uid WHERE Habits.on_reform = 1")
    LiveData<List<HabitWithSubroutines>> getAllHabitsOnReformWithSubroutinesLiveData();

    @Query("SELECT pk_habit_uid FROM Habits WHERE Habits.on_reform = 1")
    List<Long> getAllHabitsOnReformUID();

    @Query("SELECT * FROM subroutines WHERE fk_habit_uid=:uid")
    List<Subroutines> getAllSubroutinesOfHabit(long uid);

    @Query("SELECT * FROM subroutines WHERE fk_habit_uid=:uid")
    LiveData<List<Subroutines>> getAllSubroutinesOfHabitLiveData(long uid);

    @Query("SELECT * FROM Habits")
    LiveData<List<Habits>> getAllHabitListLiveData();

    @Query("SELECT * FROM Habits")
    List<Habits> getAllHabitList();

    @Query("SELECT COUNT(*) FROM Habits WHERE Habits.on_reform = 1")
    LiveData<Long> getHabitOnReformCount();

//    @Transaction
//    @Query("SELECT * FROM Habits INNER JOIN subroutines ON Habits.pk_habit_uid = subroutines.fk_habit_uid WHERE Habits.modifiable = 0")
//    LiveData<List<HabitWithSubroutines>> getAllUserDefinedHabitsSubroutinesLiveData();

    @Query("SELECT * FROM Habits WHERE modifiable = 1")
    LiveData<List<Habits>> getAllUserDefinedHabitListLiveData();

    @Update
    void updateHabit(Habits habit);
//
//    @Update
//    void updateSubroutine(Subroutines subroutine);
//
//    @Delete
//    long deleteHabit(Habits habit);

//    @Delete
//    void deleteSubroutine(Subroutines subroutine);
}
