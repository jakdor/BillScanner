package com.asi.billscanner;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

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

    private class ProcessBill extends AsyncTask<String, Void, Void>{
        Bill bill;

        @Override
        protected Void doInBackground(String... params) {
            BillProcessor billProcessor = new BillProcessor(params[0]);
            billProcessor.run();
            bill = billProcessor.getBill();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            EventBus.getDefault().postSticky(new BillAcceptanceActivity.getBillForAcceptance(bill));
        }
    }

    BillFactory(Context appContext, DbHandler dbHandler){
        BillFactory.appContext = appContext;
        this.dbHandler = dbHandler;
    }

    void createNewBill(){
        lunchOCRCapture();
    }

    private void runOcrProcessing(String ocrResult){
        new ProcessBill().execute(ocrResult);
        lunchBillAcceptanceActivity();
    }

    void setOcrResult(String input){
        if(!input.isEmpty()) {
            Log.i("BillFactory", "Got ocr result");
            runOcrProcessing(input);
        }
    }

    private void lunchOCRCapture(){
        Intent OCRCaptureIntent = new Intent(appContext, OCRCaptureActivity.class);
        OCRCaptureIntent.setFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        appContext.startActivity(OCRCaptureIntent);
    }

    private void lunchBillAcceptanceActivity(){
        Intent billAcceptanceIntent = new Intent(appContext, BillAcceptanceActivity.class);
        appContext.startActivity(billAcceptanceIntent);
    }
}
