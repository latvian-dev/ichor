package dev.latvian.apps.ichor.slot;

public class Slot {
	public final String name;
	public Object value;
	public boolean immutable;

	public Slot(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		return "[Slot " + name + " = " + value + "]";
	}
}