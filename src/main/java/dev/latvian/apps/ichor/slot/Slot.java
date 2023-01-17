package dev.latvian.apps.ichor.slot;

public class Slot {
	public Object owner;
	public Object value;
	public boolean immutable;

	@Override
	public String toString() {
		return String.valueOf(value);
	}
}