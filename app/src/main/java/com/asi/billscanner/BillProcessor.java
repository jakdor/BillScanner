package com.asi.billscanner;

import android.util.Log;

import java.util.Vector;

/**
 * Wytyczne:
 * -Klasa otrzymuje stringa na podstawie którego tworzy objekt Bill
 */

class BillProcessor {

    private String ocrString;

    BillProcessor(String input) {
        ocrString = input;

    }

    public void run() {

        Vector<String> productName = new Vector<>();
        Vector<Double> productAmmount = new Vector<>();
        Vector<Double> productSinglePrice = new Vector<>();
        Vector<Double> productSumPrice = new Vector<>();
        double sum = 0;

        boolean properBill = false;


        String str = "E.S80 ar ek i 1.5a larek Sp.T\n" + "64-000 Kosc ian, Nienceuicza 3###\n" + "###Sklep Fireowy\n"
                + "50-49 Wlroc au, Krasinsk iego 13###\n" + "###NIP 688-00-72-43###\n" + "###dn.16r12.19###\n"
                + "###wydr.1441###\n" + "###PARAGON FI SKALNY\n" + "KONCENTRAT PO 1 3,30 3,30 B\n"
                + "PüLEINICA PIU 0,35 22,59 6,78 0\n" + "KIEEEASA 2 Ki 0,26 23,99 b,24 0###\n"
                + "RABAT -10zł\n"
                + "###SER KROLENSKI 0,305 25,99 7,93 D\n" + "###Sp,op.B\n"
                + "Sp op. D###\n" + "###,30 PIU 8- 8,00\n" + "20,95 PIU D 5,002###\n" + "###Razen Piü###\n"
                + "###SUN PLN 1,24###\n" + "###2APLACUNO GUORA PIN\n" + "0033/0013 HO130 SZEF\n"
                + "6OEBL7721008128F E381561310E 175510412885###\n" + "###24,25\n" + "1:25###\n"
                + "###A CAD 1501243343###";

        String line = "";
        int begin = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\n') {

                line = str.substring(begin, i - 1);

                Log.i("BillProcessor",line);

                begin = i + 1;
                if (checkSimilarity(line, "spopb", 0.80) && properBill)
                    break;

                if (properBill)
                    processLine(line, productName, productAmmount, productSinglePrice, productSumPrice);


                else if ((checkSimilarity(line, "paragonfiskalny", 0.80) || checkSimilarity(line, "paragfisk", 0.80)) &&
                        !properBill) {
                    Log.i("BillProcessor","String jest podobny");
                    properBill = true;
                }


            }

        }


        Log.i("BillProcessor","---------------------------");
        for (int i = 0; i < productName.size(); i++) {
            if(checkSimilarity(productName.elementAt(i),"rabat",0.80)){
                productSumPrice.set(i,(-1) * productAmmount.elementAt(i));
                productAmmount.set(i, (double) 0);
            }
            Log.i("BillProcessor",productName.elementAt(i) + "|" + productAmmount.elementAt(i) + "|" + productSinglePrice.elementAt(i) + "|" + productSumPrice.elementAt(i));
        }
        for (double price : productSumPrice)
            sum += price;
        Log.i("BillProcessor",Double.toString(sum));


    }

    private void processLine(String line, Vector<String> productName, Vector<Double> productAmmount, Vector<Double> productSinglePrice, Vector<Double> productSumPrice) {
        String billString = line;
        int[] subStringBegin = {0};
        int[] subStringEnd = {0};

        billString = billString.replaceAll("#", "");
        billString = billString.replaceAll(",", "\\.");
        Log.i("BillProcessor","\\\\\\\\\\\\" + billString);
        Log.i("BillProcessor",searchWord(billString, subStringBegin, subStringEnd));
        productName.add(searchWord(billString, subStringBegin, subStringEnd));

        subStringBegin[0] = subStringEnd[0];
        if (subStringEnd[0] < billString.length() - 1)
            subStringBegin[0] = subStringEnd[0] + 1;
        else {
            productAmmount.add((double) 0);
            productSinglePrice.add((double) 0);
            productSumPrice.add((double) 0);
            return;
        }
        Log.i("BillProcessor", Double.toString(searchNumber(billString, subStringBegin, subStringEnd)));
        productAmmount.add(searchNumber(billString, subStringBegin, subStringEnd));
        subStringBegin[0] = subStringEnd[0];
        if (subStringEnd[0] < billString.length() - 1)
            subStringBegin[0] = subStringEnd[0] + 1;
        else {
            productSinglePrice.add((double) 0);
            productSumPrice.add((double) 0);
            return;
        }
        Log.i("BillProcessor",Double.toString(searchNumber(billString, subStringBegin, subStringEnd)));
        productSinglePrice.add(searchNumber(billString, subStringBegin, subStringEnd));

        subStringBegin[0] = subStringEnd[0];
        if (subStringEnd[0] < billString.length() - 1)
            subStringBegin[0] = subStringEnd[0] + 1;
        else {
            productSumPrice.add((double) 0);
            return;
        }
        Log.i("BillProcessor",Double.toString(searchNumber(billString, subStringBegin, subStringEnd)));
        productSumPrice.add(searchNumber(billString, subStringBegin, subStringEnd));


    }

    private double searchNumber(String string, int[] subStringBegin, int[] subStringEnd) {
        for (int i = subStringBegin[0]; i < string.length(); i++) {
            if (isNumber(string.charAt(i))) {
                subStringBegin[0] = i;
                break;
            }
        }
        for (int i = subStringBegin[0]; i < string.length(); i++) {
            if (isNumber(string.charAt(i))) {
                subStringEnd[0] = i + 1;
            } else
                break;
        }

        return Double.parseDouble(string.substring(subStringBegin[0], subStringEnd[0]));
    }

    private String searchWord(String string, int[] subStringBegin, int[] subStringEnd) {


        int noLetter = 0;
        for (int i = subStringBegin[0]; i < string.length(); i++) {
            if (isLetter(string.charAt(i))) {
                subStringBegin[0] = i;
                break;
            }
        }
        for (int i = subStringBegin[0]; i < string.length() && noLetter < 4; i++) {
            if (isLetter(string.charAt(i))) {
                subStringEnd[0] = i + 1;
                noLetter = 0;
            } else
                noLetter++;

        }


        return string.substring(subStringBegin[0], subStringEnd[0]);

    }

    private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');

    }

    private boolean isNumber(char c) {
        return (c >= '0' && c <= '9') || c == '.';

    }

    private boolean checkSimilarity(String lineString, String patternString, double simPercentage) {
        String examinedString = lineString;

        int patternCounter;
        int examinedCounter;
        int sameLetters = 0;
        int patternStringLength = patternString.length();
        examinedString = examinedString.replaceAll(" ", "");
        examinedString = examinedString.replaceAll("#", "");
        examinedString = examinedString.replaceAll(",", "");
        examinedString = examinedString.replaceAll("\\.", "");
        int examindedStringLength = examinedString.length();
        double similarity;

        examinedString = examinedString.toLowerCase();
        for (int i = 0; i < patternString.length(); i++) {
            patternCounter = patternString.length() - patternString.replace(patternString.charAt(i) + "", "").length();
            examinedCounter = examinedString.length()
                    - examinedString.replace(patternString.charAt(i) + "", "").length();
            if (examinedCounter >= patternCounter)
                sameLetters += patternCounter;
            else
                sameLetters += examinedCounter;
            examinedString = examinedString.replace(patternString.charAt(i) + "", "");

        }
        similarity = (double) sameLetters / patternStringLength - Math.abs((examindedStringLength - patternStringLength) / patternStringLength);
        Log.i("BillProcessor",Double.toString(similarity));

        return  (similarity >= simPercentage);
    }


}
