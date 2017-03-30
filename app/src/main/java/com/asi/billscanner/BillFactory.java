package com.asi.billscanner;

import android.content.Context;
import android.content.Intent;

/**
 * Wytyczne:
 * - Klasa odpala OCRCaptureActivity lub ManualCapture,
 * - Przekazuje dane zwrotne z ocr do BillProcesor (zwraca gotowy Bill),
 * - Na podstwaie danych z ManualCapture buduje Bill,
 * - Przekazuje objekt Bill klasie DBhendler do dodanie do bazy danych
 */

class BillFactory {

    private Context appContext;
    private DbHandler dbHandler;

    BillFactory(Context appContext, DbHandler dbHandler){
        this.appContext = appContext;
        this.dbHandler = dbHandler;
    }

    void createNewBill(){
        lunchOCRCapture();

        //tutaj reszta procesu dodawania Bill'a
    }

    private void lunchOCRCapture(){
        Intent OCRCaptureIntent = new Intent(appContext, OCRCaptureActivity.class);
        appContext.startActivity(OCRCaptureIntent);
    }

    private void lunchBillAcceptanceActivity(){
        Intent billAcceptanceIntent = new Intent(appContext, BillAcceptanceActivity.class);
        appContext.startActivity(billAcceptanceIntent);
    }
}
