package com.asi.billscanner;

import android.util.Log;
import android.util.Pair;
import android.util.SparseArray;

import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;

import com.asi.billscanner.ui.camera.GraphicOverlay;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Vector;

class OCRProcessor implements Detector.Processor<TextBlock> {

    private GraphicOverlay<OCRGraphic> mGraphicOverlay;
    private String ocrStr = "";

    private SparseArray<TextBlock> items;

    OCRProcessor(GraphicOverlay<OCRGraphic> ocrGraphicOverlay) {
        mGraphicOverlay = ocrGraphicOverlay;
    }

    /**
     * Passes detected text blocks to OCRGraphic and creates ocrStr
     */
    @Override
    public void receiveDetections(Detector.Detections<TextBlock> detections) {
        mGraphicOverlay.clear();

        items = detections.getDetectedItems();
        for (int i = 0; i < items.size(); ++i) {
            TextBlock item = items.valueAt(i);
            OCRGraphic graphic = new OCRGraphic(mGraphicOverlay, item);
            mGraphicOverlay.add(graphic);
        }

    }

    /**
     * sorts detected text blocks by the pos from the top of the screen and creates ocrStr
     */
    private String createOcrStr(){
        String ocrStr = "";

        if(items.size() > 0) {

            List<Pair<Integer, Integer>> sortList = new Vector<>();

            for (int i = 0; i < items.size(); ++i) {
                TextBlock item = items.valueAt(i);
                sortList.add(Pair.create(i, item.getBoundingBox().top));
            }

            Log.i("OCRProcessor", "SortTest1: " + Integer.toString(sortList.get(0).first) +
                    ", " + Integer.toString(sortList.get(0).second));

            Collections.sort(sortList, new Comparator<Pair<Integer, Integer>>() {
                @Override
                public int compare(Pair<Integer, Integer> x, Pair<Integer, Integer> y) {
                    return x.second - y.second;
                }
            });

            Log.i("OCRProcessor", "SortTest2: " + Integer.toString(sortList.get(0).first) +
                    ", " + Integer.toString(sortList.get(0).second));

            for (int i = 0; i < items.size(); ++i) {
                int sortPos = sortList.get(i).first;
                TextBlock item = items.valueAt(sortPos);
                if (item != null && item.getValue() != null) {
                    ocrStr += "\n###" + item.getValue() + "###";
                }
            }
        }

        return ocrStr;
    }

    /**
     * Provides access to OCR results
     */
    String ocrResult(){
        ocrStr = createOcrStr();

        if(!ocrStr.isEmpty()) {
            return ocrStr;
        }
        return null;
    }

    /**
     * Frees the resources associated with this detection processor
     */
    @Override
    public void release() {
        mGraphicOverlay.clear();
    }
}
