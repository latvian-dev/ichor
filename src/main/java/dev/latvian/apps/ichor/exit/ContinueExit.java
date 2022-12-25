package dev.latvian.apps.ichor.exit;

import dev.latvian.apps.ichor.Interpretable;

public class ContinueExit extends LabelExit {
	public ContinueExit(Interpretable stop) {
		super("continue statement is not supported here", stop);
	}
}