package com.ctrldot.secureSocket.Server;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.KeyStore;
import java.security.SecureRandom;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocket;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;

public class Server 
{
	/**
	 * Server main method.  Starts up and listens on hard coded port for client communication.
	 * 
	 * @param args
	 */
	public static void main(String[] args) 
	{
		System.out.println("Socket Server Startup.");
		
		SSLSocket socket = null;
		BufferedReader in = null;
		ServerSocket listener = null;
		
		// Path to keystore that contains the pub/priv keys for the server
		String keyStore = "d:\\work\\keystore\\keystore.jks";
			
		try 
		{
			// Setup a KeyStore instance
			KeyStore serverKeys = KeyStore.getInstance("JKS");
			// Don't store your password like this in production, just for example purposes
			// Could use something like Jasyp
			serverKeys.load(new FileInputStream(keyStore), "!MyKEYP@SS!".toCharArray());
			
			// Loads the key to use from the keystore.  Assumes that the keystore only has the key for this server (which it should)
			KeyManagerFactory serverKeyManager = KeyManagerFactory.getInstance("SunX509");
			serverKeyManager.init(serverKeys,"!MyKEYP@SS!".toCharArray());
			
			// Setup SSL Context and create socket factory
			// Notice using SecureRandom
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(serverKeyManager.getKeyManagers(), null, SecureRandom.getInstance("SHA1PRNG"));
			
			SSLServerSocketFactory sslSocketFactory = sslContext.getServerSocketFactory();
			
			SSLServerSocket secureSocket = (SSLServerSocket)sslSocketFactory.createServerSocket(1234);
			
			System.out.println("Socket initialized and listenining.");
			socket = (SSLSocket)secureSocket.accept();
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			String input = null;
			while ((input = in.readLine()) != null)
			{
				System.out.println("FROM CLIENT: " + input);
			}
		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			try
			{
				if (in != null) in.close();
				if (socket != null) socket.close();
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
			System.out.println("Server shutdown complete");
		} // end Finally
	}// end main
}// end class
