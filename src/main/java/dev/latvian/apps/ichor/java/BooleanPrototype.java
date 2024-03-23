package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.IchorUtils;

public class BooleanPrototype extends Prototype<Boolean> {
	public BooleanPrototype(Scope cx) {
		super(cx, Boolean.class);
	}

	@Override
	public Object call(Scope scope, Object[] args, boolean hasNew) {
		return args.length == 0 ? Boolean.FALSE : scope.asBoolean(args[0]);
	}

	@Override
	public Number asNumber(Scope scope, Boolean self) {
		return self ? IchorUtils.ONE : IchorUtils.ZERO;
	}

	@Override
	public Boolean asBoolean(Scope scope, Boolean self) {
		return self;
	}
}
