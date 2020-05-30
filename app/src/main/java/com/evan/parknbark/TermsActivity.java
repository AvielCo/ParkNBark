package com.evan.parknbark;

import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TextView;
import com.evan.parknbark.utilities.BaseActivity;
import java.io.InputStream;
public class TermsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms);
        TextView rawText = findViewById(R.id.terms_conds_text);
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.conds_terms_eng);
            byte[] b = new byte[in_s.available()];
            in_s.read(b);
            rawText.setText(new String(b));
        }
        catch (Exception e){
            rawText.setText("Error! Terms and conditions unavailable right now... Sorry.");
        }
    }
}
