package edu.seg2105.edu.server.backend;

import java.io.BufferedReader;
import java.io.*;
import java.io.InputStreamReader;

import edu.seg2105.client.common.ChatIF;

public class ServerConsole implements ChatIF {
	final public static int DEFAULT_PORT = 5555;
	EchoServer server;

	public ServerConsole(int port) {
		try {
			server = new EchoServer(port, this);
		} catch (IOException ex) {
			System.out.println("Error: Can't setup connection. Terminating client.");
		}
	    
		try {
			server.listen();
		} catch (Exception e) {
			//System.out.print(e);
			System.out.println("ERROR while listening for clients.");
	    }
		
	}
	  
	public void accept() {
		String message;

		try {
			BufferedReader fromConsole = new BufferedReader(new InputStreamReader(System.in));
		
			while (true) {
				message = fromConsole.readLine();
				server.handleMessageFromServerUI(message);
			}
		} catch (Exception ex) {
			System.out.println(ex);
		      System.out.println("ERROR while reading from console.");
		} 
		   
	}
	  
	public void display(String message) {
		System.out.println("> " + message);
	}
	
	public static void main(String[] args) {
	    String host = "";
	    int port = 0;
	
		try {
			port = Integer.parseInt(args[0]);
		} catch(Throwable e) {
			port = DEFAULT_PORT;
		}
		
		ServerConsole server = new ServerConsole(port);
		server.accept();
	}
}
