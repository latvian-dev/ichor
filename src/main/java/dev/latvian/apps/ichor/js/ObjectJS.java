package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.java.MapValueHandler;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.LinkedHashMap;

public class ObjectJS {
	public static final Prototype PROTOTYPE = PrototypeBuilder.create("Object")
			.constructor((cx, args, hasNew) -> new LinkedHashMap<>())
			.namedValueHandler(MapValueHandler.INSTANCE);

}
