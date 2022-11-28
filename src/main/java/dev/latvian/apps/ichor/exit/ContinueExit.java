package dev.latvian.apps.ichor.exit;

public class ContinueExit extends ScopeExit {
	public ContinueExit(String label) {
		super("continue statement is not supported here", label);
	}
}