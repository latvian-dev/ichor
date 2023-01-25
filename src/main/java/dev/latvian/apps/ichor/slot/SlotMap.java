package dev.latvian.apps.ichor.slot;

import org.jetbrains.annotations.Nullable;

import java.util.Set;

public interface SlotMap {
	@Nullable
	Slot getSlot(String name);

	void setSlot(Slot slot);

	void removeSlot(String name);

	Set<String> getSlotNames();

	default SlotMap upgradeSlotMap() {
		return this;
	}
}
