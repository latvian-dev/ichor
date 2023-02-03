package dev.latvian.apps.ichor.js.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;

public class ObjectJS extends Prototype<ObjectJS> {
	public static final Callable ASSIGN = Functions.WIP;
	public static final Callable CREATE = Functions.WIP;
	public static final Callable DEFINE_PROPERTIES = Functions.WIP;
	public static final Callable DEFINE_PROPERTY = Functions.WIP;
	public static final Callable ENTRIES = Functions.WIP;
	public static final Callable KEYS = Functions.WIP;
	public static final Callable VALUES = Functions.WIP;
	public static final Callable GET_PROTOTYPE_OF = Functions.WIP;

	public ObjectJS(Context cx) {
		super(cx, "Object", ObjectJS.class);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return new LinkedHashMap<>();
	}

	@Override
	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		return switch (name) {
			case "assign" -> ASSIGN;
			case "create" -> CREATE;
			case "defineProperties" -> DEFINE_PROPERTIES;
			case "defineProperty" -> DEFINE_PROPERTY;
			case "entries" -> ENTRIES;
			case "keys" -> KEYS;
			case "values" -> VALUES;
			case "getPrototypeOf" -> GET_PROTOTYPE_OF;
			default -> super.getStatic(cx, scope, name);
		};
	}
}
