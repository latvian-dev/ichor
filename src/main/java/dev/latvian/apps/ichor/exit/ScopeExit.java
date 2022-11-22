package dev.latvian.apps.ichor.exit;

public class ScopeExit extends RuntimeException {
	public final Object value;

	public ScopeExit(String msg, Object v) {
		super(msg, null, false, false);
		value = v;
	}
}
