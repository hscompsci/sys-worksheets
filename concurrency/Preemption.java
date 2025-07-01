class Preemption {
	public static void main(String[] args) {
		long then = System.currentTimeMillis();
		while(true) {
			long now = System.currentTimeMillis();
			if(now - then > 1) {
				System.out.println(now - then + " ms");
			}
			then = now;
		}
	}
}
