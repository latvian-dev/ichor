package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;

public class ContextJS extends Context {
	public ContextJS() {
		safePrototypes.add(stringPrototype = StringJS.PROTOTYPE);
		safePrototypes.add(numberPrototype = NumberJS.PROTOTYPE);
		safePrototypes.add(booleanPrototype = BooleanJS.PROTOTYPE);
		safePrototypes.add(listPrototype = ArrayJS.PROTOTYPE);
		safePrototypes.add(mapPrototype = ObjectJS.PROTOTYPE);
	}
}
