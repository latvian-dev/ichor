package dev.latvian.apps.ichor.slot;

import dev.latvian.apps.ichor.Special;

public class Slot {
	public static final byte DEFAULT = 0;
	public static final byte IMMUTABLE = 1;
	public static final byte ROOT = 2;

	public final String name;
	public Object value;
	public byte flags;

	public Slot(String name) {
		this.name = name;
		this.value = Special.UNDEFINED;
		this.flags = 0;
	}

	@Override
	public String toString() {
		return "[Slot " + name + " = " + value + "]";
	}

	public boolean isImmutable() {
		return (flags & IMMUTABLE) != 0;
	}

	public boolean isRoot() {
		return (flags & ROOT) != 0;
	}
}