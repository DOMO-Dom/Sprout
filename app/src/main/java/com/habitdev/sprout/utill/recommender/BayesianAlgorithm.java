package com.habitdev.sprout.utill.recommender;

import android.util.Log;

import com.habitdev.sprout.database.habit.firestore.HabitFireStoreRepository;
import com.habitdev.sprout.database.habit.model.firestore.HabitFireStore;
import com.habitdev.sprout.database.habit.model.firestore.SubroutineFireStore;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for calculating ratings using the Bayesian average algorithm.
 * The algorithm uses a constant value m as a prior, and the average rating R of all items in the dataset.
 * The formula for calculating the rating of an item is: (v * R + m * C) / (v + m),
 * where v is the number of total votes (upvotes + downvotes) for the item,
 * R is the average rating of all items in the dataset,
 * m is the constant prior value,
 * and C is the average of upvotes and downvotes.
 */
public class BayesianAlgorithm {

    private static List<HabitFireStore> habits; //item The HabitFireStore to calculate the rating for.
    private static List<SubroutineFireStore> subroutines;
    private static double R;
    private static double C;
    private static final int M = 5; //  m The constant prior value for minimum vote

    public BayesianAlgorithm() {
        // List of HabitFireStore to initialize the ratings for.
        BayesianAlgorithm.habits = getHabitFireStoreList();
    }

    /**
     * Initializes the ratings for all items in the dataset using the
     * proportion of upvotes to total votes (upvotes + downvotes) as a percentage.
     */
    public static void initializeRatings() {
        if (habits == null) {
            Log.d("tag", "initializeRatings: Items is null");
        }
        if (habits.isEmpty()) {
            Log.d("Tag", "initializeRatings: Items is empty");
        }

        for (HabitFireStore item : habits) {
            item.setRating((double) item.getUpvote() / (item.getUpvote() + item.getDownvote()));
        }
    }

    /**
     * Calculates the rating of an item using the Bayesian average algorithm.
     */
    public static void calculateRating() {
        if (habits == null) {
            Log.d("tag", "initializeRatings: Items is null");
        }
        if (habits.isEmpty()) {
            Log.d("Tag", "initializeRatings: Items is empty");
        }

        calculateR();
        calculateC();
        for (HabitFireStore item : habits) {
            double v = item.getUpvote() + item.getDownvote();
            double rating = (v * R + M * C) / (v + M);
            item.setRating(rating);
        }
    }

    public static List<HabitFireStore> getHabitFireStoreList() {
        HabitFireStoreRepository habitFireStoreRepository = new HabitFireStoreRepository();
        final List<HabitFireStore>[] habits = new List[]{new ArrayList<>()};
        habitFireStoreRepository.fetchData(new HabitFireStoreRepository.FetchCallback() {
            @Override
            public void onFetchHabitSuccess(List<HabitFireStore> habitFireStoreList) {
                habits[0] = habitFireStoreList;
                Log.d("tag", "onFetchHabitSuccess: ");
            }

            @Override
            public void onFetchHabitFailure(Exception e) {
                Log.e("tag", "onFetchHabitFailure: " + e.getMessage());
            }
        });

        return habits[0];
    }

    /**
     * This method calculates the average rating of all items in the dataset using the proportion of upvotes to total votes (upvotes + downvotes) as a percentage.
     * The method iterates through the list of items,adds up the total number of votes (upvotes + downvotes) and the total rating (upvotes - downvotes) for all items,
     * then calculates the average rating by dividing the total rating by the total number of votes.
     * Update: R double, The average rating of all items in the dataset which will be used in the Bayesian algorithm.
     */
    public static void calculateR() {
        if (checkItems()) return;

        int totalVotes = 0;
        double totalRating = 0;
        for (HabitFireStore item : habits) {
            totalVotes += item.getUpvote() + item.getDownvote();
            totalRating += item.getUpvote() - item.getDownvote();
        }
        R = totalRating / totalVotes;
    }

    /**
     * This method calculates the value of C, which is used as a prior value in the Bayesian algorithm.
     * The method iterates through the list of items,
     * adds up the total number of upvotes and total number of votes (upvotes + downvotes) for all items,
     * then calculates the value of C by dividing the total number of upvotes by the total number of votes.
     * Update: C double, The value of C which is used as a prior value in the Bayesian algorithm.
     */
    private static void calculateC() {
        double totalUpvotes = 0;
        double totalVotes = 0;
        for (HabitFireStore item : habits) {
            totalUpvotes += item.getUpvote();
            totalVotes += item.getUpvote() + item.getDownvote();
        }
        C = totalUpvotes / totalVotes;
    }

    private static boolean checkItems() {
        if (habits == null) {
            Log.d("tag", "calculateR: item is null");
            return true;
        }

        if (habits.isEmpty()) {
            Log.d("tag", "calculateR: item is empty");
            return true;
        }
        return false;
    }
}