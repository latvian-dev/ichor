package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("Array")
			.constructor((cx, args, hasNew) -> args.length == 0 ? new ArrayList<>() : Arrays.asList(args));

}
