import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

class FlowControlClient {
	public static void main(String[] args) throws Exception {
		final int PACKET_SIZE = 1024; // bytes

		if(args.length != 3) {
			System.out.println("USAGE: java FlowControlClient <server> <port> <line count>");
			System.out.println();
			System.out.println("Send <line count> numbers to <server> port <port> over UDP.");
			return;
		}

		// Parse command-line arguments.
		String server = args[0];
		int port = Integer.parseInt(args[1]);
		int lines = Integer.parseInt(args[2]);

		// Establish a UDP connection so that subsequent .send()s will go to the requested address.
		DatagramSocket connection = new DatagramSocket();
		connection.connect(new InetSocketAddress(server, port));

		// Generate the message to send.
		String message = "";
		System.out.print("Generating message... ");
		for(int line = 1; line <= lines; ++line) {
			message += line + "\n";
		}
		System.out.println("done!");

		// Prepare and transmit the first packet, capping its payload at PACKET_SIZE bytes.
		byte[] buffer = message.substring(0, Math.min(PACKET_SIZE, message.length())).getBytes();
		DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
		connection.send(packet);

		// Wait for the server to reply with its receive buffer size.
		connection.receive(packet);

		// Keep track of the remaining receive buffer capacity so we do not overfill it.
		int capacity = Integer.parseInt(new String(buffer, 0, packet.getLength()));
		for(int offset = PACKET_SIZE; offset < message.length(); offset += PACKET_SIZE) {
			if(capacity < PACKET_SIZE) {
				// In the unlikely event that the server process has not yet handled *any* of our
				// data, its receive buffer is now full and further transmissions would be lost.
				System.out.println("Pausing transmission to allow server to clear its receive buffer...");

				// Wait for the server to confirm that it has cleared the backlog.
				packet = new DatagramPacket(buffer, buffer.length);
				connection.receive(packet);
				capacity = Integer.parseInt(new String(buffer, 0, packet.getLength()));

				// It is now safe to start filling the buffer again.
				System.out.println("Resuming transmission as server reports " + capacity + " bytes available!");
			}

			// Prepare and transmit the next packet.
			int length = Math.min(message.length() - offset, PACKET_SIZE);
			buffer = message.substring(offset, offset + length).getBytes();
			packet = new DatagramPacket(buffer, buffer.length);
			connection.send(packet);
			capacity -= buffer.length;
		}

		// Close the ephemeral port that we were assigned.
		connection.close();
	}
}
