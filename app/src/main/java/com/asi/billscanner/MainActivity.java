package com.asi.billscanner;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private Context appContext = this;

    private BillFactory billFactory;
    private DbHandler dbHandler;

    private TextView dummyTextView;

    private final View.OnTouchListener scanButtonListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            dummyTextView.setText("click!");

            billFactory.createNewBill();
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        billFactory = new BillFactory(appContext, dbHandler);
        dbHandler = new DbHandler();

        dummyTextView = (TextView)findViewById(R.id.dummyTextView);

        findViewById(R.id.scanButton).setOnTouchListener(scanButtonListener);
    }
}
