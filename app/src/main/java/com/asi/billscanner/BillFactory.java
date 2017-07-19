package com.asi.billscanner;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;

import java.util.Vector;

/**
 * Wytyczne:
 * - Klasa odpala OCRCaptureActivity lub ManualCapture,
 * - Przekazuje dane zwrotne z ocr do BillProcesor (zwraca gotowy Bill),
 * - Na podstwaie danych z ManualCapture buduje Bill,
 * - Przekazuje objekt Bill klasie DBhendler do dodanie do bazy danych
 */

class BillFactory {

    private static Context appContext;
    private BillsAdapter billsAdapter;

    private class ProcessBill extends AsyncTask<String, Void, Void>{
        Bill bill;

        @Override
        protected Void doInBackground(String... params) {
            BillProcessor billProcessor = new BillProcessor(params[0]);
            billProcessor.run();
            //rest of the bill processing here

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            EventBus.getDefault().postSticky(
                    new BillAcceptanceActivity.GetBillForAcceptance(bill, getCategories()));
        }
    }

    BillFactory(Context appContext, DbHandler dbHandler){
        BillFactory.appContext = appContext;
        billsAdapter = new BillsAdapter(dbHandler);
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

    private Vector<String> getCategories(){
        Vector<String> categories = billsAdapter.getUsedCategories();
        Vector<String> discardedCategories = billsAdapter.getDiscardedCategories();
        for(int i = 0; i < categories.size(); ++i){
            for(int j = 0; j < discardedCategories.size(); ++j){
                if(categories.elementAt(i).equals(discardedCategories.elementAt(j))){
                    categories.remove(i);
                    break;
                }
            }
        }

        return categories;
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
