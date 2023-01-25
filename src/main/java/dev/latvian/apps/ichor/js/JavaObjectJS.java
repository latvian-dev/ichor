package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.java.JavaClassPrototype;
import dev.latvian.apps.ichor.prototype.Prototype;

public class JavaObjectJS<T> implements WrappedObject {
	public final T self;
	public final JavaClassPrototype prototype;

	public JavaObjectJS(T self, JavaClassPrototype prototype) {
		this.self = self;
		this.prototype = prototype;
	}

	@Override
	public String toString() {
		return String.format("[JavaObjectJS %s#%08x]", prototype.getPrototypeName(), System.identityHashCode(self));
	}

	@Override
	public T unwrap() {
		return self;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return prototype;
	}
}
