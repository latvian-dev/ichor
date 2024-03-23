package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;

import java.util.Objects;

public class ScriptValue {
	public final Object value;
	public final Prototype<?> prototype;

	public ScriptValue(Object value, Prototype<?> prototype) {
		this.value = value;
		this.prototype = prototype;
	}

	@Override
	public String toString() {
		return "[" + prototype.getPrototypeName() + " " + value + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(value);
	}
}
