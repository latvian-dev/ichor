package dev.latvian.apps.ichor.parser.expression;

import dev.latvian.apps.ichor.Evaluable;

public abstract class AstGetFrom extends AstGetBase {
	public final Evaluable from;

	public AstGetFrom(Evaluable from) {
		this.from = from;
	}
}
