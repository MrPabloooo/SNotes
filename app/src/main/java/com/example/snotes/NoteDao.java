package com.example.snotes;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public interface NoteDao {

 @Insert
    void insert(Note note);

 @Query("SELECT * FROM note")
    List<Note> getAll();

 @Query("SELECT * FROM note WHERE parent = :parent")
    List<Note> getByParent(String parent);

 @Query("DELETE FROM note WHERE id = :id")
    void deleteById(int id);

 @Query("Select * FROM note WHERE id = :id")
    Note getById(int id);
    @Query("SELECT title FROM note WHERE id = :id")
    String getNameById(int id);

    @Query("SELECT id FROM note WHERE title = :title")
    Integer getIdByTitle(String title);

    @Query("SELECT hint FROM note WHERE id = :id")
    String getHintById(int id);

}
