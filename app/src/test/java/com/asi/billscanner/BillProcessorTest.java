package com.asi.billscanner;

import static junit.framework.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class BillProcessorTest {
    private final String TEST_STRING = "Smolarek i 1.5a larek Sp.T\n" + "64-000 Kosc ian, Nienceuicza 3###\n" + "###Sklep Fireowy\n"
            + "50-49 Wlroc au, Krasinsk iego 13###\n" + "###NIP 688-00-72-43###\n" + "###2016-04-06 poniedziałek###\n"
            + "###wydr.1441###\n" + "###PARAGON FI SKALNY\n" + "KONCENTRAT PO 1 3,30 3,30 B\n"
            + "PüLEINICA PIU 0,35 22,59 6,78 0\n" + "KIEEEASA 2 Ki 0,26 23,99 b,24 0###\n"
            + "RABAT -10zł\n"
            + "###SER KROLENSKI 0,305 25,99 7,93 D\n" + "###Sp,op.B\n"
            + "Sp op. D###\n" + "###,30 PIU 8- 8,00\n" + "20,95 PIU D 5,002###\n" + "###Razen Piü###\n"
            + "###SUN PLN 1,24###\n" + "###2APLACUNO GUORA PIN\n" + "0033/0013 HO130 SZEF\n"
            + "6OEBL7721008128F E381561310E 175510412885###\n" + "###24,25\n" + "1:25###\n"
            + "###A CAD 1501243343###";

    private BillProcessor billProcessor;

    @Before
    public void setup() throws Exception {
        billProcessor = new BillProcessor(TEST_STRING);
        billProcessor.run();
    }

    @Test
    public void productName()  throws Exception{
        assertTrue(billProcessor.getBill().getProductsSize() > 0);
        assertEquals(5,billProcessor.getBill().getProductsSize());

    }

    @Test
    public void checkProduct() throws Exception{
        for(Bill.Product product : billProcessor.getBill().getProductList()){
            assertNotNull(product.name);
            assertNotNull(product.amount);
            assertTrue(product.price != 0);
        }
        assertTrue(billProcessor.getBill().getBillSum()>0);
        assertEquals("KONCENTRAT PO",billProcessor.getBill().getProductNameAtIndex(0));
        assertEquals(0.35,billProcessor.getBill().getProductAmountAtIndex(1));

    }
    @Test
    public void checkDate() throws Exception{
        assertNotNull(billProcessor.getBill().getAddress());
    }
    @Test
    public void checkCompany() throws Exception{
        assertNotNull(billProcessor.getBill().getCompany());
        assertEquals("Smolarek",billProcessor.getBill().getCompany());
    }
}
