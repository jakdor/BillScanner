package com.asi.billscanner;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

/**
 * Wytyczne:
 * - Wyświetla objekt Bill w celu akceptacji przez użytkownika
 * - umożliwia modyfikację pól
 * - przypisanie kategorii produktom z dropDownList
 */

public class BillAcceptanceActivity extends AppCompatActivity {

    private static Bill bill;
    static void setBill(Bill input){
        bill = input;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_acceptance);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(BillAcceptanceActivity.this, OCRCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }
}
