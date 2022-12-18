package dev.latvian.apps.ichor.exit;

public class BreakExit extends LabelExit {
	public static final BreakExit DEFAULT_BREAK = new BreakExit("");

	public BreakExit(String label) {
		super("break statement is not supported here", label);
	}
}