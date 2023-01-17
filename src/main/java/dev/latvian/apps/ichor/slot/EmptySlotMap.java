package dev.latvian.apps.ichor.slot;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class EmptySlotMap implements SlotMap {
	public static final EmptySlotMap INSTANCE = new EmptySlotMap();

	@Override
	@Nullable
	public Slot getSlot(String name) {
		return null;
	}

	@Override
	public void setSlot(String name, Slot slot) {
	}

	@Override
	public void removeSlot(String name) {
	}

	@Override
	public Set<String> getSlotNames() {
		return Set.of();
	}

	@Override
	public SlotMap upgradeSlotMap() {
		return new SlotHashMap();
	}
}
