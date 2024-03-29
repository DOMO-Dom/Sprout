package com.habitdev.sprout.database.assessment;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Transaction;
import androidx.room.Update;

import com.habitdev.sprout.database.assessment.model.Answer;
import com.habitdev.sprout.database.assessment.model.Assessment;
import com.habitdev.sprout.database.assessment.model.AssessmentRecord;
import com.habitdev.sprout.database.assessment.model.Choices;
import com.habitdev.sprout.database.assessment.model.Question;

import java.util.List;

@Dao
public interface AssessmentDao {
    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertQuestion(Question question);

    @Transaction
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertChoices(List<Choices> choices);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    long insertAssessmentRecord(AssessmentRecord assessmentRecord);

    @Transaction
    @Query("SELECT * FROM question INNER JOIN choices ON pk_questions_uid = fk_question_uid")
    LiveData<List<Assessment>> getAssessmentsListLiveData();

    @Transaction
    @Query("SELECT * FROM question INNER JOIN choices ON pk_questions_uid = fk_question_uid")
    List<Assessment> getAllAssessmentsList();

    @Query("SELECT * FROM question")
    List<Question> getAllQuestion();

    @Query("SELECT * FROM question ORDER BY RANDOM()")
    List<Question> getShuffledQuestions();

    @Query("SELECT * FROM Choices WHERE fk_question_uid=:pk_uid_question")
    List<Choices> getAllChoicesByUID(long pk_uid_question);

    @Query("SELECT * FROM Answer WHERE fk_assessment_record_uid=:uid")
    LiveData<List<Answer>> getAllAnswerListLiveData(long uid);

    @Query("SELECT * FROM Answer WHERE fk_assessment_record_uid=:uid")
    List<Answer> getAllAnswerList(long uid);

    @Query("SELECT * FROM Answer WHERE fk_assessment_record_uid=:record_uid AND fk_question_uid=:question_uid")
    Answer getAnswerByFkQuestionUID(long record_uid, long question_uid);

    @Query("SELECT COUNT(fk_question_uid) FROM Answer WHERE fk_assessment_record_uid=:record_uid AND fk_question_uid=:question_uid")
    long doesAnswerExist(long record_uid, long question_uid);

    @Query("SELECT * FROM AssessmentRecord")
    LiveData<List<AssessmentRecord>> getAssessmentRecordLiveData();

    @Query("SELECT * FROM AssessmentRecord")
    List<AssessmentRecord> getAssessmentRecord();

    @Query("SELECT * FROM AssessmentRecord WHERE pk_assessment_record_uid=:uid")
    AssessmentRecord getAssessmentRecordByUID(long uid);

    @Query("SELECT pk_assessment_record_uid FROM AssessmentRecord WHERE is_completed=0")
    long getUncompletedAssessmentRecordUID();

    @Query("SELECT COUNT(*) FROM AssessmentRecord WHERE is_completed = 0")
    int getUncompletedAssessmentRecordCount();

    @Query("SELECT * FROM AssessmentRecord WHERE pk_assessment_record_uid = (SELECT MAX(pk_assessment_record_uid) FROM AssessmentRecord WHERE is_completed = 1)")
    AssessmentRecord getLatestCompletedAssessmentRecord();

    @Query("DELETE FROM answer WHERE fk_assessment_record_uid=:uid")
    void deleteAnswersByAssessmentRecordUid(long uid);

    @Update
    void updateQuestion(Question question);

    @Update
    void updateChoice(Choices choices);

    @Update
    void updateAnswer(Answer... answers);

    @Update
    void updateAssessmentRecord(AssessmentRecord...assessmentRecord);

    @Insert
    void insertAnswer(Answer... answers);

    @Delete
    void deleteQuestion(Question question);

    @Delete
    void deleteChoice(Choices choices);

    @Delete
    void deleteAssessmentRecord(AssessmentRecord assessmentRecord);
}
