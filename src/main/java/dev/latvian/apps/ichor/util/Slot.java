package dev.latvian.apps.ichor.util;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.prototype.Prototype;

public class Slot {
	public Object value;
	public boolean immutable;
	public Prototype prototype;

	public Prototype getPrototype(Context cx) {
		if (prototype == null) {
			prototype = cx.getPrototype(value);
		}

		return prototype;
	}
}
