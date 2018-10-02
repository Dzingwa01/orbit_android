package com.carefulcollections.gandanga.mishift.Helpers;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;

import com.carefulcollections.gandanga.mishift.Models.Comment;
import com.carefulcollections.gandanga.mishift.Models.Message;
import com.carefulcollections.gandanga.mishift.Models.Shift;
import com.carefulcollections.gandanga.mishift.Models.ShiftRoom;
import com.carefulcollections.gandanga.mishift.Models.TaskRoom;
import com.carefulcollections.gandanga.mishift.Models.Team;

import java.util.List;

/**
 * Created by Gandanga on 2018-09-07.
 */
@Dao
public interface DaoAccess {

    @Query("SELECT * FROM shifts")
    List<ShiftRoom> getAllShifts();

    @Query("SELECT * FROM tasks")
    List<TaskRoom> getAllTasks();

    @Query("SELECT * FROM teams")
    List<Team> getAllTeams();

    @Query("SELECT * FROM comments order by id DESC")
    List<Comment> getAllComments();

    @Query("SELECT * FROM current_shifts")
    List<Shift> getAllCurrent();

    @Query("SELECT * FROM messages order by id DESC")
    List<Message> getAllMessages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllShifts(List<ShiftRoom> shifts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllMessages(List<Message> messages);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTeams(List<Team> teams);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllTasks(List<TaskRoom> tasks);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCurrentShifts(List<Shift> shifts);

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertAllCurrentAllComments(List<Comment> comments);

    @Delete
    void delete(ShiftRoom shift);
}
