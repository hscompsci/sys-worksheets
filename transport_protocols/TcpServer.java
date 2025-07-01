import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class TcpServer {
	public static void main(String[] args) throws Exception {
		final int PORT = 1024;

		// Bind to the port so that other processes can send to it.
		ServerSocket bound = new ServerSocket(PORT);

		// Wait for a client to connect, then stop listening for other clients.
		Socket connection = bound.accept();
		bound.close();

		// Build streams for reading from and writing to the client.
		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		PrintWriter out = new PrintWriter(new OutputStreamWriter(connection.getOutputStream()));

		// Wait for the client to send a message, then print it out.
		String message = in.readLine();
		System.out.println(message);

		// Reverse the string and enqueue it for sending.
		out.println(new StringBuilder(message).reverse());

		// Transmit all queued messages.
		out.flush();

		// Unbind from the port.
		connection.close();
	}
}
