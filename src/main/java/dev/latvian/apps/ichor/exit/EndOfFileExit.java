package dev.latvian.apps.ichor.exit;

public class EndOfFileExit extends RuntimeException {
	public EndOfFileExit() {
		super("EOF", null, false, false);
	}
}