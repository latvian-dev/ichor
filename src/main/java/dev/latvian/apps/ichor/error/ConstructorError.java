package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.prototype.Prototype;
import org.jetbrains.annotations.Nullable;

public class ConstructorError extends ScriptError {
	public final Prototype prototype;

	public ConstructorError(@Nullable Prototype prototype) {
		super(prototype == null ? "Cannot construct function" : ("Cannot construct " + prototype));
		this.prototype = prototype;
	}
}
