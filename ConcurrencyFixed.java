import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ConcurrencyFixed {
	private static StringBuffer message = new StringBuffer();

	public static void main(String[] args) throws InterruptedException {
		final int COUNT = 1000;

		ExecutorService workers = Executors.newCachedThreadPool();
		long time = System.currentTimeMillis();
		for(int count = 0; count != COUNT; ++count) {
			workers.submit(ConcurrencyFixed::append);
		}
		workers.shutdown();
		workers.awaitTermination(1, TimeUnit.DAYS);
		System.out.println("Finished in " + (System.currentTimeMillis() - time) + " ms");

		System.out.println("Called append() " + COUNT + " times");
		System.out.println("String length is " + message.length() + " characters");
	}

	private static void append() {
		message.append((int) (Math.random() * 10));
	}
}
