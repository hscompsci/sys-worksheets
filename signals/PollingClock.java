class PollingClock {
	private static int seconds = 0;

	public static void main(String[] args) {
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
}
