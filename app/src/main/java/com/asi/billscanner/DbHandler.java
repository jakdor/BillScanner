package com.asi.billscanner;

/**
 * Wytyczne:
 * Klasa pośrednicząca w wymianie danych z lokalną bazą danych (SQLite)
 * - Dodawnie rekordów z klasy/struktury Bill
 * - Metody/kwerendy pozwalające pobierać i usuwać dane z DB
 *
 * Proponowany układ pól w db (pewnie jeszcze się zmieni):
 *
 * Tabele Bills i Products połączone relacją "jeden do wielu"
 *
 * Tabela Products:
 * (Id)[INTEGER], (BillId)[INTEGER], (Category)[VARCHAR(64)], (ProductName)[VARCHAR(128)], (Amount)[INTEGER], (Price)[MONEY]
 *
 * Tabela Bills:
 * (Id)[INTEGER], (AddTime)[TIMESTAMP], (BillDate)[DATE], (Company)[VARCHAR(128)], (Adress)[VARCHAR(128)]
 */

class DbHandler {
    DbHandler(){

    }

    void addBillToDB(Bill bill){

    }
}
