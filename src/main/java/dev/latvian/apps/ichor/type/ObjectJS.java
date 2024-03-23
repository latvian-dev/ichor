package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashMap;
import java.util.List;

public class ObjectJS extends Prototype<ObjectJS> {
	public static final Callable ASSIGN = Functions.of2((scope, arg1, arg2) -> {
		var o = scope.asMap(arg1);
		//noinspection unchecked
		o.putAll(scope.asMap(arg2));
		return o;
	});

	public static final Callable CREATE = Functions.WIP;
	public static final Callable DEFINE_PROPERTIES = Functions.WIP;
	public static final Callable DEFINE_PROPERTY = Functions.WIP;

	public static final Callable ENTRIES = Functions.of1((scope, arg) -> {
		var p = scope.getPrototype(arg);
		var c = p.entries(scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable KEYS = Functions.of1((scope, arg) -> {
		var p = scope.getPrototype(arg);
		var c = p.keys(scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable VALUES = Functions.of1((scope, arg) -> {
		var p = scope.getPrototype(arg);
		var c = p.values(scope, p.cast(arg));
		return c == null ? List.of() : c;
	});

	public static final Callable GET_PROTOTYPE_OF = Functions.of1(Scope::getPrototype);

	public ObjectJS(Scope cx) {
		super(cx, "Object", ObjectJS.class);
	}

	@Override
	public Object call(Scope scope, Object[] args, boolean hasNew) {
		return new LinkedHashMap<>();
	}

	@Override
	@Nullable
	public Object getStatic(Scope scope, String name) {
		return switch (name) {
			case "assign" -> ASSIGN;
			case "create" -> CREATE;
			case "defineProperties" -> DEFINE_PROPERTIES;
			case "defineProperty" -> DEFINE_PROPERTY;
			case "entries" -> ENTRIES;
			case "keys" -> KEYS;
			case "values" -> VALUES;
			case "getPrototypeOf" -> GET_PROTOTYPE_OF;
			default -> super.getStatic(scope, name);
		};
	}
}
