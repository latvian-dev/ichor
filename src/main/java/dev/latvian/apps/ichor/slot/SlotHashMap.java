package dev.latvian.apps.ichor.slot;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Set;

public class SlotHashMap extends HashMap<String, Slot> implements SlotMap {
	@Override
	@Nullable
	public Slot getSlot(String name) {
		return get(name);
	}

	@Override
	public void setSlot(String name, Slot slot) {
		put(name, slot);
	}

	@Override
	public void removeSlot(String name) {
		remove(name);
	}

	@Override
	public Set<String> getSlotNames() {
		return keySet();
	}
}
