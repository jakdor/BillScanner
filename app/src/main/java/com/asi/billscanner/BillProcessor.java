package com.asi.billscanner;

import android.util.Log;

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
        String str = "E.S80 ar ek i 1.5a larek Sp.T\n" + "64-000 Kosc ian, Nienceuicza 3###\n" + "###Sklep Fireowy\n"
                + "50-49 Wlroc au, Krasinsk iego 13###\n" + "###NIP 688-00-72-43###\n" + "###dn.16r12.19###\n"
                + "###wydr.1441###\n" + "###PARAGON FI SKALNY\n" + "KONCENTRAT PO 1511 1 3,30 3,30 B\n"
                + "PüLEINICA PIU 439 0,35 4 22,59 6, 78 0\n" + "KIEEEASA 2 Ki 248 0,26 23,99 b,24 0###\n"
                + "###SER KROLENSKI 1841###\n" + "###0,305 25,99 7,93 D\n" + "0,24\n" + "1.00###\n" + "###Sp,op.B\n"
                + "Sp op. D###\n" + "###,30 PIU 8- 8,00\n" + "20,95 PIU D 5,002###\n" + "###Razen Piü###\n"
                + "###SUN PLN 1,24###\n" + "###2APLACUNO GUORA PIN\n" + "0033/0013 HO130 SZEF\n"
                + "6OEBL7721008128F E381561310E 175510412885###\n" + "###24,25\n" + "1:25###\n"
                + "###A CAD 1501243343###";

        String line = "";
        int begin = 0;

        for (int i = 0; i < str.length(); i++) {
            if (str.charAt(i) == '\n') {

                line = str.substring(begin, i - 1);
                System.out.println(line);
                Log.i("BillProcessor", "dadsgd" + line);
                begin = i + 1;
                if (checkSimilarity(line, "paragonfiskaln", 0.50) == true)
                    Log.i("BillProcessor", "String jest podobny");
            }

        }
    }

    private static boolean checkSimilarity(String lineString, String patternString, double simPercentage) {
        String examinedString = new String(lineString);

        int patternCounter;
        int examinedCounter;
        int sameLetters = 0;
        int patternLength = patternString.length();
        examinedString = examinedString.replaceAll(" ", "");
        examinedString = examinedString.replaceAll("#", "");
        examinedString = examinedString.replaceAll(",", "");

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
        Log.i("BillProcessor", Double.toString((double) sameLetters / patternLength));

        if ((double) sameLetters / patternLength >= simPercentage)
            return true;
        else
            return false;
    }


}
