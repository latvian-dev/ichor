package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.prototype.Prototype;

public class IndexedMemberNotFoundError extends ScriptError {
	public final int index;
	public final Prototype<?> prototype;
	public final Object self;

	public IndexedMemberNotFoundError(int index, Prototype<?> prototype, Object self) {
		super("Member with index " + index + " of " + self + " of type '" + prototype + "' not found");
		this.index = index;
		this.prototype = prototype;
		this.self = self;
	}
}
