import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class UdpClient {
	public static void main(String[] args) throws Exception {
		final int PORT = 1024;

		String server = System.console().readLine("Server to connect to? ");

		// Establish a UDP connection so that subsequent .send()s will go to the requested address.
		DatagramSocket connection = new DatagramSocket();
		connection.connect(new InetSocketAddress(server, PORT));

		// Prepare and transmit a packet.
		byte[] message = System.console().readLine("Message to send? ").getBytes();
		DatagramPacket packet = new DatagramPacket(message, message.length);
		connection.send(packet);

		// Wait for the server to send a response, then print it out.
		packet = new DatagramPacket(message, message.length);
		connection.receive(packet);
		System.out.println(new String(message, 0, packet.getLength()));

		// Close the ephemeral port that we were assigned.
		connection.close();
	}
}
