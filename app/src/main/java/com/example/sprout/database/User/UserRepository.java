package com.example.sprout.database.User;

import android.app.Application;
import android.os.AsyncTask;

import androidx.lifecycle.LiveData;

import com.example.sprout.database.AppDatabase;

import java.util.List;

public class UserRepository {
    private UserDao userDao;
    private List<User> userList;
    private LiveData<List<User>> userListLiveData;
    private int userCount;
    private boolean isAssesstment;

    public UserRepository(Application application) {
        AppDatabase appDatabase = AppDatabase.getDbInstance(application);
        userDao = appDatabase.userDao();
        userList = userDao.getAllUser();
        userListLiveData = userDao.getAllUserLiveData();
        userCount = userDao.countAllSections();
        isAssesstment = userDao.isAssestment();
    }

    public void insert(User user) {
        new InsertUserAsyncTask(userDao).execute(user);
    }

    public void update(User user) {
        new UpdateUserAsyncTask(userDao).execute(user);
    }

    public void delete(User user) {
        new DeleteUserAsyncTask(userDao).execute(user);
    }

    public void deleteAll() {
        new DeleteAllUserAsyncTask(userDao).execute();
    }

    public LiveData<List<User>> getUserListLiveData() {
        return userListLiveData;
    }

    public List<User> getUserList(){
        return userList;
    }

    public int getUserCount(){
        return userCount;
    }

    public void setAssesstment(){
        userDao.setUserAssesstmentTrue();
    }

    public boolean getAssestment(){
        return isAssesstment;
    }

    private static class InsertUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        private InsertUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.insert(users[0]);
            return null;
        }
    }

    private static class UpdateUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        private UpdateUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.update(users[0]);
            return null;
        }
    }

    private static class DeleteUserAsyncTask extends AsyncTask<User, Void, Void> {

        private UserDao userDao;

        private DeleteUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(User... users) {
            userDao.delete(users[0]);
            return null;
        }
    }

    private static class DeleteAllUserAsyncTask extends AsyncTask<Void, Void, Void> {

        private UserDao userDao;

        private DeleteAllUserAsyncTask(UserDao userDao) {
            this.userDao = userDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            userDao.deleteAllUser();
            return null;
        }
    }
}
