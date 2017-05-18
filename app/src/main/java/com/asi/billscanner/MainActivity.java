package com.asi.billscanner;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.TextView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnTouch;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.dummyTextView) TextView dummyTextView;

    private final String CLASS_TAG = "MainActivity";

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
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.i(CLASS_TAG, "onDestroy called");
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

    @Subscribe(sticky = true)
    public void onOcrReturnEvent(getOcrStringEvent event) {
        if(!event.getOcrStr().isEmpty()) {
            Log.i(CLASS_TAG, "getOcrStringEvent called");
            billFactory.setOcrResult(event.getOcrStr());
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    static class getOcrStringEvent {
        private final String str;

        getOcrStringEvent(String str) {
            this.str = str;
        }

        String getOcrStr() {
            return str;
        }
    }
}
