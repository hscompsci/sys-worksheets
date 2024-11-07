class UnusedJava {
	public static void main(String[] args) {
		final int BYTES = 1024 * 1024 * 1024; // 1 GB

		Object[] arrays = new Object[1024];
		for(int index = 0; index != arrays.length; ++index) {
			System.out.println("Successfully allocated " + index + " times with the Java runtime");
			arrays[index] = new byte[BYTES];
		}
	}
}
