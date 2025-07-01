public class TraceAllocations {
	public static void start() {
		final int DEFAULT_MINIMUM_BYTES = 1024;

		start(DEFAULT_MINIMUM_BYTES);
	}

	public static native void start(long minimumBytes);
	public static native void stop();

	static {
		final String NATIVE_LIBRARY = "TraceAllocations";

		String preloads = System.getenv("DYLD_INSERT_LIBRARIES");
		if(preloads == null || !preloads.contains(NATIVE_LIBRARY)) {
			String command = ProcessHandle.current().info().commandLine().get();
			command = command.substring(command.indexOf("java"));
			System.err.println(
				"WARNING: For tracing to work, rerun me with the command: " + 
				"DYLD_INSERT_LIBRARIES=lib" + NATIVE_LIBRARY + ".dylib ./" + command
			);
		}
		System.loadLibrary(NATIVE_LIBRARY);
	}
}
