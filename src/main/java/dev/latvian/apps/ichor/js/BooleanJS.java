package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class BooleanJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("Boolean")
			.constructor((cx, args, hasNew) -> args.length == 0 ? Boolean.FALSE : cx.asBoolean(args[0]))
			.asString((cx, self) -> self.toString())
			.asNumber((cx, self) -> (Boolean) self ? NumberJS.ONE : NumberJS.ZERO)
			.asBoolean((cx, self) -> (Boolean) self);

}
