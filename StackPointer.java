public class StackPointer {
	public static native long getStackPointer();

	static {
		System.loadLibrary("StackPointer");
	}
}
