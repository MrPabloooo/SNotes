package com.example.snotes;

import androidx.room.Entity;
import androidx.room.PrimaryKey;


@Entity
public class Note {

    @PrimaryKey(autoGenerate = true)
    public int id;

    public String title;

    public String parent;

    public String hint;
    public String content;

    public Note(String title, String content, String hint) {
        this.title = title;
        this.content = content;
        this.parent = parent;
        this.hint = hint;

    }

}
