package dev.latvian.apps.ichor.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to hide objects in java classes from javascript.<br>
 * If added to a member (field or method), they will act as undefined / non-existant.<br>
 * If added to a class, all members will be hidden.<br>
 * If added to a constructor, new Type() will be hidden.<br>
 * For fields <code>transient</code> keyword can (and is encouraged to) be used instead of this annotation.
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE, ElementType.FIELD, ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface Hidden {
}