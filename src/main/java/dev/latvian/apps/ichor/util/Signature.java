package dev.latvian.apps.ichor.util;

import java.lang.reflect.Executable;
import java.util.Arrays;

public final class Signature {
	public static final Signature EMPTY = new Signature();
	private static final Signature ONE_ANY = new Signature(Void.TYPE);
	private static final Signature ONE_OBJECT = new Signature(Object.class);
	private static final Signature ONE_OBJECT_ARRAY = new Signature(Object[].class);
	private static final Signature ONE_STRING = new Signature(String.class);
	private static final Signature ONE_CHARACTER = new Signature(Character.TYPE);
	private static final Signature ONE_BOOLEAN = new Signature(Boolean.TYPE);
	private static final Signature ONE_BYTE = new Signature(Byte.TYPE);
	private static final Signature ONE_SHORT = new Signature(Short.TYPE);
	private static final Signature ONE_INTEGER = new Signature(Integer.TYPE);
	private static final Signature ONE_LONG = new Signature(Long.TYPE);
	private static final Signature ONE_FLOAT = new Signature(Float.TYPE);
	private static final Signature ONE_DOUBLE = new Signature(Double.TYPE);

	public static Signature of(Class<?>[] types) {
		if (types.length == 0) {
			return EMPTY;
		} else if (types.length == 1) {
			if (types[0] == Object.class) {
				return ONE_OBJECT;
			} else if (types[0] == Void.TYPE) {
				return ONE_ANY;
			} else if (types[0] == ONE_OBJECT_ARRAY.types[0]) {
				return ONE_OBJECT_ARRAY;
			} else if (types[0] == String.class) {
				return ONE_STRING;
			} else if (types[0] == Character.TYPE) {
				return ONE_CHARACTER;
			} else if (types[0] == Boolean.TYPE) {
				return ONE_BOOLEAN;
			} else if (types[0] == Byte.TYPE) {
				return ONE_BYTE;
			} else if (types[0] == Short.TYPE) {
				return ONE_SHORT;
			} else if (types[0] == Integer.TYPE) {
				return ONE_INTEGER;
			} else if (types[0] == Long.TYPE) {
				return ONE_LONG;
			} else if (types[0] == Float.TYPE) {
				return ONE_FLOAT;
			} else if (types[0] == Double.TYPE) {
				return ONE_DOUBLE;
			}
		}

		return new Signature(types);
	}

	public static Signature of(Executable executable) {
		return executable.getParameterCount() == 0 ? EMPTY : of(executable.getParameterTypes());
	}

	public static Signature ofArgs(Object[] args) {
		var types = new Class[args.length];

		for (int i = 0; i < args.length; i++) {
			types[i] = args[i] == null ? Void.TYPE : args[i].getClass();
		}

		return of(types);
	}

	public final Class<?>[] types;
	private int hashCode;
	private String toString;

	private Signature(Class<?>... t) {
		types = t;
	}

	@Override
	public int hashCode() {
		if (hashCode == 0) {
			hashCode = Arrays.hashCode(types);

			if (hashCode == 0) {
				hashCode = 1;
			}
		}

		return hashCode;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == this) {
			return true;
		} else if (obj instanceof Signature s && types.length == s.types.length) {
			for (int i = 0; i < types.length; i++) {
				if (types[i] != s.types[i] && types[i] != Void.TYPE && s.types[i] != Void.TYPE) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		if (toString == null) {
			var sb = new StringBuilder();
			sb.append('(');

			for (var t : types) {
				sb.append(t.descriptorString());
			}

			sb.append(')');
			toString = sb.toString();
		}

		return toString;
	}
}
