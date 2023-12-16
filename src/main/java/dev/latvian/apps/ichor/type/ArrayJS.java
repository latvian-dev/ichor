package dev.latvian.apps.ichor.type;

import dev.latvian.apps.ichor.Callable;
import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;

public class ArrayJS extends Prototype<ArrayJS> {
	public static final Callable FROM = Functions.WIP;
	public static final Callable IS_ARRAY = Functions.WIP;
	public static final Callable OF = Functions.WIP;

	public ArrayJS(Context cx) {
		super(cx, "Array", ArrayJS.class);
	}

	@Override
	public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
		return args.length == 0 ? new ArrayList<>() : new ArrayList<>(Arrays.asList(args));
	}

	@Override
	@Nullable
	public Object getStatic(Context cx, Scope scope, String name) {
		return switch (name) {
			case "from" -> FROM;
			case "isArray" -> IS_ARRAY;
			case "of" -> OF;
			default -> super.getStatic(cx, scope, name);
		};
	}
}
