// Compile/run with command-line arguments: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
import jdk.internal.misc.Unsafe;

class Bench {
	public static void main(String[] args) {
		final int BYTES = 1024 * 1024 * 1024; // 1 GB

		// Like rawArray = new byte[BYTES], but do not automatically reclaim the memory when it leaves scope
		long allocationTime = currentTimeMicros();
		long rawArray = UnsafeExtension.allocateInitializedMemory(BYTES);
		allocationTime = currentTimeMicros() - allocationTime;

		Unsafe unsafe = Unsafe.getUnsafe();
		long traversalTime = currentTimeMicros();
		for(int index = 0; index != BYTES; ++index) {
			// rawArray[index] = (byte) index
			unsafe.putByte(rawArray + index, (byte) index);
		}
		traversalTime = currentTimeMicros() - traversalTime;

		System.out.println("Memory from the C language runtime:");
		System.out.println(allocationTime + " μs to allocate");
		System.out.println(traversalTime + " μs to traverse");
		System.out.println(allocationTime + traversalTime + " μs total");

		// Manually reclaim the memory, since we did not let Java manage it for us!
		unsafe.freeMemory(rawArray);
		rawArray = 0;

		System.out.println();

		// Like rawArray = new byte[BYTES], but get the memory from the operating system
		allocationTime = currentTimeMicros();
		rawArray = UnsafeExtension.mapInitializedMemory(BYTES);
		allocationTime = currentTimeMicros() - allocationTime;

		traversalTime = currentTimeMicros();
		for(int index = 0; index != BYTES; ++index) {
			// rawArray[index] = (byte) index
			unsafe.putByte(rawArray + index, (byte) index);
		}
		traversalTime = currentTimeMicros() - traversalTime;

		System.out.println("Memory directly from the operating system:");
		System.out.println(allocationTime + " μs to allocate");
		System.out.println(traversalTime + " μs to traverse");
		System.out.println(allocationTime + traversalTime + " μs total");

		UnsafeExtension.unmapMemory(rawArray, BYTES);
		rawArray = 0;
	}

	private static long currentTimeMicros() {
		return System.nanoTime() / 1000;
	}
}
