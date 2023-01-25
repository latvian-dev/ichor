package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.js.NumberJS;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.Objects;

public interface WrappedObject extends PrototypeSupplier, WrappedObjectFactory {
	default Object unwrap() {
		return this;
	}

	@Override
	default WrappedObject create(Context cx, Scope scope) {
		return this;
	}

	@Nullable
	default Object get(Context cx, Scope scope, String name) {
		return getPrototype(cx, scope).get(cx, scope, unwrap(), name);
	}

	default boolean set(Context cx, Scope scope, String name, @Nullable Object value) {
		return getPrototype(cx, scope).set(cx, scope, unwrap(), name, value);
	}

	default boolean delete(Context cx, Scope scope, String name) {
		return getPrototype(cx, scope).delete(cx, scope, unwrap(), name);
	}

	@Nullable
	default Object get(Context cx, Scope scope, int index) {
		return getPrototype(cx, scope).get(cx, scope, unwrap(), index);
	}

	default boolean set(Context cx, Scope scope, int index, @Nullable Object value) {
		return getPrototype(cx, scope).set(cx, scope, unwrap(), index, value);
	}

	default boolean delete(Context cx, Scope scope, int index) {
		return getPrototype(cx, scope).delete(cx, scope, unwrap(), index);
	}

	default Collection<?> keys(Context cx, Scope scope) {
		return Collections.emptySet();
	}

	default Collection<?> values(Context cx, Scope scope) {
		return Collections.emptySet();
	}

	default Collection<?> entries(Context cx, Scope scope) {
		return Collections.emptySet();
	}

	default void asString(Context cx, Scope scope, StringBuilder builder, boolean escape) {
		builder.append(unwrap());
	}

	default Number asNumber(Context cx, Scope scope) {
		return NumberJS.ONE;
	}

	default boolean asBoolean(Context cx, Scope scope) {
		return true;
	}

	default boolean equals(Context cx, Scope scope, Object right, boolean shallow) {
		return shallow ? unwrap() == right : Objects.equals(unwrap(), right);
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	default int compareTo(Context cx, Scope scope, Object right) {
		if (unwrap() instanceof Comparable l && right instanceof Comparable r) {
			return l.compareTo(r);
		}

		return 0;
	}
}
