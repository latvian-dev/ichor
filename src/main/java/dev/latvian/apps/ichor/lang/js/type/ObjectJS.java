package dev.latvian.apps.ichor.lang.js.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ObjectJS extends Prototype<ObjectJS> {
	public static final Callable ASSIGN = Functions.of2((cx, scope, arg1, arg2) -> {
		var o = cx.as(scope, arg1, Map.class);
		//noinspection unchecked
		o.putAll(cx.as(scope, arg2, Map.class));
		return o;
	});

	public static final Callable CREATE = Functions.WIP;
	public static final Callable DEFINE_PROPERTIES = Functions.WIP;
	public static final Callable DEFINE_PROPERTY = Functions.WIP;

	public static final Callable ENTRIES = Functions.of1((cx, scope, arg) -> {
		var p = cx.getPrototype(scope, arg);
		var c = p.entries(cx, scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable KEYS = Functions.of1((cx, scope, arg) -> {
		var p = cx.getPrototype(scope, arg);
		var c = p.keys(cx, scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable VALUES = Functions.of1((cx, scope, arg) -> {
		var p = cx.getPrototype(scope, arg);
		var c = p.values(cx, scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable GET_PROTOTYPE_OF = Functions.of1(Context::getPrototype);

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
