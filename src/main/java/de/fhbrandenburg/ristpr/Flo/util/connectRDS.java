/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhbrandenburg.ristpr.Flo.util;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author taake
 */
public class connectRDS {

    private static final String url = Constants.DBURL;
    private static final String dbName = Constants.DBNAME;
    private static final String driver = Constants.DBDRIVER;
    private static final String userName = Constants.DBUSERNAME;
    private static final String password = Constants.DBPASSWORD;

    private static Connection conn;
    private static List<Integer> ID;
    private static List<Integer> userID;
    private static List<Date> msgDate;
    private static List<String> message;
    private static List<Integer> msgID;
    private ResultSet recordset;
    private ArrayList<String[]> record;
    private long time;

    public connectRDS() {
        Loger.LOG("connetion Compiler wird gestartet");
        ID = new ArrayList<Integer>();
        userID = new ArrayList<Integer>();
        msgDate = new ArrayList<Date>();
        message = new ArrayList<String>();
        msgID = new ArrayList<Integer>();
        recordset = null;
        connect();
        time = System.currentTimeMillis();

    }

    public ArrayList<String[]> getRecord(String sql) {
        record = null;
        record = new ArrayList<String[]>();
        recordset = sendQuery(sql);
        try {
            while (recordset.next()) {
                String[] array = {""+recordset.getInt("ID"),recordset.getString("message")};
                record.add(array);
            }
            recordset.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return record;
    }


    public static void connect() {

        Loger.LOG("Verbindung zur Datenbank wird hergestellt");
        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + "/" + dbName, userName, password);
            if (conn.isClosed()) {
                Loger.ERR("Conn is closed");
                System.exit(-1);
            } else {
                Loger.LOG("Conn is active");
            }


        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }
    }

    public static ResultSet sendQuery(String sql) {
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            //stm.close();
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

    public static void readQueryResultsMessages(ResultSet rs) throws SQLException {
        List<Integer> ID = new ArrayList<Integer>();
        List<Integer> userID = new ArrayList<Integer>();
        List<Date> msgDate = new ArrayList<Date>();
        List<String> message = new ArrayList<String>();
        List<Integer> msgID = new ArrayList<Integer>();


        while (rs.next()) {
            ID.add(rs.getInt("ID"));
            userID.add(rs.getInt("userID"));
            msgDate.add(rs.getDate("msgDate"));
            message.add(rs.getString("message"));
            msgID.add(rs.getInt("msgIndex"));
        }
        rs.close();
    }

    public static void closeConn() throws SQLException {
        conn.close();
    }

    public static List<Integer> getID() {

        return ID;
    }

    public static List<Integer> getuserID() {

        return userID;
    }

    public static List<Date> getMsgDate() {

        return msgDate;
    }

    public static List<String> getMessage() {

        return message;
    }

    public static List<Integer> getMsgID() {

        return msgID;
    }

}