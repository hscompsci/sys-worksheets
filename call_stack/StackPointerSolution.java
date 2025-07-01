class StackPointerSolution {
	private static long main;

	public static void main(String[] args) {
		main = StackPointer.getStackPointer();
		System.out.println(main + " main()");
		call();
		allocateInts();
		allocateStrings();
		recurse(100);
		allocateAndRecurse(1000);
	}

	public static void call() {
		System.out.println(StackPointer.getStackPointer() - main + " call()");
	}

	public static void allocateInts() {
		int i0 = 0;
		int i1 = 1;
		System.out.println(StackPointer.getStackPointer() - main + " allocateInts()");
	}

	public static void allocateStrings() {
		String s0 = "This string is longer than 8 bytes!";
		String s1 = "As is this one, as a matter of fact!";
		System.out.println(StackPointer.getStackPointer() - main + " allocateStrings()");
	}

	public static void recurse(int times) {
		System.out.println(StackPointer.getStackPointer() - main + " recurse(" + times + ")");
		if(times > 0) {
			recurse(times - 1);
		}
	}

	public static void allocateAndRecurse(int times) {
		int[] big = new int[10000000];
		System.out.println(StackPointer.getStackPointer() - main + " allocateAndRecurse(" + times + ")");
		if(times > 0) {
			allocateAndRecurse(times - 1);
		}
	}
}
