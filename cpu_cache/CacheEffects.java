class CacheEffects {
	public static void main(String[] args) {
		int skip = Integer.parseInt(System.console().readLine("Number of indices to jump by? "));

		long[] big = new long[50000000];
		for(int index = 0; index != big.length; ++index) {
			// Set each element to a random in-range "index."
			big[index] = (int) (Math.random() * big.length);
		}

		long time = System.currentTimeMillis();
		for(int repetitions = 0; repetitions != 50; ++repetitions) {
			for(int position = 0; position != big.length; ++position) {
				// Jump forward by the requested number of indices, wrapping to avoid bounds errors.
				// Thus, we perform the same number of accesses regardless of the skip amount.
				int index = (int) (((long) position * skip) % big.length);

				// Access the desired element.
				int indexAtIndex = (int) big[index];

				// Access a random element to confuse the CPU's prefetcher.
				long access = big[indexAtIndex];
			}
		}
		System.out.println(System.currentTimeMillis() - time + " ms");
	}
}
