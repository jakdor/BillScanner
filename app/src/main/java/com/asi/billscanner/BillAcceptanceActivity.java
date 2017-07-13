package com.asi.billscanner;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Wytyczne:
 * - Wyświetla objekt Bill w celu akceptacji przez użytkownika
 * - umożliwia modyfikację pól
 * - przypisanie kategorii produktom z dropDownList
 */

public class BillAcceptanceActivity extends AppCompatActivity {

    @BindView(R.id.cardDateField) EditText cardDateEditText;
    @BindView(R.id.cardSumField) TextView cardSumTextView;
    @BindView(R.id.cardCompanyField) EditText cardCompanyEditText;
    @BindView(R.id.cardAddressField) EditText cardAddressEditText;

    private final String CLASS_TAG = "BillAcceptanceActivity";

    private Bill bill;
    private Bitmap billBitmap;
    private Calendar calendar;

    private CollapsingToolbarLayout collapsingToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_acceptance);
        ButterKnife.bind(this);

        calendar = new GregorianCalendar();

        collapsingToolbar = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        waitForBill();
    }

    private void waitForBill(){
        collapsingToolbar.setTitle(getString(R.string.bill_loading));
    }

    private void loadBill(){
        String sum = String.format(Locale.getDefault() ,"%.2f", bill.getBillSum()) + " zł";
        collapsingToolbar.setTitle(sum);
        cardDateEditText.setText(bill.getDate());
        cardSumTextView.setText(sum);
        cardCompanyEditText.setText(bill.getCompany());
        cardAddressEditText.setText(bill.getAddress());

        loadProductsList();
    }

    private void loadProductsList(){
        LinearLayout layout = (LinearLayout) findViewById(R.id.productsLayout);
        View child = getLayoutInflater().inflate(R.layout.bill_acceptance_card, layout);
        View child2 = getLayoutInflater().inflate(R.layout.bill_acceptance_card, layout);
        layout.addView(child);
        layout.addView(child2);
    }

    @OnClick(R.id.fabAccept)
    public void fabAcceptOnClick(View view){
        updateAcceptedBill();
    }

    private void updateAcceptedBill(){

    }

    private void loadBackdrop() {
        billBitmap = rotateBitmap(billBitmap, 90.0f);
        final ImageView imageView = (ImageView) findViewById(R.id.backdrop);
        Glide.with(this)
                .load(bitmapToByte(billBitmap))
                .asBitmap()
                .centerCrop()
                .into(imageView);
    }

    private byte[] bitmapToByte(Bitmap bitmap){
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.setHeight(800);
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap rotateBitmap(Bitmap toTransform, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(toTransform, 0, 0,
                toTransform.getWidth(), toTransform.getHeight(), matrix, true);
    }

    DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, monthOfYear);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            updateDateField();
        }

    };

    @OnClick(R.id.cardDateField)
    public void cardDateFieldOnClick(View view) {

        new DatePickerDialog(this, date,
                bill.getDateYear() ,
                bill.getDateMonth() - 1,
                bill.getDateDay())
                .show();
    }

    private void updateDateField(){
        String format = "yyyy-MM-dd";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format, Locale.getDefault());
        String newDate = simpleDateFormat.format(calendar.getTime());
        bill.setDate(newDate);
        cardDateEditText.setText(newDate);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
        loadBackdrop();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        cancelBill();
    }

    private void cancelBill(){
        Intent intent = new Intent(BillAcceptanceActivity.this, OCRCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @OnClick(R.id.fabCancel)
    public void fabCancelOnClick(View view){
        cancelBill();
    }

    @Subscribe(sticky = true)
    public void onGetBillEvent(GetBillForAcceptance event) {
        if(event.getBill() != null) {
            Log.i(CLASS_TAG, "onGetBillEvent called");
            this.bill = event.getBill();
        }
        else {
            Log.wtf(CLASS_TAG, "onGetBillEvent; Bill not initialized");
            this.bill = spawnDummyTestBill(); //BillProcessor not working yet
        }
        loadBill();
        EventBus.getDefault().removeStickyEvent(event);
    }

    static class GetBillForAcceptance {
        private final Bill bill;

        GetBillForAcceptance(Bill bill) {
            this.bill = bill;
        }

        Bill getBill() {
            return bill;
        }
    }

    @Subscribe(sticky = true)
    public void onGetBitmapEvent(GetBitmap event) {
        if(event.getBitmap() != null) {
            Log.i(CLASS_TAG, "onGetBitmapEvent called");
            this.billBitmap = event.getBitmap();
        }
        else {
            Log.wtf(CLASS_TAG, "onGetBitmapEvent; bitmap empty or not initialized");
        }
        EventBus.getDefault().removeStickyEvent(event);
    }

    static class GetBitmap {
        private final Bitmap bitmap;

        GetBitmap(Bitmap bitmap) {
            this.bitmap = bitmap;
        }

        Bitmap getBitmap() {
            return bitmap;
        }
    }

    private Bill spawnDummyTestBill(){
        Bill bill = new Bill("2017-06-30", "ZZ TOP S.C.", "K.Wielkiego 25/1A");
        bill.addNewProduct("ABBA", "", 1.0, 10.5);
        bill.addNewProduct("Pudelko", "", 1.0, 0.5);
        bill.addNewProduct("zupa kremowa", "", 1.0, 6.5);
        return bill;
    }
}
