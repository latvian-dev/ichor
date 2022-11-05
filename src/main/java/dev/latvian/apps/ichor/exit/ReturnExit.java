package dev.latvian.apps.ichor.exit;

public class ReturnExit extends ScopeExit {
	public ReturnExit(Object v) {
		super("return statement is not supported here", v);
	}
}