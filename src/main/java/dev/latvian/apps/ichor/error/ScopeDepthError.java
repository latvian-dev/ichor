package dev.latvian.apps.ichor.error;

public class ScopeDepthError extends ScriptError {
	public final int maxDepth;

	public ScopeDepthError(int maxDepth) {
		super("Scope depth is > " + maxDepth);
		this.maxDepth = maxDepth;
	}
}
