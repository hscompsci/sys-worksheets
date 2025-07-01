public class Timer {
	public static boolean set(long milliseconds, boolean repeating) {
		return set(milliseconds, repeating, "ALRM");
	}

	public static boolean set(long milliseconds, boolean repeating, String signal) {
		int identifier;
		if(signal.equals("ALRM")) {
			identifier = 0;
		} else if(signal.equals("VTALRM")) {
			identifier = 1;
		} else if(signal.equals("PROF")) {
			identifier = 2;
		} else {
			return false;
		}

		return set(milliseconds, repeating, identifier);
	}

	private static native boolean set(long milliseconds, boolean repeating, int identifier);

	static {
		System.loadLibrary("Timer");
	}
}
