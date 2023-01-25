package dev.latvian.apps.ichor.error;

import dev.latvian.apps.ichor.prototype.Prototype;

public class IndexedMemberNotFoundError extends ScriptError {
	public final int index;
	public final Prototype prototype;

	public IndexedMemberNotFoundError(int index) {
		super("Member with index " + index + " not found");
		this.index = index;
		this.prototype = null;
	}

	public IndexedMemberNotFoundError(int index, Prototype prototype) {
		super("Member with index " + index + " of " + prototype + " not found");
		this.index = index;
		this.prototype = prototype;
	}
}
