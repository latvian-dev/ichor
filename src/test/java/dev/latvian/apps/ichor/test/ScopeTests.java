package dev.latvian.apps.ichor.test;

import dev.latvian.apps.ichor.error.IchorError;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.SimpleScope;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class ScopeTests {
	@Test
	public void redeclaration() {
		Assertions.assertThrows(IchorError.class, () -> {
			var scope = new SimpleScope();

			scope.setMember("test", 5, AssignType.IMMUTABLE);

			var child = new SimpleScope();
			child.setParentScope(scope);

			child.setMember("test", 10, AssignType.NONE);
		});
	}
}
