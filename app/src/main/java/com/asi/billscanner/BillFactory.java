package com.asi.billscanner;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * Wytyczne:
 * - Klasa odpala OCRCaptureActivity lub ManualCapture,
 * - Przekazuje dane zwrotne z ocr do BillProcesor (zwraca gotowy Bill),
 * - Na podstwaie danych z ManualCapture buduje Bill,
 * - Przekazuje objekt Bill klasie DBhendler do dodanie do bazy danych
 */

class BillFactory {

    private static Context appContext;
    private DbHandler dbHandler;

    BillFactory(Context appContext, DbHandler dbHandler){
        BillFactory.appContext = appContext;
        this.dbHandler = dbHandler;
    }

    void createNewBill(){
        lunchOCRCapture();
    }

    private static void runOcrProcessing(String ocrResult){
        lunchBillAcceptanceActivity();
        BillProcessor billProcessor = new BillProcessor(ocrResult);
        billProcessor.run();
        //Bill bill = billProcessor.getResult();
        //                  lub inne metody zwracające billa

        //BillAcceptanceActivity.setBill(bill);
    }

    static void setOcrResult(String input){
        if(!input.isEmpty()) {
            Log.i("BillFactory", "Got ocr result");
            runOcrProcessing(input);
        }
    }

    private void lunchOCRCapture(){
        Intent OCRCaptureIntent = new Intent(appContext, OCRCaptureActivity.class);
        appContext.startActivity(OCRCaptureIntent);
    }

    private static void lunchBillAcceptanceActivity(){
        Intent billAcceptanceIntent = new Intent(appContext, BillAcceptanceActivity.class);
        appContext.startActivity(billAcceptanceIntent);
    }

}
