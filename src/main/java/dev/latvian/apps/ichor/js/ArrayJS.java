package dev.latvian.apps.ichor.js;

import dev.latvian.apps.ichor.Context;
import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.java.ListValueHandler;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class ArrayJS {
	public static final Prototype PROTOTYPE = new PrototypeBuilder("Array") {
		@Override
		public Object call(Context cx, Scope scope, Object[] args, boolean hasNew) {
			return args.length == 0 ? new ArrayList<>() : Arrays.asList(args);
		}

		@Override
		public void asString(Context cx, Scope scope, Object self, StringBuilder builder, boolean escape) {
			builder.append('[');

			boolean first = true;

			for (Object o : collection(self)) {
				if (first) {
					first = false;
				} else {
					builder.append(',');
					builder.append(' ');
				}

				cx.asString(scope, o, builder, true);
			}

			builder.append(']');
		}
	}
			.property("length", ArrayJS::length)
			.customMembers(ListValueHandler.INSTANCE);

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static Collection<Object> collection(Object self) {
		return (Collection) self;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	public static List<Object> list(Object self) {
		return (List) self;
	}

	private static Object length(Context cx, Scope scope, Object self) {
		return collection(self).size();
	}
}
