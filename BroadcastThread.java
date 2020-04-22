package server;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.net.SocketException;
import java.util.Date;
import java.util.Vector;

public class BroadcastThread implements Runnable
{
	Date date = new Date();
	Vector<String> messageVector;
	public BroadcastThread()
	{
		messageVector = new Vector<String>();
	}
    public void run() 
    {
    	
    	BufferedWriter toClient = null;
        while (true) 
        {
            // sleep for 1/10th of a second
            try { Thread.sleep(100); } catch (InterruptedException ignore) { }
            
            
            if (messageVector.size() > 0)
            {
            	String[] messages = new String[messageVector.size()];
            	messages = messageVector.toArray(messages);
            	messageVector.removeAllElements();
            	System.out.println("Broadcasting");
	            for (String s : messages)
	            {
	            	if (s != null)
	            		for (int i = 0; i < ServerMain.socketConnections.size(); i++)
		            	{
	            			ChatUser cu = ServerMain.socketConnections.get(i);
		            		try 
		            		{
		            			toClient = new BufferedWriter(new OutputStreamWriter(cu.getSocket().getOutputStream()));
								toClient.write(s + "\r\n");
								toClient.write("\r\n\r\n");
								toClient.flush();
							} 
		            		catch (SocketException e)
		            		{
		            			System.err.println(cu.getUsername() + " no longer is connected");
		            			ServerMain.socketConnections.remove(cu);
		            			addMessage(cu.getUsername() + " has disconnected.");
		            			try 
		            			{
									toClient.close();
								} 
		            			catch (IOException e1) 
		            			{
									e1.printStackTrace();
								}
		            		}
		            		catch (IOException e) 
		            		{
								e.printStackTrace();
							}
		            	}
	            }
	            messageVector.clear();
	            messageVector.trimToSize();
            }
            /**
             * check if there are any messages in the Vector. If so, remove them
             * and broadcast the messages to the chatroom
             */
        }
    }
	public void addMessage(String string) 
	{
		// TODO Auto-generated method stub
		messageVector.addElement(string);
	}
	
	public void sendPrivateMessage(String statusHeader, String datestampHeader, String fromUsernameHeader, String toUsernameHeader, String message)
	{
		Socket toSocket = null;
		String fromUsername = fromUsernameHeader.split("\\s+")[1];
		String toUsername = toUsernameHeader.split("\\s+")[1];
		for (ChatUser cu: ServerMain.socketConnections)
		{
			if (cu.getUsername().equals(toUsername))
			{
				toSocket = cu.getSocket();
			}
		}
		
		if (toSocket != null)
		{
			try
			{
				BufferedWriter toClient = null;
				toClient = new BufferedWriter(new OutputStreamWriter(toSocket.getOutputStream()));
				toClient.write(statusHeader + "\r\n");
				toClient.write(datestampHeader + "\r\n");
				toClient.write(fromUsernameHeader + "\r\n");
				toClient.write(toUsernameHeader + "\r\n");
				toClient.write(message + "\r\n");
				toClient.write("\r\n\r\n");
				toClient.flush();
			}
			catch (IOException ioe)
			{
				System.err.println(ioe);
			}
		}
		else
		{
			System.out.println("user does not exist");
			
			Socket fromSocket = null;
			for (ChatUser cu: ServerMain.socketConnections)
			{
				if (cu.getUsername().equals(fromUsername))
				{
					fromSocket = cu.getSocket();
				}
			}
			if (fromSocket != null)
			{
				try
				{
					BufferedWriter fromClient = new BufferedWriter(new OutputStreamWriter(fromSocket.getOutputStream()));
					
					fromClient.write("status: 404" + "\r\n");
					fromClient.write("date: " +date.toGMTString() + "\r\n");
					fromClient.write("from: " + toUsername +  "\r\n");
					fromClient.write("\r\n\r\n");
					fromClient.flush();
				}
				catch (IOException ioe)
				{
					System.err.println(ioe);
				}
			}
			else
			{
				System.err.println("fromClient is null");
			}
		}
	}
} 