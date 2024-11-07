class Pi {
	public static void main(String[] args) {
		int darts = Integer.parseInt(System.console().readLine("Number of darts to throw to approximate π? "));

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
		double pi = sectorArea * 4;

		int guess = Integer.parseInt(System.console().readLine("Guess how many of the leading digits are right! "));
		int actual = countMatchingDigits(pi, Math.PI);
		if(guess == actual) {
			System.out.println("You guessed right!");
		} else {
			System.out.println("Close, but we got the first " + actual + " digits right!");
		}
		System.out.println("We approximated π as " + pi);
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
