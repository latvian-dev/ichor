package dev.latvian.apps.ichor;

public interface TokenSource {
	record Named(String name) implements TokenSource {
		@Override
		public String toString() {
			return name;
		}
	}
}
