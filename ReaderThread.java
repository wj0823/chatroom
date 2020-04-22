package client;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
public class ReaderThread implements Runnable
{
	private Socket socket;
	
	public ReaderThread(Socket s)
	{
		socket = s;
	}
	
    @Override
	public void run() 
    {
        BufferedReader fromServer = null;
    	try 
        {
            // get the input stream from the socket
    		fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    		while(true) 
    		{
	            // read from the socket
	    		String status = fromServer.readLine();
	    		if (status != null && status.length() != 0){
					String headerDate = fromServer.readLine();
					switch (status.trim())
					{
						case "status: 201":
							System.out.println("Successful join");
							break;
						case "status: 301":
							System.out.println(fromServer.readLine());
							break;
						case "status: 202":
							System.out.print("From " + fromServer.readLine().split("\\s+")[1] + ": ");
							System.out.println(fromServer.readLine());
							break;
						case "status: 203":
							String fromUsername = fromServer.readLine().split("\\s+")[1];
							fromServer.readLine();
							System.out.println("Private message from " + fromUsername + ": " + fromServer.readLine());
							break;
						case "status: 401":
							System.out.println("Username already taken");
							System.exit(0);
							break;
						case "status: 404":
							System.out.println("Username "+ fromServer.readLine().split("\\s+")[1] +" does not exist");
							break;
						default:
							System.out.println(status + "\n" + fromServer.readLine());
							System.err.println("Something went wrong!");
					}
				}
	             /**
	              * ok, data has now arrived. Display it in the text area,
	              * and resume listening from the socket.
	              */
    		}
        }
        catch (java.io.IOException ioe) 
        { 
        	System.err.println(ioe);
        }
    }
}