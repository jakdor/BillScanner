package com.asi.billscanner;

import android.util.Log;

import java.util.Vector;

/**
 * Wytyczne:
 * -Klasa otrzymuje stringa na podstawie którego tworzy objekt Bill
 */

class BillProcessor {

    private String ocrString;
    private Bill bill;

    BillProcessor(String input) {
        ocrString = input;
        bill = new Bill();

    }

    Bill getBill() {
        return bill;
    }

    public void run() {
        bill = new Bill();
        Vector<String> productName = new Vector<>();
        Vector<Double> productAmount = new Vector<>();
        Vector<Double> productPrice = new Vector<>();


        boolean properBill = false;


        String line;
        int begin = 0;

        for (int i = 0; i < ocrString.length(); i++) {
            if (ocrString.charAt(i) == '\n') {

                line = ocrString.substring(begin, i - 1);

                if(bill.getCompany().equals("")) {
                    bill.setCompany(searchCompany(line,begin,begin));
                }

                Log.i("BillProcessor", line);

                begin = i + 1;
                if (checkSimilarity(line, "spopb", 0.80) && properBill)
                    break;

                if (properBill)
                    processLine(line, productName, productAmount, productPrice);


                else if ((checkSimilarity(line, "paragonfiskalny", 0.80) || checkSimilarity(line, "paragfisk", 0.80)) &&
                        !properBill) {
                    Log.i("BillProcessor", "String jest podobny");
                    properBill = true;

                }
                else
                    searchDate(line);


            }

        }


        Log.i("BillProcessor", "---------------------------");
        for (int i = 0; i < productName.size(); i++) {
            if (checkSimilarity(productName.elementAt(i), "rabat", 0.80)) {
                productPrice.set(i, (-1) * productAmount.elementAt(i));
                productAmount.set(i, (double) 1);

            }
            bill.addNewProduct(productName.elementAt(i), null, productAmount.elementAt(i), productPrice.elementAt(i));
            Log.i("BillProcessor", productName.elementAt(i) + "|" + productAmount.elementAt(i) + "|" + productPrice.elementAt(i));
        }


    }

    private void processLine(String line, Vector<String> productName, Vector<Double> productAmount, Vector<Double> productSinglePrice) {
        String billString = line;
        int[] subStringBegin = {0};
        int[] subStringEnd = {0};

        billString = billString.replaceAll("#", "");
        billString = billString.replaceAll(",", "\\.");
        Log.i("BillProcessor", "\\\\\\\\\\\\" + billString);
        Log.i("BillProcessor", searchWord(billString, subStringBegin, subStringEnd));
        productName.add(searchWord(billString, subStringBegin, subStringEnd));

        subStringBegin[0] = subStringEnd[0];
        if (subStringEnd[0] < billString.length() - 1) {
            subStringBegin[0] = subStringEnd[0] + 1;
            productAmount.add(searchNumber(billString, subStringBegin, subStringEnd));
        } else {
            productAmount.add((double) 0);
            productSinglePrice.add((double) 0);

            return;
        }
        Log.i("BillProcessor", Double.toString(searchNumber(billString, subStringBegin, subStringEnd)));

        subStringBegin[0] = subStringEnd[0];
        if (subStringEnd[0] < billString.length() - 1) {
            subStringBegin[0] = subStringEnd[0] + 1;
            productSinglePrice.add(searchNumber(billString, subStringBegin, subStringEnd));
        } else {
            productSinglePrice.add((double) 0);

            return;
        }
        Log.i("BillProcessor", Double.toString(searchNumber(billString, subStringBegin, subStringEnd)));


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
    private String searchCompany(String string, int subStringBegin, int subStringEnd) {



        for (int i = subStringBegin; i < string.length(); i++) {
            if (isLetter(string.charAt(i))) {
                subStringBegin = i;
                break;
            }
        }
        for (int i = subStringBegin; i < string.length() ; i++) {
            if (!(isLetter(string.charAt(i)))) {
                subStringEnd = i;
                break;
            }


        }


        return string.substring(subStringBegin, subStringEnd);

    }

   private boolean isLetter(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');

    }

   private boolean isNumber(char c) {
        return (c >= '0' && c <= '9') || c == '.';

    }

   private void searchDate(String lineString) {
        String examinedString = lineString;
        examinedString = examinedString.replaceAll("[^\\d.||-]", "");
        int separatorCounter = 0;
        int numberCounter = 0;
        for (int i = 0; i < examinedString.length(); i++) {

            if (examinedString.charAt(i) == '-' || examinedString.charAt(i) == '.')
                separatorCounter++;
            if (isNumber(examinedString.charAt(i)))
                numberCounter++;
        }
        if (separatorCounter == 2 && numberCounter >= 6 && numberCounter <= 8)
            bill.setDate(examinedString);

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
        int examinedStringLength = examinedString.length();
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
        similarity = ((double) sameLetters / patternStringLength) - Math.abs((examinedStringLength - patternStringLength) / patternStringLength);
        Log.i("BillProcessor", Double.toString(similarity));

        return (similarity >= simPercentage);
    }


}
