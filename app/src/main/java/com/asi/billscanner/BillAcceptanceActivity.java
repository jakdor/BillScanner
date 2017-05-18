package com.asi.billscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

/**
 * Wytyczne:
 * - Wyświetla objekt Bill w celu akceptacji przez użytkownika
 * - umożliwia modyfikację pól
 * - przypisanie kategorii produktom z dropDownList
 */

public class BillAcceptanceActivity extends AppCompatActivity {

    private final String CLASS_TAG = "BillAcceptanceActivity";

    private Bill bill;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_acceptance);
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
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BillAcceptanceActivity.this, OCRCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Subscribe(sticky = true)
    public void onGetBillEvent(getBillForAcceptance event) {
        if(event.getBill() != null) {
            Log.i(CLASS_TAG, "onGetBillEvent called");
            this.bill = event.getBill();
        }
        else {
            Log.wtf(CLASS_TAG, "onGetBillEvent; Bill not initialized");
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    static class getBillForAcceptance {
        private final Bill bill;

        getBillForAcceptance(Bill bill) {
            this.bill = bill;
        }

        Bill getBill() {
            return bill;
        }
    }
}
