// This file contains material supporting section 3.7 of the textbook:
// "Object Oriented Software Engineering" and is issued under the open-source
// license found at www.lloseng.com 

package edu.seg2105.client.backend;

import ocsf.client.*;

import java.io.*;

import edu.seg2105.client.common.*;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 *
 * @author Dr Timothy C. Lethbridge
 * @author Dr Robert Lagani&egrave;
 * @author Fran&ccedil;ois B&eacute;langer
 */
public class ChatClient extends AbstractClient
{
  //Instance variables **********************************************
  
  /**
   * The interface type variable.  It allows the implementation of 
   * the display method in the client.
   */
  ChatIF clientUI; 
  
  String loginID;

  
  //Constructors ****************************************************
  
  /**
   * Constructs an instance of the chat client.
   *
   * @param host The server to connect to.
   * @param port The port number to connect on.
   * @param clientUI The interface type variable.
   */
  
  /*public ChatClient(String host, int port, ChatIF clientUI) 
    throws IOException 
  {
    super(host, port); //Call the superclass constructor
    //this.clientUI = clientUI;
    //openConnection();
	clientUI.display("Login information must be provided.");
	quit();
  }*/
  
  public ChatClient(String loginID, String host, int port, ChatIF clientUI) 
    throws IOException 
  {
	  super(host, port); //Call the superclass constructor

	if (loginID.equals("")) {
		clientUI.display("Login information must be provided.");
		quit();
	} else {
	    this.clientUI = clientUI;
	    this.loginID = loginID;
	    openConnection();
	    sendToServer("#login " + loginID);
	}
  }

  
  //Instance methods ************************************************
    
  /**
   * This method handles all data that comes in from the server.
   *
   * @param msg The message from the server.
   */
  public void handleMessageFromServer(Object msg) 
  {
    clientUI.display(msg.toString());
    
    
  }

  /**
   * This method handles all data coming from the UI            
   *
   * @param message The message from the UI.    
   */
  public void handleMessageFromClientUI(String message)
  {
	  if (message.charAt(0) == '#') {
		  if (message.equalsIgnoreCase("#quit")) {
		      quit();
		      
		  } else if (message.equalsIgnoreCase("#logoff")) {
			  try {
				  closeConnection();
		      } catch(IOException e) {}
			  
			  clientUI.display("You have logged off.");
		      
		  } else if (message.toLowerCase().startsWith("#sethost")) {
			  if (isConnected()) {
				  clientUI.display("Please log off first.");
			  } else {
				  setHost(message.substring(9));
			  	  clientUI.display ("Host changed to " + getHost());
			  }
		  } else if (message.toLowerCase().startsWith("#setport")) {
			  if (isConnected()) {
				  clientUI.display("Please log off first.");
			  } else {
				  try {
					  int newPort = Integer.parseInt(message.substring(9));
					  setPort(newPort);
					  clientUI.display ("Port changed to " + getPort());
				  } catch (Exception e) {
					  clientUI.display("An error occurred while setting client port.");
				  }
			  }
			      
		  } else if (message.toLowerCase().startsWith("#login")) {
			  if (isConnected()) {
		    	  clientUI.display("You are already logged in.");
		    	  return;
		      }
		      
		      String login = message.substring(7);
		      try {
		    	  openConnection();
		    	  sendToServer("#login " + login);
		      } catch (Exception e) {
		    	  clientUI.display("Cannot connect to server.");
		      }
		
		    } else if (message.equalsIgnoreCase("#gethost")) {
			  clientUI.display("Current host: " + getHost());
			  
			} else if (message.equalsIgnoreCase("#getport")) {
		      clientUI.display("Current port: " + Integer.toString(getPort()));
		      
		    }
			    
		  } else {
			  try {
				  sendToServer(message);
			  } catch (IOException e) {
				  clientUI.display ("Message could not be sent.");
				  quit();
			  }
		  
		  }
  }
  
  public void quit() {
    try {
      closeConnection();
    } catch(IOException e) {}
    System.exit(0);
  }
  
  protected void connectionClosed() {
	  clientUI.display ("Connection closed.");
  }
  
  protected void connectionException(Exception exception) {
	  clientUI.display ("The server (" + getHost() + ", " + getPort() + ") has shut down.");
  }
  
}
//End of ChatClient class
