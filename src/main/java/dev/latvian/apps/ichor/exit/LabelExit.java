package dev.latvian.apps.ichor.exit;

public class LabelExit extends ScopeExit {
	public final String label;

	public LabelExit(String msg, String label) {
		super(msg, label);
		this.label = label;
	}
}