//import jdk.internal.misc.Signal;

class SignalClock {
	private static int seconds = 0;

	public static void main(String[] args) {
		// Ask the operating system to send us a SIGALRM every second.
		subscribeToSignal();

		System.out.print("Hit Ctrl+C to terminate... 000");
		
		while(true) {
			long time = System.currentTimeMillis();
			while(System.currentTimeMillis() - time < 1000) {}

			updateDisplay();
		}
	}

	private static void updateDisplay() {
		++seconds;

		String formatted = String.valueOf(seconds);
		for(int count = 0; count != formatted.length(); ++count) {
			// Print backspace character to move the cursor left.
			System.out.print("\b");
		}
		System.out.print(formatted);
	}

	private static void subscribeToSignal() {
		// TODO: Uncomment to make SIGALRM call handleSignal() instead of taking its default action.
		//Signal alarm = new Signal("ALRM");
		//Signal.handle(alarm, SignalClock::handleSignal);

		Timer.set(1000, true);
	}

	/*private static void handleSignal(Signal signal) {
		updateDisplay();
	}
	*/
}
