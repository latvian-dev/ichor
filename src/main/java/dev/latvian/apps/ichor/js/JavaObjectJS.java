package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeWrappedObject;
import org.jetbrains.annotations.Nullable;

public class JavaObjectJS<T> implements PrototypeWrappedObject {
	public final T self;
	public final Prototype prototype;

	public JavaObjectJS(T self, Prototype prototype) {
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

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return name.equals("class") ? self.getClass() : PrototypeWrappedObject.super.get(cx, scope, name);
	}
}
