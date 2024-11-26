import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient {
	public static void main(String[] args) throws Exception {
		final int PORT = 1024;

		String server = System.console().readLine("Server to connect to? ");

		// Establish a TCP connection and build streams for reading from and writing to the server.
		Socket connection = new Socket(server, PORT);
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));

		// Prepare and enqueue a message for sending.
		String message = System.console().readLine("Message to send? ");
		out.println(message);

		// Transmit all queued messages.
		out.flush();

		// Wait for the server to send a response, then print it out.
		System.out.println(in.readLine());

		// Close the ephemeral port that we were assigned.
		connection.close();
	}
}
