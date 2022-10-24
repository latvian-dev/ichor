package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeBuilder;
import dev.latvian.apps.ichor.util.NotFoundObject;
import org.jetbrains.annotations.Nullable;

public interface Special {
	NotFoundObject NOT_FOUND = new NotFoundObject(); // Internal use only

	Prototype NULL = PrototypeBuilder.create("null");
	Prototype UNDEFINED = PrototypeBuilder.create("undefined");

	static boolean isInvalid(@Nullable Object o) {
		return o == null || o == NOT_FOUND || o == NULL || o == UNDEFINED;
	}
}
