package dev.latvian.apps.ichor.test;

public class ReflectionExample {
	public int publicField = 30;
	private final float privateField = 40.5F;
	public transient String publicTransientField = "Hi";

	public void sout(String text) {
		System.out.println(text);
	}

	public void serr(String text) {
		System.err.println(text);
	}

	public void soutNum(short number) {
		System.out.println(number);
	}
}
