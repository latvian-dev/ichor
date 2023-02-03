package dev.latvian.apps.ichor.error;

public class ArgumentCountMismatchError extends ScriptError {
	public final int requiredCount;
	public final int actualCount;

	public ArgumentCountMismatchError(int expectedCount, int actualCount) {
		super("Invalid number of arguments: Expected " + expectedCount + ", got " + actualCount);
		this.requiredCount = expectedCount;
		this.actualCount = actualCount;
	}
}