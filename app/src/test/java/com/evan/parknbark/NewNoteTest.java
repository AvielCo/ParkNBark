package com.evan.parknbark;

import com.evan.parknbark.bulletinboard.NewNoteDialog;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NewNoteTest {
    private String title;
    private String description;

    @Before
    public void setUp() throws Exception {
        title = "test title";
        description = "descriptionnnnnn :)";
    }

    @Test
    public void newNoteTest() {
        NewNoteDialog activity = new NewNoteDialog();
        assertTrue("Error test failed!", activity.saveNote(title, description, true));
    }
}
