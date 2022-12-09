package dev.latvian.apps.ichor.java.info;

import dev.latvian.apps.ichor.api.Remap;

import java.lang.reflect.Field;

public class FieldInfo {
	public static final FieldInfo[] EMPTY = new FieldInfo[0];

	public final ClassInfo parent;
	public final Field wrapped;
	public final String remapOverride;

	public FieldInfo(ClassInfo parent, Field wrapped) {
		this.parent = parent;
		this.wrapped = wrapped;
		this.remapOverride = wrapped.isAnnotationPresent(Remap.class) ? wrapped.getAnnotation(Remap.class).value() : null;
	}
}
