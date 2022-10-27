package dev.latvian.apps.ichor.parser;

public abstract class Ast {
	public int line;
	public int pos;

	@Override
	public String toString() {
		var sb = new StringBuilder();
		toString(sb);
		return sb.toString();
	}

	public abstract void toString(StringBuilder builder);
}
