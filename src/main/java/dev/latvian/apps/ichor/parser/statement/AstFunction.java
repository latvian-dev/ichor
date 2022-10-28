package dev.latvian.apps.ichor.parser.statement;

public class AstFunction extends AstStatement {
	public final String name;
	public final String[] params;
	public final AstStatement body;

	public AstFunction(String name, String[] params, AstStatement body) {
		this.name = name;
		this.params = params;
		this.body = body;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append("function ");
		builder.append(name);
		builder.append('(');

		for (int i = 0; i < params.length; i++) {
			if (i > 0) {
				builder.append(", ");
			}

			builder.append(params[i]);
		}

		builder.append(") ");
		body.append(builder);
	}
}
