package dev.latvian.apps.ichor.ast.expression;

import dev.latvian.apps.ichor.Special;

public class AstParam {
	public static final AstParam[] EMPTY_PARAM_ARRAY = new AstParam[0];
	private static final String DEFAULT_TYPE = "any";

	public final String name;
	public final String type;
	public Object defaultValue;

	public AstParam(String name) {
		this.name = name;
		this.type = DEFAULT_TYPE;
		this.defaultValue = Special.UNDEFINED;
	}
}
