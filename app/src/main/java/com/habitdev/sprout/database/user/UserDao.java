package com.habitdev.sprout.database.user;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.habitdev.sprout.database.user.model.User;

import java.util.List;

@Dao
public interface UserDao {
    @Query("SELECT * FROM user")
    List<User> getAllUser();

    @Query("SELECT * FROM User WHERE uid=:pk_useruid")
    User getUserByUID(long pk_useruid);

    @Query("SELECT * FROM user")
    LiveData<List<User>> getAllUserLiveData();

    @Query("SELECT COUNT(*) FROM user")
    int countAllUser();

    @Query("SELECT assessment_done FROM user WHERE uid = 1")
    boolean isAssessment();

    @Query("SELECT onboarding_done FROM user WHERE uid = 1")
    boolean isOnBoarding();

    @Query("SELECT nickname FROM user WHERE uid = 1")
    LiveData<String> getNickname();

    @Insert
    void insert(User... users);

    @Update
    void update(User users);

    @Query("UPDATE user SET assessment_done = 1 WHERE uid = 1")
    void setUserAssessmentTrue();

    @Query("UPDATE user SET onboarding_done = 1 WHERE uid = 1")
    void setUserOnBoardingTrue();

    @Delete
    void delete(User user);

    @Query("DELETE FROM user")
    void deleteAllUser();
}
