import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class ConcurrencyPrimitive {
	private static int counter = 0;

	public static void main(String[] args) throws InterruptedException {
		final int COUNT = 1000;

		ExecutorService workers = Executors.newCachedThreadPool();
		for(int count = 0; count != COUNT; ++count) {
			workers.submit(ConcurrencyPrimitive::increment);
		}
		workers.shutdown();
		workers.awaitTermination(1, TimeUnit.DAYS);

		System.out.println("Called increment() " + COUNT + " times");
		System.out.println("Counter has reached " + counter);
	}

	private static void increment() {
		++counter;
	}
}
