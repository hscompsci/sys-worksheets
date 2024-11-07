import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

class PiConcurrent {
	private static int darts;
	private static double pi;

	public static void main(String[] args) throws InterruptedException {
		darts = Integer.parseInt(System.console().readLine("Number of darts to throw to approximate π? "));

		// Calculate pi concurrently...
		ExecutorService worker = Executors.newCachedThreadPool();
		worker.submit(PiConcurrent::calculatePi);

		// ...while we collect input!
		int guess = Integer.parseInt(System.console().readLine("Guess how many of the leading digits are right! "));

		// Wait for the calculation to finish...
		worker.shutdown();
		worker.awaitTermination(1, TimeUnit.DAYS);

		// ...before we do anything with the result!
		int actual = countMatchingDigits(pi, Math.PI);
		if(guess == actual) {
			System.out.println("You guessed right!");
		} else {
			System.out.println("Close, but we got the first " + actual + " digits right!");
		}
		System.out.println("We approximated π as " + pi);
	}

	private static void calculatePi() {
		// Throw that many darts at a 1x1 "board" containing the first quadrant of the unit circle.
		int hits = 0;
		for(int count = 0; count != darts; ++count) {
			double x = Math.random();
			double y = Math.random();
			double radius = Math.hypot(x, y);
			if(radius < 1) {
				++hits;
			}
		}

		// We know the square board's area to be 1, so we can compute the sector's area as a ratio.
		double sectorArea = (double) hits / darts;
		if(darts == 0) {
			sectorArea = 0.0;
		}

		// The unit circle is 4x as big as the sector. And the radius is 1, so the area is pi!
		pi = sectorArea * 4;
	}

	private static int countMatchingDigits(double one, double another) {
		String left = String.valueOf(one);
		String right = String.valueOf(another);
		int length = Math.min(left.length(), right.length());
		int index;
		for(index = 0; index != length && left.charAt(index) == right.charAt(index); ++index) {}
		if(index > 1) {
			// Correct for the decimal point.
			--index;
		}
		return index;
	}
}
