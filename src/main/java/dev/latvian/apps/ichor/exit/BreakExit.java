package dev.latvian.apps.ichor.exit;

public class BreakExit extends ScopeExit {
	public BreakExit() {
		super("break statement is not supported here", null);
	}
}