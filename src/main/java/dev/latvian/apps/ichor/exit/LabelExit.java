package dev.latvian.apps.ichor.exit;

import dev.latvian.apps.ichor.Interpretable;

public class LabelExit extends ScopeExit {
	public final Interpretable stop;

	public LabelExit(String msg, Interpretable stop) {
		super(msg, stop);
		this.stop = stop;
	}
}