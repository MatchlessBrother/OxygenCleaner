package com.oxygen.cleaner.pags;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.oxygen.cleaner.R;

public class OxygenAct_Prvacy extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.privacyact);
        TextView privacyTV = findViewById(R.id.tv_privacy);
        privacyTV.setText(Html.fromHtml(getString(R.string.text_privacy)));

        findViewById(R.id.fl_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
