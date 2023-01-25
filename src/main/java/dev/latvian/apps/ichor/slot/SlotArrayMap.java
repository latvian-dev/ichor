package dev.latvian.apps.ichor.slot;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class SlotArrayMap extends ArrayList<Slot> implements SlotMap {
	public SlotArrayMap() {
		super(4);
	}

	@Override
	@Nullable
	public Slot getSlot(String name) {
		for (var slot : this) {
			if (slot.name.equals(name)) {
				return slot;
			}
		}

		return null;
	}

	@Override
	public void setSlot(Slot slot) {
		for (int i = 0; i < size(); i++) {
			if (get(i).name.equals(slot.name)) {
				set(i, slot);
				return;
			}
		}

		add(slot);
	}

	@Override
	public void removeSlot(String name) {
		var itr = iterator();

		while (itr.hasNext()) {
			if (itr.next().name.equals(name)) {
				itr.remove();
				return;
			}
		}
	}

	@Override
	public Set<String> getSlotNames() {
		var set = new HashSet<String>(size());

		for (var slot : this) {
			set.add(slot.name);
		}

		return set;
	}

	@Override
	public SlotMap upgradeSlotMap() {
		if (size() == 16) {
			var map = new SlotHashMap();

			for (var slot : this) {
				map.setSlot(slot);
			}

			return map;
		}

		return this;
	}
}
