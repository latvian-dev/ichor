package dev.latvian.apps.ichor.exit;

import dev.latvian.apps.ichor.Special;

public class ReturnExit extends ScopeExit {
	public static final ReturnExit DEFAULT_RETURN = new ReturnExit(Special.UNDEFINED);

	public ReturnExit(Object value) {
		super("return statement is not supported here", value);
	}
}