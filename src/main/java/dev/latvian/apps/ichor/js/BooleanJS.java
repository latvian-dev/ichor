package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

public class BooleanJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Boolean")
			.constructor((scope, args, hasNew) -> args.length == 0 ? Boolean.FALSE : scope.getContext().asBoolean(scope, args[0]))
			.asNumber((scope, self) -> (Boolean) self ? NumberJS.ONE : NumberJS.ZERO)
			.asBoolean((scope, self) -> (Boolean) self);
}
