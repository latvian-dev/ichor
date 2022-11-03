package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;

public class ContextJS extends Context {
	public ContextJS() {
		stringPrototype = StringJS.PROTOTYPE;
		numberPrototype = NumberJS.PROTOTYPE;
		booleanPrototype = BooleanJS.PROTOTYPE;
		listPrototype = ArrayJS.PROTOTYPE;
		mapPrototype = ObjectJS.PROTOTYPE;
	}
}
