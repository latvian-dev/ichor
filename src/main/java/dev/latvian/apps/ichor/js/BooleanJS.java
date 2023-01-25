package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.WrappedObject;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class BooleanJS implements WrappedObject {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Boolean") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? Boolean.FALSE : cx.asBoolean(scope, args[0]);
		}
	};

	public static final BooleanJS TRUE = new BooleanJS(true);
	public static final BooleanJS FALSE = new BooleanJS(false);

	public final boolean self;
	public final Boolean selfObj;

	private BooleanJS(boolean self) {
		this.self = self;
		this.selfObj = self;
	}

	@Override
	public Object unwrap() {
		return selfObj;
	}

	@Override
	public Prototype getPrototype(Context cx, Scope scope) {
		return PROTOTYPE;
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
}
