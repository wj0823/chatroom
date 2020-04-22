package server;
/**
 * This is the separate thread that services each
 * incoming echo client request.
 *
 * @author Greg Gagne 
 */

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.*;
import java.util.Date;

public class Connection implements Runnable
{
	private BufferedReader  fromClient = null;
	private BufferedWriter toClient = null;
	private Socket client = null;
//	private static Handler handler = new Handler();
	
	public Connection(Socket client) throws IOException {
		this.client = client;
		fromClient = new BufferedReader(new InputStreamReader(client.getInputStream()));
		toClient = new BufferedWriter(new OutputStreamWriter(client.getOutputStream()));
	}

    /**
     * This method runs in a separate thread.
     */	
	public void run()
	{		
		try 
		{
			/**
			 * get the input and output streams associated with the socket.
			 */
			
			while (true)
			{
				String status = fromClient.readLine();
				
				if (status != null && status.length() > 0)
				{
					String datestamp = fromClient.readLine().trim();
					String fromUsername = null;
					String message = null;
					switch (status)
					{
					case "status: 200":
						boolean usernameTaken = false;
						String username = fromClient.readLine().trim();
						for (ChatUser cu : ServerMain.socketConnections)
						{
							if (cu.getUsername().equals(username))
							{
								usernameTaken = true;
								ServerMain.socketConnections.remove(ServerMain.socketConnections.size() - 1);
								badUsername(username);
		
							}
						}	
						
						if (usernameTaken)
							break;
						ServerMain.socketConnections.get(ServerMain.socketConnections.size() - 1).setUsername(username);
						
						toClient.write("status: 201" + "\r\n");
						toClient.write(datestamp + "\r\n");
						toClient.flush();
						
						ServerMain.bt.addMessage("status: 301\r\n" + datestamp + "\r\n" + username + " has joined the chat");
						
						System.out.println("new user name request/join " + username);
						break;
						
					case "status: 202":
		                fromUsername = fromClient.readLine().trim();
		                message = fromClient.readLine().trim();
		                ServerMain.bt.addMessage(status + "\r\n" + datestamp  + "\r\n" + fromUsername + "\r\n" + message);
		                
		                break;
		                
		            case "status: 203":
		                fromUsername = fromClient.readLine().trim();
		                String toUsername = fromClient.readLine().trim();
		                message = fromClient.readLine().trim();
		                ServerMain.bt.sendPrivateMessage(status, datestamp, fromUsername, toUsername, message);
		                break;
		
						
					default:
						System.out.println(status + datestamp + fromClient.readLine());
						System.err.println("Something went wrong");
					}
				}
			}

		}
		catch (java.io.IOException ioe) 
		{
			System.err.println(ioe);
		}
	}

	private void badUsername(String username) throws IOException 
	{
		// TODO Auto-generated method stub
		
		System.out.println("User tried joining with a used username.");
		
		toClient.write("status: 401" + "\r\n");
		toClient.write("date: " + new Date().toGMTString() + "\r\n");
		toClient.write("\r\n\r\n");
		toClient.close();
		fromClient.close();
	}
}

