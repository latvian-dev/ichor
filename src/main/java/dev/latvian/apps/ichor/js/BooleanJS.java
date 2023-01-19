package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class BooleanJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Boolean") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args) {
			return args.length == 0 ? Boolean.FALSE : cx.asBoolean(scope, args[0]);
		}

		@Override
		public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
			builder.append(self);
		}

		@Override
		public Number asNumber(Context cx, Scope scope, Object self) {
			return (Boolean) self ? NumberJS.ONE : NumberJS.ZERO;
		}

		@Override
		public boolean asBoolean(Context cx, Scope scope, Object self) {
			return (Boolean) self;
		}
	};
}
