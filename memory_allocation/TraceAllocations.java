public class TraceAllocations {
	public static void start() {
		final int DEFAULT_MINIMUM_BYTES = 1024;

		start(DEFAULT_MINIMUM_BYTES);
	}

	public static native void start(long minimumBytes);
	public static native void stop();

	static {
		final String NATIVE_LIBRARY = "TraceAllocations";

		if(
			!isPreloaded("DYLD_INSERT_LIBRARIES", NATIVE_LIBRARY)
			&&
			!isPreloaded("LD_PRELOAD", NATIVE_LIBRARY)
		) {
			System.err.println("WARNING: For tracing to work, rerun me with one of the commands:");
			System.err.println("  macOS: DYLD_INSERT_LIBRARIES=lib" + NATIVE_LIBRARY + ".dylib ./java");
			System.err.println("  GNU/Linux: LD_PRELOAD=./lib" + NATIVE_LIBRARY + ".dylib java");
		}
		System.loadLibrary(NATIVE_LIBRARY);
	}

	private static boolean isPreloaded(String environment, String library) {
		String preloads = System.getenv(environment);
		return preloads != null && preloads.contains(library);
	}
}
