package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeWrappedObject;
import org.jetbrains.annotations.Nullable;

public class BooleanJS implements PrototypeWrappedObject {
	public static final BooleanJS TRUE = new BooleanJS(true, Boolean.TRUE);
	public static final BooleanJS FALSE = new BooleanJS(false, Boolean.FALSE);

	public static Prototype createDefaultPrototype() {
		return new Prototype("Boolean") {
			@Override
			public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
				return args.length == 0 ? Boolean.FALSE : cx.asBoolean(scope, args[0]);
			}
		};
	}

	public final boolean self;
	public final Boolean selfObj;

	private BooleanJS(boolean self, Boolean selfObj) {
		this.self = self;
		this.selfObj = selfObj;
	}

	@Override
	public Object unwrap() {
		return selfObj;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return ((ContextJS) cx).booleanPrototype;
	}

	@Override
	public String toString() {
		return self ? "[Boolean true]" : "[Boolean false]";
	}

	@Override
	public void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append(self);
	}

	@Override
	public Number asNumber(Context cx, Scope scope) {
		return self ? NumberJS.ONE : NumberJS.ZERO;
	}

	@Override
	public boolean asBoolean(Context cx, Scope scope) {
		return self;
	}

	@Override
	@Nullable
	public Object get(Context cx, Scope scope, String name) {
		return name.equals("class") ? Boolean.class : PrototypeWrappedObject.super.get(cx, scope, name);
	}
}
