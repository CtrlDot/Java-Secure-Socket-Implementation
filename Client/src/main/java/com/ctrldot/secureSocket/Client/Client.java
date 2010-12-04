package com.ctrldot.secureSocket.Client;

import java.io.BufferedWriter;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;

import javax.net.ServerSocketFactory;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

public class Client
{
	/**
	 * Main Method for Client program.  Simply connects to predetermined (hard coded) port and passes in a message
	 * @author ctrl-dot
	 */
	public static void main(String[] args)
	{
		System.out.println("Client Startup.");
		SSLSocket socket = null;
		Socket socket2 = null;
		PrintWriter out = null;
		
		// Path to keystore.  Note, same as Server.java.  Don't do this in production
		// Keystore for client should only have pub cert from server
		String keyStorePath = "d:\\work\\keystore\\keystore.jks";
		
		try
		{

			KeyStore serverPubKey = KeyStore.getInstance("JKS");
			serverPubKey.load(new FileInputStream(keyStorePath), "!MyKEYP@SS!".toCharArray());
			
			// Trust manager on this end.  I(the client) trust the servers public key
			TrustManagerFactory trustManager = TrustManagerFactory.getInstance("SunX509");
			trustManager.init(serverPubKey);
			
			SSLContext sslContext = SSLContext.getInstance("TLS");
			sslContext.init(null, trustManager.getTrustManagers(), SecureRandom.getInstance("SHA1PRNG"));
			
			SSLSocketFactory factory = sslContext.getSocketFactory();
			
			socket = (SSLSocket)factory.createSocket("localhost", 1234);
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())));
			String input = "Hello server, this is client.";
			out.println(input);
			out.flush();
		}
		catch (UnknownHostException e)
		{
			e.printStackTrace();
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
				if (out != null) out.close();
				if (socket != null)socket.close();
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
			System.out.println("Client shutdown complete.");
		} // end Finally
	} // end main
}// end class
