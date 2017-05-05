package com.digitalmatrix.pack.recursive.devanalysis;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class ConnectionFactory {
	
	// JDBC driver name and database URL
	   static final String JDBC_DRIVER = "com.mysql.jdbc.Driver";  
	   static final String DB_URL = "jdbc:mysql://mysql796.umbler.com:41890/recursive_logs";

	   //  Database credentials
	   static final String USER = "recursive";
	   static final String PASS = "R3curs1v3";
	   
	   static Connection conn;
	   static Thread thread;
	   
	   public static Connection getConnection(){
		   try {
			   System.out.println("Connecting to database");
			   Class.forName("com.mysql.jdbc.Driver");
			   conn = DriverManager.getConnection(DB_URL,USER,PASS);   
			   System.out.println("Connected " + conn);
			   return conn;
		   }
		   catch (ClassNotFoundException e) {
			   System.out.println(e.getMessage());
		   } catch (SQLException e) {
			   System.out.println(e.getMessage());
		   }
		   System.out.println("Error connecting to Database ");
		   return null;
	   }
	   
	   public static void sendCrashError(ErrorElement error){
		   try{
			   
			   String sql;
			   sql = "INSERT INTO crashes (ip, pc_name, exception_name, general_state, total_layers, active_layer, currentLayerState, stackTrace, user_text, level)"
			   		+ " values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			   PreparedStatement stmt = conn.prepareStatement(sql);
			   
			   stmt.setString(1, InetAddress.getLocalHost().getHostAddress() + "");
			   stmt.setString(2, InetAddress.getLocalHost().getHostName() + "");
			   stmt.setString(3, error.exceptionName);
			   stmt.setString(4, error.generalState);
			   stmt.setInt(5, error.totalLayers);
			   stmt.setInt(6, error.activeLayer);
			   stmt.setString(7, error.currentLayerState);
			   stmt.setString(8, error.stackTraceText());
			   stmt.setString(9, error.userText);
			   stmt.setString(10, error.level);
			   
			   stmt.execute();
			   
		   }
		   catch(SQLException e){
			   System.out.println(e.getMessage());
		   }
		   catch (UnknownHostException e) {
			   System.out.println(e.getMessage());
		   }
	   }
	   
	   static long timer = System.nanoTime();
	   
	   public static void sendSlowError(ErrorElement error){
		   if((System.nanoTime() - timer)/1000000.0 > 5000){

		   if(thread == null){
			   SendSlow s = new SendSlow();
			   s.error = error;
			   thread = new Thread(s);
			   thread.start();
			   timer = System.nanoTime();
		   }
		   else{
			   if(!thread.isAlive()){
				   SendSlow s = new SendSlow();
				   s.error = error;
				   thread = new Thread(s);
				   thread.start();
				   timer = System.nanoTime();
			   }
		   }
		   
	   }
		  
	   }

}

class SendSlow implements Runnable{

	public ErrorElement error;
	
	@Override
	public void run() {
		Connection conn = ConnectionFactory.getConnection();
		 try{
			    
			   String sql;
			   sql = "INSERT INTO slowa (ip, pc_name, action, miliseconds, total_layers, active_layer, level)"
			   		+ " values (?, ?, ?, ?, ?, ?, ?)";
			   PreparedStatement stmt = conn.prepareStatement(sql);
			   
			   stmt.setString(1, InetAddress.getLocalHost().getHostAddress() + "");
			   stmt.setString(2, InetAddress.getLocalHost().getHostName() + "");
			   stmt.setString(3, error.exceptionName);
			   stmt.setFloat(4, error.FPS);
			   stmt.setInt(5, error.totalLayers);
			   stmt.setInt(6, error.activeLayer);
			   stmt.setString(7, error.level);
			   
			   stmt.execute();
			   System.out.println("Inserted successfully");
		   }
		   catch(SQLException e){
			   System.out.println(e.getMessage());
		   }
		   catch (UnknownHostException e) {
			   System.out.println(e.getMessage());
		   }			
		}
	
}
