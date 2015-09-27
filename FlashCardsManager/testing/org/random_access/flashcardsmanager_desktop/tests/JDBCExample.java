package org.random_access.flashcardsmanager_desktop.tests;

import java.sql.*;

public class JDBCExample {

    public static void main(String[] argv) {

        System.out.println("-------- MySQL JDBC Connection Testing ------------");

        try {
            Class.forName("com.mysql.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("Where is your MySQL JDBC Driver?");
            e.printStackTrace();
            return;
        }

        System.out.println("MySQL JDBC Driver Registered!");
        Connection connection = null;

        try {
            connection = DriverManager.getConnection("jdbc:mysql://212.227.103.70:3306/flashcards_db?create=true", "moni",
                    "testing123");
            Statement st = connection.createStatement();
            st.execute("CREATE TABLE TEST(" + "ID INT NOT NULL, TEXT VARCHAR(20) NOT NULL, PRIMARY KEY (ID) )");

        } catch (SQLException e) {
            System.out.println("Connection Failed! Check output console");
            e.printStackTrace();
            return;
        }

        if (connection != null) {
            System.out.println("You made it, take control your database now!");
        } else {
            System.out.println("Failed to make connection!");
        }
    }
}
