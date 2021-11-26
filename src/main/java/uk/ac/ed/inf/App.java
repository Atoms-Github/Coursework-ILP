package uk.ac.ed.inf;


import dataDownload.DatabaseHandle;
import dataDownload.WebsiteHandle;

import java.sql.SQLException;

public class App
{
    public static void main( String[] args ) throws SQLException {
        // So. What information do we need?
        // 1. The orders, from the database.
        // 2. The shop information, from the website.
        // 3. The no-fly zones, from the website.

        System.out.println("Starting!");
        WebsiteHandle website = new WebsiteHandle("localhost", "9898");
        DatabaseHandle database = new DatabaseHandle("localhost", "9876");

        var e = website.fetchWhatThreeWordsBox("army.monks.grapes");


    }
}
