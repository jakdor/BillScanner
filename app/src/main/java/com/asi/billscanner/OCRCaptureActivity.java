package com.asi.billscanner;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class OCRCaptureActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onBackPressed() {
        BillFactory.lunchOnceHandler();
        super.onBackPressed();
    }

}
