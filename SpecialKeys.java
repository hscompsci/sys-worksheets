class SpecialKeys {
	public static void main(String[] args) {
		String input = System.console().readLine("Enter something (or hit Ctrl+D, Ctrl+C, or Ctrl+Z): ");
		System.out.println();
		System.out.println("You entered '" + input + "'");
	}
}
