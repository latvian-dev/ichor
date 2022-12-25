package dev.latvian.apps.ichor;

public record ContextProperty<T>(String name, T defaultValue) {
	@Override
	public String toString() {
		return name;
	}

	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof ContextProperty<?> p && p.name.equals(name);
	}
}
