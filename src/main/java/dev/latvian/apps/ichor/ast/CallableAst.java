package dev.latvian.apps.ichor.ast;

import dev.latvian.apps.ichor.Evaluable;

public interface CallableAst {
	Evaluable createCall(Evaluable[] arguments, boolean isNew);
}
