package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.prototype.Prototype;

public class JavaObjectJS implements Prototype {
	public final Object javaObject;
	private Prototype prototype;

	public JavaObjectJS(Object o) {
		javaObject = o;
	}

	@Override
	public String toString() {
		return javaObject.toString();
	}

	@Override
	public String getPrototypeName() {
		return javaObject.getClass().getSimpleName();
	}

	// It's ok to cache context-specific value, because there won't be a static instance of JavaObjectJS
	@Override
	public Prototype getPrototype(Context cx) {
		if (prototype == null) {
			prototype = cx.getClassPrototype(javaObject.getClass());
		}

		return prototype;
	}
}
