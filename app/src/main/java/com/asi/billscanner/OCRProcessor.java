package com.asi.billscanner;

import android.util.Log;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import com.asi.billscanner.ui.camera.GraphicOverlay;

public class OCRProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OCRGraphic> mGraphicOverlay;

    OCRProcessor(GraphicOverlay<OCRGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        String ocrStr = "";
        mGraphicOverlay.clear();

        SparseArray<TextBlock> items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            if (item != null && item.getValue() != null) {
                ocrStr += "\n" + item.getValue();
            }
            OCRGraphic graphic = new OCRGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }

        Log.wtf("OcrDetectorProcessor", "TEXT: " + ocrStr);
    }

    /**
     * Frees the resources associated with this detection processor.
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
