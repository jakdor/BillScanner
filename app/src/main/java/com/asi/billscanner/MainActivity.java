package com.asi.billscanner;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.dummyTextView) TextView dummyTextView;

    private Context appContext = this;

    private BillFactory billFactory;
    private DbHandler dbHandler;
    BillsAdapter billsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        billFactory = new BillFactory(appContext, dbHandler);
        dbHandler = new DbHandler(this);
        billsAdapter = new BillsAdapter(dbHandler);
        dbHandler.openDb();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!dbHandler.isDbOpen()) {
            dbHandler.openDb();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        dbHandler.closeDb();
    }

    @OnTouch(R.id.scanButton)
    public boolean onScanButtonTouch(MotionEvent motionEvent) {
        if (motionEvent.getAction() == MotionEvent.ACTION_DOWN) {
            dummyTextView.setText("click!");

            billFactory.createNewBill();
        }
        return false;
    }
}
