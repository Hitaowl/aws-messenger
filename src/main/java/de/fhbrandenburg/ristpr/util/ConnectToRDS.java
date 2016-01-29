/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhbrandenburg.ristpr.util;


import java.sql.*;
import java.util.ArrayList;

/**
 * @author taake
 */
public class ConnectToRDS {

    private static final String url = Constants.DBURL;
    private static final String dbName = Constants.DBNAME;
    private static final String driver = Constants.DBDRIVER;
    private static final String userName = Constants.DBUSERNAME;
    private static final String password = Constants.DBPASSWORD;

    private static Connection conn;
    private ResultSet recordset;
    private ArrayList<String[]> record;
    private ArrayList<String[]> linkList;

    public ConnectToRDS() {
        recordset = null;
        connect();
    }

    public ArrayList<String[]> getRecord(String sql) {
        record = null;
        record = new ArrayList<String[]>();
        recordset = selectQuery(sql);
        try {
            while (recordset.next()) {
                String[] array = {"" + recordset.getInt("ID"), recordset.getString("message"), recordset.getString("userName"), recordset.getString("channel")};
                record.add(array);
            }
            recordset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }

    public ArrayList<String[]> getLinkList(String sql) {
        linkList = new ArrayList<String[]>();
        recordset = selectQuery(sql);
        try {
            while (recordset.next()) {
                String[] array = {"" + recordset.getInt("ID"), recordset.getString("link")};
                linkList.add(array);
            }
            recordset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return linkList;
    }

    public String getUserName(String sql) {
        recordset = selectQuery(sql);
        try {
            while (recordset.next()) {
                return recordset.getString("userName");
            }
            recordset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return "";
    }


    public static void connect() {
        Loger.LOG("Verbindung zur Datenbank wird hergestellt");
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + "/" + dbName, userName, password);
            if (conn.isClosed()) {
                Loger.ERR("Verbindung konnte nicht hergestellt werden. Server wird beendet...");
                System.exit(-1);
            } else {
                Loger.LOG("Verbindung wurde erfolgreich hergestellt.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static ResultSet selectQuery(String sql) {
        Statement stm;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void execute(String sql) {
        Statement stm = null;
        try {
            stm = conn.createStatement();
            stm.execute(sql);
            stm.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}