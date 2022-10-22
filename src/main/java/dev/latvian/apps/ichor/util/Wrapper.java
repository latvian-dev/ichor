package dev.latvian.apps.ichor.util;

public interface Wrapper {
	Object unwrap();

	static Object unwrapped(Object o) {
		Object u = o;

		while (u instanceof Wrapper w) {
			u = w.unwrap();
		}

		return u;
	}
}
