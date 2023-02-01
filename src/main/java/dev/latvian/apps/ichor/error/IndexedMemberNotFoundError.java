package dev.latvian.apps.ichor.error;

public class IndexedMemberNotFoundError extends ScriptError {
	public final int index;
	public final Object self;

	public IndexedMemberNotFoundError(int index, Object self) {
		super("Member with index " + index + " of " + self + " not found");
		this.index = index;
		this.self = self;
	}
}
