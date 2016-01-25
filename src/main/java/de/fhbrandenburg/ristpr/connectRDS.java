/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package de.fhbrandenburg.ristpr;

import java.util.List;
import java.sql.*;
import java.util.ArrayList;

/**
 *
 * @author taake
 */
public class connectRDS {
    
    private static final String url = "xx";
    private static final String dbName ="xx";
    private static final String driver = "com.mysql.jdbc.Driver";
    private static final String userName = "xx"; 
    private static final String password = "xx";
    private static Connection conn = null;
    private static List<Integer> ID = new ArrayList<Integer>();
    private static List<Integer> userID = new ArrayList<Integer>();
    private static List<Date> msgDate = new ArrayList<Date>();
    private static List<String> message = new ArrayList<String>();
    private static List<Integer> msgID = new ArrayList<Integer>();

    public static List<Integer> getID(){
    
    return ID;
    }
    
    public static List<Integer> getuserID(){
    
    return userID;
    }
    
    public static List<Date> getMsgDate(){
    
    return msgDate;
    }
    
    public static List<String> getMessage(){
    
    return message;
    }
    
    public static List<Integer> getMsgID(){
    
    return msgID;
    }
    
    public static void connect(){
        
        
        try {
		  Class.forName(driver).newInstance();
		  conn = DriverManager.getConnection(url+ "/" + dbName,userName,password);
                  if (conn.isClosed()){
                      System.out.println("Conn is closed");
                  }else {
                      System.out.println("Conn is active");
                  }
		  
		  
		  } catch (Exception e) {
		  e.printStackTrace();
		  }
		  }  
    
    public static ResultSet sendQuery(String sql) throws SQLException{
        Statement stm = null;
        stm = conn.createStatement();
        ResultSet rs = stm.executeQuery(sql);
        stm.close();
        return rs;    
    }
    
    public static void readQueryResultsMessages(ResultSet rs) throws SQLException{
        List<Integer> ID = new ArrayList<Integer>();
        List<Integer> userID = new ArrayList<Integer>();
        List<Date> msgDate = new ArrayList<Date>();
        List<String> message = new ArrayList<String>();
        List<Integer> msgID = new ArrayList<Integer>();
        
        
        
        while(rs.next()){
                ID.add(rs.getInt("ID"));
                userID.add(rs.getInt("userID"));
                msgDate.add(rs.getDate("msgDate"));
                message.add(rs.getString("message"));
                msgID.add(rs.getInt("msgIndex"));
        }
        rs.close();
    }
    
    public static void closeConn() throws SQLException{
        conn.close();
    }
    
}