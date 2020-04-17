package com.evan.parknbark.bulletinboard;

import androidx.annotation.Keep;

import java.io.Serializable;

/**
 *  Made it implements Serializable to make it possible
 *  to pass between activities when needed.
 *  intent.putExtra("NAME", new Note(...));
 *  in the second activity that we passing to use:
 *  Intent i = getIntent();
 *  Note note = (Note)i.getSerializableExtra("NAME");
 *  cool cool cool cool cool cool cool cool cool cool cool cool cool cool cool cool cool cool cool
 */
@Keep
public class Note implements Serializable {
    private String title, description, date;

    public Note() {
        //do not delete.
    }

    Note(String title, String description, String date) {
        setTitle(title);
        setDescription(description);
        setDate(date);
    }

    public String getTitle() throws NullPointerException{
        return title;
    }

    public String getDescription()throws NullPointerException {
        return description;
    }

    public String getDate()throws NullPointerException {
        return date;
    }

    private void setTitle(String title) {
        this.title = title;
    }

    private void setDescription(String description) {
        this.description = description;
    }

    private void setDate(String date) {
        this.date = date;
    }
}
