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

    private static final String url = "jdbc:mysql://awsimage.csusuehzytyo.us-west-2.rds.amazonaws.com:3306";
    private static final String dbName = "awsimage";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String userName = "root";
    private static final String password = "m1101s11";

    private static final String messagequery = "";
    private static Connection conn;
    private static List<Integer> ID;
    private static List<Integer> userID;
    private static List<Date> msgDate;
    private static List<String> message;
    private static List<Integer> msgID;
    private ResultSet recordset;
    private ArrayList<String> record;
    private long time;

    public connectRDS() {
        ID = new ArrayList<Integer>();
        userID = new ArrayList<Integer>();
        msgDate = new ArrayList<Date>();
        message = new ArrayList<String>();
        msgID = new ArrayList<Integer>();
        recordset = null;
        record = null;
        connect();
        time = System.currentTimeMillis();

        sendUpdateQuery("INSERT INTO users (userName, serverName, lastAction) VALUES ('Flo3', 'Flosrechner', '0815')");

    }

    public String getMessages() {

        try {
            this.recordset = sendQuery(messagequery);

            while (recordset.next()) {
                ID.add(recordset.getInt("ID"));
                userID.add(recordset.getInt("userID"));
                msgDate.add(recordset.getDate("msgDate"));
                message.add(recordset.getString("message"));
                msgID.add(recordset.getInt("msgIndex"));


            }
            recordset.close();

        } catch (SQLException e) {
            this.recordset = null;
        }
        return "";
    }

    public static void connect() {


        try {
            Class.forName(driver).newInstance();
            conn = DriverManager.getConnection(url + "/" + dbName, userName, password);
            if (conn.isClosed()) {
                System.out.println("Conn is closed");
            } else {
                System.out.println("Conn is active");
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet sendQuery(String sql) {
        Statement stm = null;
        try {
            stm = conn.createStatement();
            ResultSet rs = stm.executeQuery(sql);
            stm.close();
            return rs;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void sendUpdateQuery(String sql) {
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