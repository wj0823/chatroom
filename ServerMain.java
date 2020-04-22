package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class ServerMain 
{
	private static final Executor exec = Executors.newCachedThreadPool();
	public static BroadcastThread bt;
	public static Vector<ChatUser> socketConnections = new Vector<ChatUser>();
	public static void main(String[] args) throws IOException 
	{
		System.out.println("Server is started");	
		
		ServerSocket sock = null;
		try
		{
			bt = new BroadcastThread();
			sock = new ServerSocket(7331);
			exec.execute(bt);
			
			while (true)
			{
				Socket clientSocket = sock.accept();
				Runnable task = new Connection(clientSocket);
				socketConnections.add(new ChatUser(clientSocket));
				System.out.println("Made connection");
				exec.execute(task);			
			}
		}
		catch (Exception e)
		{
			System.err.println(e);
		}
	}

}
