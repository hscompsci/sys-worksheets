import java.net.DatagramPacket;
import java.net.DatagramSocket;

class UdpServer {
	public static void main(String[] args) throws Exception {
		final int PACKET_SIZE = 16; // bytes
		final int PORT = 1024;

		// Bind to the port so that other processes can send to it.
		DatagramSocket connection = new DatagramSocket(PORT);

		// Allocate a buffer for receiving a packet's payload.
		byte[] buffer = new byte[PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		// Wait for the client to send a packet.
		connection.receive(packet);

		// Decode the payload as a string and print it out.
		String message = new String(buffer, 0, packet.getLength());
		System.out.println(message);

		// Establish a UDP connection so that subsequent .send()s will go to the client's address.
		if(connection.getRemoteSocketAddress() == null) {
			connection.connect(packet.getSocketAddress());
		}

		// Reverse the string and send it back to the client.
		buffer = String.valueOf(new StringBuilder(message).reverse()).getBytes();
		packet = new DatagramPacket(buffer, buffer.length);
		connection.send(packet);

		// Unbind from the port.
		connection.close();
	}
}
