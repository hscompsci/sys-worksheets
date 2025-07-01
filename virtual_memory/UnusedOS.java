class OOMInternal {
	public static void main(String[] args) {
		final int BYTES = 1024 * 1024 * 1024; // 1 GB

		long[] rawArrays = new long[1024];
		for(int index = 0; index != rawArrays.length; ++index) {
			System.out.println("Successfully allocated " + index + " times with the operating system");
			rawArrays[index] = UnsafeExtension.mapInitializedMemory(BYTES);
			if(rawArrays[index] == 0) {
				System.out.println("Allocation failed!");
				return;
			}
		}
	}
}
