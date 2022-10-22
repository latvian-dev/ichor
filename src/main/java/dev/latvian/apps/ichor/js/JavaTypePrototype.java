package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.prototype.Prototype;

public class JavaTypePrototype extends Prototype {
	public final Class<?> type;

	public JavaTypePrototype(Class<?> t) {
		super(t.getName());
		type = t;
	}
}
