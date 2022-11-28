package dev.latvian.apps.ichor.exit;

public class BreakExit extends ScopeExit {
	public BreakExit(String label) {
		super("break statement is not supported here", label);
	}
}