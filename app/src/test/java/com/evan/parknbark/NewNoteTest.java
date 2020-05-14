package com.evan.parknbark;

import com.evan.parknbark.bulletinboard.NewNoteActivity;
import com.evan.parknbark.emailpassword.ChangePassActivity;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class NewNoteTest {
    private String title;
    private String description;

    @Before
    public void setUp() throws Exception {
        title = "Some shitty old pass";
        description = "New password :)";
    }

    @Test
    public void changePassTest() {
        NewNoteActivity activity = new NewNoteActivity();
        assertTrue("Successfully saved new note!", activity.saveNote(title, description, true));
    }
}
