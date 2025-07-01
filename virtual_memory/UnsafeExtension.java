public class UnsafeExtension {
	public static native long allocateInitializedMemory(long bytes);

	public static native long mapInitializedMemory(long bytes);
	public static native boolean unmapMemory(long address, long bytes);

	public static native long mapFileBackedMemory(String filename);

	public static native long getAddress(String string);

	static {
		System.loadLibrary("UnsafeExtension");
	}
}
