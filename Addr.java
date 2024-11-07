// Compile/run with command-line arguments: --add-exports java.base/jdk.internal.misc=ALL-UNNAMED
import jdk.internal.misc.Unsafe;

class Addr {
	public static void main(String[] args) {
		long address = UnsafeExtension.getAddress("string literal");
		System.out.println("Memory address: " + address);

		String replacement = System.console().readLine("Hit enter to continue...");

		System.out.print("string literal: ");
		System.out.println("string literal");

		if(!replacement.equals("")) {
			Unsafe unsafe = Unsafe.getUnsafe();
			int length = Math.min("string literal".length(), replacement.length());
			for(int index = 0; index != length; ++index) {
				unsafe.putChar(address + index, replacement.charAt(index));
			}

			System.out.print("string literal: ");
			System.out.println("string literal");
		}
	}
}
