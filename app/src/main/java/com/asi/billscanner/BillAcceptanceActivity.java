package com.asi.billscanner;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.ByteArrayOutputStream;

/**
 * Wytyczne:
 * - Wyświetla objekt Bill w celu akceptacji przez użytkownika
 * - umożliwia modyfikację pól
 * - przypisanie kategorii produktom z dropDownList
 */

public class BillAcceptanceActivity extends AppCompatActivity {

    private final String CLASS_TAG = "BillAcceptanceActivity";

    private Bill bill;
    private Bitmap billBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_acceptance);

        CollapsingToolbarLayout collapsingToolbar =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbar.setTitle("hello");
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
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
        return stream.toByteArray();
    }

    private Bitmap rotateBitmap(Bitmap toTransform, float angle){
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(toTransform, 0, 0,
                toTransform.getWidth(), toTransform.getHeight(), matrix, true);
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
        Intent intent = new Intent(BillAcceptanceActivity.this, OCRCaptureActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intent);
    }

    @Subscribe(sticky = true)
    public void onGetBillEvent(GetBillForAcceptance event) {
        if(event.getBill() != null) {
            Log.i(CLASS_TAG, "onGetBillEvent called");
            this.bill = event.getBill();
        }
        else {
            Log.wtf(CLASS_TAG, "onGetBillEvent; Bill not initialized");
        }
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
}
