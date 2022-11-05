package dev.latvian.apps.ichor.ast;

import dev.latvian.apps.ichor.Evaluable;

public interface CallableAst {
	Evaluable createCall(Object[] arguments, boolean isNew);
}
