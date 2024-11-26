import java.net.DatagramPacket;
import java.net.DatagramSocket;

class FlowControlServer {
	public static void main(String[] args) throws Exception {
		final int PACKET_SIZE = 1024; // bytes
		final int PEAK_FULLNESS_FRACTION = 4;

		if(args.length != 1) {
			System.out.println("USAGE: java FlowControlServer <port>");
			return;
		}
		
		// Parse command-line argument.
		int port = Integer.parseInt(args[0]);

		// Bind to the port so that other processes can send to it.
		DatagramSocket connection = new DatagramSocket(port);

		// Check the size of the receive buffer where the operating system stores incoming data
		// until the application has a chance to retrieve it. We will fill it to at most
		// 1/PEAK_FULLNESS_FRACTIONth of its capacity.
		int capacity = connection.getReceiveBufferSize() / PEAK_FULLNESS_FRACTION;

		// Allocate a smaller buffer for retrieving an individual packet from the operating system.
		byte[] buffer = new byte[PACKET_SIZE];
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

		// Wait for the client to send the first packet, then print it out.
		connection.receive(packet);
		System.out.print(new String(buffer, 0, packet.getLength()));

		// Establish a UDP connection so that subsequent .send()s will go to the client's address.
		connection.connect(packet.getSocketAddress());

		// Inform the client of our receive buffer size so it can avoid overfilling it.
		byte[] remaining = String.valueOf(capacity).getBytes();
		packet = new DatagramPacket(remaining, remaining.length);
		connection.send(packet);

		// Keep track of how much receive buffer space we have freed up by .receive()'ing.
		int reclaimed = 0;
		while(true) {
			// Wait for the client to send the next packet.
			packet = new DatagramPacket(buffer, buffer.length);
			connection.receive(packet);
			
			// Print it out.
			int length = packet.getLength();
			System.out.print(new String(buffer, 0, length));
			capacity -= length;
			reclaimed += length;

			if(capacity < PACKET_SIZE) {
				// We have emptied the receive buffer. Tell the client that it is safe to transmit.
				capacity += reclaimed;
				reclaimed = 0;
				remaining = String.valueOf(capacity).getBytes();
				packet = new DatagramPacket(remaining, remaining.length);
				connection.send(packet);
			}
		}
	}
}
