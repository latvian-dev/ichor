package dev.latvian.apps.ichor.exit;

public enum ExitType {
	RETURN("return"),
	BREAK("break"),
	CONTINUE("continue");

	public final String name;

	ExitType(String n) {
		name = n;
	}
}
