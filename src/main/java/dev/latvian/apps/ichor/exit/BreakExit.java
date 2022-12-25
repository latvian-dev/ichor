package dev.latvian.apps.ichor.exit;

import dev.latvian.apps.ichor.Interpretable;

public class BreakExit extends LabelExit {
	public BreakExit(Interpretable stop) {
		super("break statement is not supported here", stop);
	}
}