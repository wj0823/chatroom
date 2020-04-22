package client;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ClientMain 
{

	private static final Executor exec = Executors.newCachedThreadPool();
	
	public static void main(String[] args) throws IOException 
	{
		// TODO Auto-generated method stub

		if (args.length != 2)
		{
			System.out.println("Please run as: java client/ClientMain <server ip> <username>");
			System.exit(1);
		}
		
		BufferedWriter toServer = null;		
		Socket sock = null;
		ReaderThread rt = null;
		String serverAddress = args[0];
		String username = args[1];
		Date date = new Date();
		Scanner myObj = new Scanner(System.in);
		
		if (username.length() > 100)
		{
			System.out.println("Username is too long. It should be under 100 characters");
			myObj.close();
			System.exit(0);
		}

	
		try 
		{
			// open socket and buffered read and write
			sock = new Socket(serverAddress,7331);
			toServer = new BufferedWriter(new OutputStreamWriter(sock.getOutputStream()));
			
			rt = new ReaderThread(sock);
			SimpleDateFormat sdf = new SimpleDateFormat();
			sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
			
			// Send username request
			toServer.write("status: 200\r\n");
			toServer.write("date: " + date.toGMTString() +"\r\n");
			toServer.write(username + "\r\n");
			toServer.write("\r\n\r\n");
			
			toServer.flush();
			
			// Read response from server and display it to user
			exec.execute(rt);
			
			while (true)
			{
				// read line from scanner
				  
				String message = myObj.nextLine();
				// Check if first word is "/pm"
				String[] messageSplit = message.split("\\s+");
				if( messageSplit.length > 0 && messageSplit[0].equals("/pm"))
				{
					// if yes, parse out username and message and send message to server as private message to user
					String privateName = messageSplit[1];
					if (message.substring(4 + privateName.length()).length() <= 1000)
					{
						
						toServer.write("status: 203\r\n");
						toServer.write("date: " + date.toGMTString() +"\r\n");
						toServer.write("from: " + username + "\r\n");
						toServer.write("to: " + privateName + "\r\n");
						toServer.write(message.substring(4 + privateName.length()));
						toServer.write("\r\n\r\n");
						toServer.flush();
						System.out.println("pm sent");
					}
					else
					{
						System.out.println("Message too long. Please enter shorter message.");
					}
				}
				// if no, parse out username and message and send message to server as general message to user
				else
				{
					if (message.length() > 0 && message.length() <= 1000)
					{
						toServer.write("status: 202\r\n");
						toServer.write("date: " + date.toGMTString() +"\r\n");
						toServer.write("from: " + username + "\r\n");
						toServer.write(message);
						toServer.write("\r\n\r\n");
						toServer.flush();
					}
				}
				
			}
		}
		catch (java.net.ConnectException ce)
		{
			System.out.println("Could not connect to server. Please check ip address and try again.");
			System.out.println("Please run as: java client/ClientMain <server ip> <username>");
			System.exit(-1);
		}
		catch (UnknownHostException uhe) 
		{
			System.err.println("Unknown server: " + args[0]);
		}
		myObj.close();
	}

}
