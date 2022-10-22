package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;

public interface Special {
	Prototype NOT_FOUND = new Prototype("not_found");
	Prototype NULL = new Prototype("null");
	Prototype UNDEFINED = new Prototype("undefined");
}
