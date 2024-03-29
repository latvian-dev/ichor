package dev.latvian.apps.ichor.java;

import dev.latvian.apps.ichor.Scope;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.util.Functions;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.AnnotatedElement;

@SuppressWarnings("unchecked")
public class AnnotatedElementPrototype extends Prototype<AnnotatedElement> {
	private static final Functions.Bound<AnnotatedElement> GET_ANNOTATION = (scope, cl, args) -> cl.getAnnotation(scope.asClass(args[0]));
	private static final Functions.Bound<AnnotatedElement> GET_DECLARED_ANNOTATION = (scope, cl, args) -> cl.getDeclaredAnnotation(scope.asClass(args[0]));
	private static final Functions.Bound<AnnotatedElement> GET_ANNOTATIONS_BY_TYPE = (scope, cl, args) -> cl.getAnnotationsByType(scope.asClass(args[0]));
	private static final Functions.Bound<AnnotatedElement> GET_DECLARED_ANNOTATIONS_BY_TYPE = (scope, cl, args) -> cl.getDeclaredAnnotationsByType(scope.asClass(args[0]));

	public AnnotatedElementPrototype(Scope cx) {
		super(cx, AnnotatedElement.class);
	}

	@Override
	@Nullable
	public Object getLocal(Scope scope, AnnotatedElement self, String name) {
		return switch (name) {
			case "annotations" -> self.getAnnotations();
			case "declaredAnnotations" -> self.getDeclaredAnnotations();
			case "getAnnotation" -> GET_ANNOTATION.with(self);
			case "getDeclaredAnnotation" -> GET_DECLARED_ANNOTATION.with(self);
			case "getAnnotationsByType" -> GET_ANNOTATIONS_BY_TYPE.with(self);
			case "getDeclaredAnnotationsByType" -> GET_DECLARED_ANNOTATIONS_BY_TYPE.with(self);
			default -> super.getLocal(scope, self, name);
		};
	}
}
