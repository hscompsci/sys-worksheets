class Alloc {
	public static void main(String[] args) {
		int kilobytes = Integer.parseInt(System.console().readLine("Kilobytes to allocate? "));

		// Note that libTraceAllocations will only show those allocations
		// and frees that occur between the calls to start() and stop().
		TraceAllocations.start();
		for(int count = 0; count != kilobytes; ++count) {
			byte[] unused = new byte[1024];
		}
		TraceAllocations.stop();
	}
}
