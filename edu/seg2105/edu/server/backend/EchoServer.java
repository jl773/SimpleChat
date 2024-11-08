package edu.seg2105.edu.server.backend;
// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 


import java.io.*;

import ocsf.server.AbstractServer;
import ocsf.server.ConnectionToClient;
import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods in the abstract 
 * superclass in order to give more functionality to the server.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;re
 * @author Fran&ccedil;ois B&eacute;langer
 * @author Paul Holden
 */
public class EchoServer extends AbstractServer 
{
  //Class variables *************************************************
  
  /**
   * The default port to listen on.
   */
  final public static int DEFAULT_PORT = 5555;
  ChatIF serverUI;
  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the echo server.
   *
   * @param port The port number to connect on.
   */
  public EchoServer(int port) 
  {
    super(port);
  }
  
  public EchoServer(int port, ChatIF serverUI) throws IOException
  {
    super(port);
    this.serverUI = serverUI;
  }

  
  //Instance methods ************************************************
  
  public void handleMessageFromClient
    (Object msg, ConnectionToClient client)
  {
	  
	  if (msg.toString().startsWith("#login ")) {
		  if (client.getInfo("loginID") != null) {
			  try {
				  client.sendToClient("You are already logged in.");
			  } catch (IOException e) {}
			  
			  return;
	      } else {
	    	  this.sendToAllClients("Message received: " + msg + " from null");
	    	  this.sendToAllClients(msg.toString().substring(7) + " has logged on.");
	      }
	      
	      client.setInfo("loginID", msg.toString().substring(7));
	  } else {
	    	if (client.getInfo("loginID") == null) {
	    		try {
	    			client.sendToClient("You need to login before you can chat.");
	    			client.close();
	    		} catch (IOException e) {}
	    		
	    		return;
	    	}
	      
		    //System.out.println("Message received: " + msg + " from " + client);
		    this.sendToAllClients("Message received: " + msg + " from " + client);
	  }
	  
	  //System.out.println("Message received: " + msg + " from " + client);
	  //this.sendToAllClients("Message received: " + msg + " from " + client);
  }
  
  public void handleMessageFromServerUI(String message)
  {
	  if (message.charAt(0) == '#') {
		  if (message.equalsIgnoreCase("#quit")) {
			  quit();
		  } else if (message.equalsIgnoreCase("#stop")) {
			  stopListening();
			  serverStopped();
		  } else if (message.equalsIgnoreCase("#close")) {
			  try {
				  close();
			  } catch(IOException e) {}
			  
		  } else if (message.toLowerCase().startsWith("#setport")) {
	          if (getNumberOfClients() == 0 && !isListening()) {
	        	  int newPort = Integer.parseInt(message.substring(9));
	        	  setPort(newPort);
	        	  serverUI.display("Server port changed to " + getPort());
	          } else {
	        	  serverUI.display("The server is not closed. Port cannot be changed.");
	          }
        } else if (message.equalsIgnoreCase("#start")) {
        	if (!isListening()) {
        		try {
        			serverStarted();
        			listen();
        		} catch(Exception ex) {
        			serverUI.display("ERROR: Could not listen for clients.");
        		}
        	  
        	} else {
        		serverUI.display("The server is already listening for clients.");
        	  
        	}
        } else if (message.equalsIgnoreCase("#getport")) {
        	serverUI.display("Currently port: " + Integer.toString(getPort()));
        }
	  } else {
    	serverUI.display("SERVER MSG> " + message);
      this.sendToAllClients("SERVER MSG> " + message);
      
    }
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server starts listening for connections.
   */
  protected void serverStarted()
  {
    System.out.println
      ("Server listening for connections on port " + getPort());
  }
  
  /**
   * This method overrides the one in the superclass.  Called
   * when the server stops listening for connections.
   */
  protected void serverStopped()
  {
    System.out.println
      ("Server has stopped listening for connections.");
  }
  
  protected void clientConnected(ConnectionToClient client) {
    String message = "A client has connected.";
    this.sendToAllClients(message);
    System.out.println(message);
    //serverUI.display(message);
  }
  
  synchronized protected void clientDisconnected(ConnectionToClient client) {
    String message = client.getInfo("loginID").toString() + " has disconnected.";
    this.sendToAllClients(message);
    System.out.println(message);
  }
  
  synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
    String message = client.getInfo("loginID").toString() + " has disconnected.";
    this.sendToAllClients(message);
    System.out.println(message);
  }
  
  public void quit()
  {
    try {
      close();
    } catch(IOException e) {}
    System.exit(0);
  }
  
  //Class methods ***************************************************
  
  /**
   * This method is responsible for the creation of 
   * the server instance (there is no UI in this phase).
   *
   * @param args[0] The port number to listen on.  Defaults to 5555 
   *          if no argument is entered.
   */
  public static void main(String[] args) 
  {
    int port = 0; //Port to listen on

    try
    {
      port = Integer.parseInt(args[0]); //Get port from command line
    }
    catch(Throwable t)
    {
      port = DEFAULT_PORT; //Set port to 5555
    }
	
    EchoServer sv = new EchoServer(port);
    
    try 
    {
      sv.listen(); //Start listening for connections
    } 
    catch (Exception ex) 
    {
    	System.out.print(ex);
      System.out.println("ERROR: Could not listen for clients.");
    }
  }
}
//End of EchoServer class
