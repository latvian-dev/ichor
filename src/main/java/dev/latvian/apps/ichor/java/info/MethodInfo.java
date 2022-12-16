package dev.latvian.apps.ichor.java.info;

import dev.latvian.apps.ichor.Hidden;
import dev.latvian.apps.ichor.Remap;
import dev.latvian.apps.ichor.util.Signature;

import java.lang.reflect.Method;

public class MethodInfo {
	public static final MethodInfo[] EMPTY = new MethodInfo[0];

	public final ClassInfo parent;
	public final Method wrapped;
	public final Signature signature;
	public final boolean isHidden;
	public final String remapOverride;

	public MethodInfo(ClassInfo parent, Method wrapped) {
		this.parent = parent;
		this.wrapped = wrapped;
		this.signature = Signature.of(wrapped);
		this.isHidden = wrapped.isAnnotationPresent(Hidden.class);
		this.remapOverride = wrapped.isAnnotationPresent(Remap.class) ? wrapped.getAnnotation(Remap.class).value() : null;
	}
}
