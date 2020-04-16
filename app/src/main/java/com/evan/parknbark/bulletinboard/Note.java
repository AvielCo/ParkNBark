package com.evan.parknbark.bulletinboard;

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
public class Note implements Serializable {
    private String title, description, date;

    public Note() {
        //do not delete.
    }

    Note(String title, String description, String date) {
        this.title = title;
        this.description = description;
        this.date = date;
    }

    String getTitle() throws NullPointerException{
        return title;
    }

    String getDescription()throws NullPointerException {
        return description;
    }

    String getDate()throws NullPointerException {
        return date;
    }
}
