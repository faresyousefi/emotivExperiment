import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.Collection;

import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.framing.Framedata;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.util.*;


public class ExperimentServer extends WebSocketServer {

	AttentionExperiment experiment;
	public ExperimentServer(int port) throws UnknownHostException {
		super( new InetSocketAddress( experiment.getWebsocketsPort() ) );
	}

	public ExperimentServer( InetSocketAddress address ) {
		super( address );
	}

	@Override
	public void onOpen( WebSocket conn, ClientHandshake handshake ) {

	}

	@Override
	public void onClose( WebSocket conn, int code, String reason, boolean remote ) {
		this.sendToAll( conn + " disconnected!" );
	}

	@Override
	public void onMessage( WebSocket conn, String message ) {
		System.out.println( conn + ": " + message );
	}

	public static void main( String[] args ) throws InterruptedException , IOException {
		if(args.length < 2){
			System.out.println("Usage: <outputDir> <participantInt>");
			return;
		}
		WebSocketImpl.DEBUG = true;
		String outputDir = args[0];
		int participant = Integer.parseInt(args[1]);


		ExperimentServer s = new ExperimentServer(outputDir, participant);
		s.start();
		System.out.println( "ExperimentServer started on port: " + s.getPort() );

		BufferedReader sysin = new BufferedReader( new InputStreamReader( System.in ) );
		while ( true ) {
			String in = sysin.readLine();
			s.sendToAll( in );
			if( in.equals( "exit" ) ) {
				s.stop();
				break;
			} else if( in.equals( "restart" ) ) {
				s.stop();
				s.start();
				break;
			}
		}
	}
	@Override
	public void onError( WebSocket conn, Exception ex ) {
		ex.printStackTrace();
		if( conn != null ) {
			// some errors like port binding failed may not be assignable to a specific websocket
		}
	}

	/**
	* Sends <var>text</var> to all currently connected WebSocket clients.
	*
	* @param text
	*            The String to send across the network.
	* @throws InterruptedException
	*             When socket related I/O errors occur.
	*/
	public void sendToAll( String text ) {
		Collection<WebSocket> con = connections();
		synchronized ( con ) {
			for( WebSocket c : con ) {
				c.send( text );
			}
		}
	}
}
