/*
 * ChatUser.java
 * 
 * 
 * Used to store connected users with there related socket and username
 */



package server;

import java.net.Socket;

public class ChatUser
{
	private Socket socket;
	private String username;
	
	public ChatUser(Socket socket)
	{
		this.socket = socket;
		this.username = "";
	}
	
	public ChatUser(Socket socket, String username)
	{
		this.socket = socket;
		this.username = username;
	}
	
	public Socket getSocket()
	{
		return socket;
	}
	
	public String getUsername()
	{
		return username;
	}
	
	public void setUsername(String username)
	{
		this.username = username;
	}

}
