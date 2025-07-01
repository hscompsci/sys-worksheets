// Compile/run with command-line arguments: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
import jdk.internal.misc.Unsafe;

class Paging {
	private static final int TIMES_TO_ALLOCATE = 32;

	private static final int BYTES = 1024 * 1024 * 1024; // 1 GB

	private static Unsafe unsafe = Unsafe.getUnsafe();

	public static void main(String[] args) {
		long[] rawArrays = new long[TIMES_TO_ALLOCATE];
		for(int index = 0; index != rawArrays.length; ++index) {
			System.out.println("Successfully allocated " + index + " times");
			rawArrays[index] = UnsafeExtension.mapInitializedMemory(BYTES);
			if(rawArrays[index] == 0) {
				System.out.println("Allocation failed!");
				return;
			}
		}

		long last = System.currentTimeMillis();
		for(int index = 0; index != rawArrays.length; ++index) {
			for(int offset = 0; offset != BYTES; ++offset) {
				unsafe.putByte(rawArrays[index] + offset, (byte) (index * offset));
			}
			verifyArray(rawArrays[index], index);

			long time = System.currentTimeMillis();
			System.out.println("Wrote and read back allocation " + index + " in " + (time - last) + " ms");
			last = time;
		}

		last = System.currentTimeMillis();
		for(int index = 0; index != rawArrays.length; ++index) {
			verifyArray(rawArrays[index], index);

			long time = System.currentTimeMillis();
			System.out.println("Read back allocation " + index + " in " + (time - last) + " ms");
			last = time;
		}
	}

	private static void verifyArray(long rawArray, int index) {
		for(int offset = 0; offset != BYTES; ++offset) {
			if(unsafe.getByte(rawArray + offset) != (byte) (index * offset)) {
				System.out.println("Unexpected value in block " + index + " offset " + offset);
			}
		}
	}
}
