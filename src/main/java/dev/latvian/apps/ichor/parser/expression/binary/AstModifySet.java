package dev.latvian.apps.ichor.parser.expression.binary;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.error.ScriptError;

public abstract class AstModifySet extends AstBinary {
	@Override
	public Object eval(Scope scope) {
		throw new ScriptError("Not supported yet!");
	}
}
