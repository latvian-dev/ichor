package dev.latvian.apps.ichor.exit;

public class ContinueExit extends LabelExit {
	public static final ContinueExit DEFAULT_CONTINUE = new ContinueExit("");

	public ContinueExit(String label) {
		super("continue statement is not supported here", label);
	}
}