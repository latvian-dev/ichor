package dev.latvian.apps.ichor.test.js;

import dev.latvian.apps.ichor.java.info.ClassInfo;
import dev.latvian.apps.ichor.prototype.PrototypeStaticFunction;
import dev.latvian.apps.ichor.token.Token;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.io.PrintWriter;
import java.io.Serializable;
import java.util.function.Function;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class ClassInfoTests {
	private static void smiInterfaceTest(Class<?> iface, boolean is) {
		System.out.println("--- " + iface.getName() + " SMI test");
		var smi = ClassInfo.of(iface).getSingleMethodInterface();
		System.out.println("SMI: " + smi);
		Assertions.assertEquals(smi != null, is);
	}

	@Test
	public void smiSerializable() {
		smiInterfaceTest(Serializable.class, false);
	}

	@Test
	public void smiRunnable() {
		smiInterfaceTest(Runnable.class, true);
	}

	@Test
	public void smiFunction() {
		smiInterfaceTest(Function.class, true);
	}

	@Test
	public void smiPrototypeStaticFunction() {
		smiInterfaceTest(PrototypeStaticFunction.class, true);
	}

	@Test
	public void smiToken() {
		smiInterfaceTest(Token.class, false);
	}

	@Test
	public void smiPrintWrapper() {
		smiInterfaceTest(PrintWriter.class, false);
	}
}
