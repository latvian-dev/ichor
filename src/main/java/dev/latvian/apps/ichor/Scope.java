package dev.latvian.apps.ichor;

import dev.latvian.apps.ichor.ast.AstStringBuilder;
import dev.latvian.apps.ichor.error.CastError;
import dev.latvian.apps.ichor.error.ConstantReassignError;
import dev.latvian.apps.ichor.error.InternalScriptError;
import dev.latvian.apps.ichor.error.RedeclarationError;
import dev.latvian.apps.ichor.error.ScopeDepthError;
import dev.latvian.apps.ichor.error.ScopeMemberNotFoundError;
import dev.latvian.apps.ichor.prototype.Prototype;
import dev.latvian.apps.ichor.prototype.PrototypeSupplier;
import dev.latvian.apps.ichor.slot.EmptySlotMap;
import dev.latvian.apps.ichor.slot.Slot;
import dev.latvian.apps.ichor.slot.SlotMap;
import dev.latvian.apps.ichor.util.AssignType;
import dev.latvian.apps.ichor.util.ClassPrototype;
import dev.latvian.apps.ichor.util.IchorUtils;
import dev.latvian.apps.ichor.util.JavaArray;
import dev.latvian.apps.ichor.util.ScopeWrapper;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Pattern;

public class Scope {
	public final Scope parent;
	public RootScope root;
	public SlotMap members;
	private int depth;
	public Object scopeOwner;
	public Object scopeThis;
	public Object scopeSuper;
	public Object[] scopeArguments;

	protected Scope(Scope parent) {
		this.parent = parent;
		this.members = EmptySlotMap.INSTANCE;

		if (this.parent != null) {
			this.root = parent.root;
			this.depth = parent.depth + 1;
			this.scopeOwner = parent.scopeOwner;
			this.scopeThis = parent.scopeThis;
			this.scopeSuper = parent.scopeSuper;
			this.scopeArguments = parent.scopeArguments;

			if (this.depth > this.root.maxScopeDepth) {
				throw new ScopeDepthError(this.root.maxScopeDepth);
			}
		}
	}

	// Member Methods //

	@Nullable
	public Slot getDeclaredMember(String name) {
		return members.getSlot(name);
	}

	public void add(String name, @Nullable Object value, byte flags) {
		var slot = members.getSlot(name);

		if (slot == null) {
			if (this != root) {
				var rootSlot = root.members.getSlot(name);

				if (rootSlot != null && rootSlot.isRoot()) {
					throw new RedeclarationError(name, this);
				}
			}

			slot = new Slot(name);
			members = members.upgradeSlotMap();
			members.setSlot(slot);
			slot.value = value;
			slot.flags = flags;
		} else {
			throw new RedeclarationError(name, this);
		}
	}

	public void addMutable(String name, @Nullable Object value) {
		add(name, value, Slot.DEFAULT);
	}

	public void addImmutable(String name, @Nullable Object value) {
		add(name, value, Slot.IMMUTABLE);
	}

	public void add(String name, Class<?> type) {
		addImmutable(name, getClassPrototype(type));
	}

	public void setScopeThis(Scope o) {
		scopeSuper = scopeThis;
		scopeThis = new ScopeWrapper(o);
	}

	public boolean setMember(String name, @Nullable Object value) {
		Scope s = this;

		do {
			var slot = s.members.getSlot(name);

			if (slot != null) {
				if (slot.value != Special.UNDEFINED && slot.isImmutable()) {
					throw new ConstantReassignError(name);
				} else {
					slot.value = value;
					// slot.prototype = null;
					return true;
				}
			}

			s = s.parent;
		}
		while (s != null);

		throw new ScopeMemberNotFoundError(name, this);
	}

	public AssignType hasDeclaredMember(String name) {
		var slot = members.getSlot(name);

		if (slot == null) {
			return AssignType.NONE;
		} else if (slot.isImmutable()) {
			return AssignType.IMMUTABLE;
		} else {
			return AssignType.MUTABLE;
		}
	}

	public void deleteDeclaredMember(String name) {
		var slot = members.getSlot(name);

		if (slot == null) {
			throw new ScopeMemberNotFoundError(name, this);
		}

		members.removeSlot(name);
	}

	public Set<String> getDeclaredMemberNames() {
		return members.getSlotNames();
	}

	public void deleteAllDeclaredMembers() {
		for (var id : Set.copyOf(getDeclaredMemberNames())) {
			deleteDeclaredMember(id);
		}
	}

	// Recursive Member Methods //

	public Object getMember(String name) {
		Scope s = this;

		do {
			var slot = s.getDeclaredMember(name);

			if (slot != null) {
				return slot.value;
			}

			s = s.parent;
		}
		while (s != null);

		return switch (name) {
			case "this" -> scopeThis;
			case "super" -> scopeSuper;
			case "arguments" -> scopeArguments;
			default -> throw new ScopeMemberNotFoundError(name, this);
		};
	}

	public AssignType hasMember(String name) {
		Scope s = this;

		do {
			var t = s.hasDeclaredMember(name);

			if (t.isSet()) {
				return t;
			}

			s = s.parent;
		}
		while (s != null);

		return switch (name) {
			case "this", "super", "arguments" -> AssignType.IMMUTABLE;
			default -> AssignType.NONE;
		};
	}

	public Scope push() {
		return push(scopeOwner);
	}

	public Scope push(Object owner) {
		var p = new Scope(this);
		p.scopeOwner = owner;
		root.checkTimeout();
		return p;
	}

	@Override
	public String toString() {
		if (scopeOwner instanceof ClassPrototype c) {
			return "Scope[" + getDepth() + ']' + getDeclaredMemberNames() + ":" + c.astClass.name;
		}

		return "Scope[" + getDepth() + ']' + getDeclaredMemberNames();
	}

	public int getDepth() {
		return depth;
	}

	public Object eval(Object o) {
		if (o == Special.UNDEFINED || o instanceof Callable) {
			return o;
		} else if (o instanceof Evaluable eval) {
			return eval.eval(this);
		} else {
			return o;
		}
	}

	public void asString(Object o, StringBuilder builder, boolean escape) {
		if (o == null) {
			builder.append("null");
		} else if (o instanceof Number) {
			AstStringBuilder.wrapNumber(o, builder);
		} else if (o instanceof Character || o instanceof CharSequence) {
			if (escape) {
				AstStringBuilder.wrapString(o, builder);
			} else {
				builder.append(o);
			}
		} else if (o instanceof Boolean || o instanceof Special) {
			builder.append(o);
		} else if (o instanceof Evaluable eval) {
			eval.evalString(this, builder);
		} else {
			var p = getPrototype(o);

			if (o == p || !p.asString(this, p.cast(o), builder, escape)) {
				builder.append(o);
			}
		}
	}

	public String asString(Object o, boolean escape) {
		if (o == null) {
			return "null";
		} else if (o instanceof Number) {
			return AstStringBuilder.wrapNumber(o);
		} else if (o instanceof Character || o instanceof CharSequence) {
			if (escape) {
				var builder = new StringBuilder();
				AstStringBuilder.wrapString(o, builder);
				return builder.toString();
			} else {
				return o.toString();
			}
		} else if (o instanceof Boolean || o instanceof Special) {
			return o.toString();
		} else if (o instanceof Evaluable eval) {
			var builder = new StringBuilder();
			eval.evalString(this, builder);
			return builder.toString();
		} else {
			var p = getPrototype(o);

			if (o == p) {
				return o.toString();
			}

			var builder = new StringBuilder();

			if (!p.asString(this, p.cast(o), builder, escape)) {
				return o.toString();
			}

			return builder.toString();
		}
	}

	private Number asNumber0(Object o) {
		var p = getPrototype(o);
		var n = o == p ? null : p.asNumber(this, p.cast(o));
		return n == null ? IchorUtils.ONE : n;
	}

	public Number asNumber(Object o) {
		if (Special.isInvalid(o)) {
			return IchorUtils.NaN;
		} else if (o instanceof Number) {
			return (Number) o;
		} else if (o instanceof Boolean) {
			return (Boolean) o ? IchorUtils.ONE : IchorUtils.ZERO;
		} else if (o instanceof CharSequence) {
			try {
				return IchorUtils.parseNumber(o.toString());
			} catch (Exception ex) {
				return IchorUtils.NaN;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(this);
		}

		return asNumber0(o);
	}

	public double asDouble(Object o) {
		if (Special.isInvalid(o)) {
			return Double.NaN;
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1D : 0D;
		} else if (o instanceof CharSequence) {
			try {
				return IchorUtils.parseNumber(o.toString()).doubleValue();
			} catch (Exception ex) {
				return Double.NaN;
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalDouble(this);
		}

		return asNumber0(o).doubleValue();
	}

	public int asInt(Object o) {
		if (Special.isInvalid(o)) {
			return 0;
		} else if (o instanceof Number) {
			return ((Number) o).intValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1 : 0;
		} else if (o instanceof CharSequence) {
			try {
				return IchorUtils.parseNumber(o.toString()).intValue();
			} catch (Exception ex) {
				throw new InternalScriptError(ex);
			}
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalInt(this);
		}

		return asNumber0(o).intValue();
	}

	public long asLong(Object o) {
		if (Special.isInvalid(o)) {
			return 0L;
		} else if (o instanceof Number) {
			return ((Number) o).longValue();
		} else if (o instanceof Boolean) {
			return (Boolean) o ? 1L : 0L;
		} else if (o instanceof CharSequence) {
			try {
				return IchorUtils.parseNumber(o.toString()).longValue();
			} catch (Exception ex) {
				throw new InternalScriptError(ex);
			}
		} else if (o instanceof Evaluable) {
			// add evalLong
			return ((Evaluable) o).evalInt(this);
		}

		return asNumber0(o).longValue();
	}

	public boolean asBoolean(Object o) {
		if (o instanceof Boolean) {
			return (Boolean) o;
		} else if (Special.isInvalid(o)) {
			return false;
		} else if (o instanceof Number) {
			return ((Number) o).doubleValue() != 0D;
		} else if (o instanceof CharSequence) {
			return !o.toString().isEmpty();
		} else if (o instanceof Evaluable) {
			return ((Evaluable) o).evalBoolean(this);
		}

		var p = getPrototype(o);
		var n = o == p ? null : p.asBoolean(this, p.cast(o));
		return n == null ? Boolean.TRUE : n;
	}

	public char asChar(Object o) {
		if (o instanceof Character) {
			return (Character) o;
		} else if (o instanceof CharSequence) {
			return ((CharSequence) o).charAt(0);
		} else if (o instanceof Number) {
			return (char) ((Number) o).intValue();
		} else if (o instanceof Evaluable) {
			var builder = new StringBuilder();
			((Evaluable) o).evalString(this, builder);
			return builder.charAt(0);
		}

		throw new CastError(o, "Character");
	}

	@SuppressWarnings("rawtypes")
	public Class asClass(Object o) {
		return o instanceof Class s ? s : (Class) as(o, Class.class);
	}

	@SuppressWarnings("rawtypes")
	public Map asMap(Object o) {
		return o instanceof Map s ? s : (Map) as(o, Map.class);
	}

	@SuppressWarnings("rawtypes")
	public List asList(Object o) {
		return o instanceof List s ? s : o != null ? o.getClass().isArray() ? JavaArray.of(o) : (List) as(o, List.class) : null;
	}

	public Object as(Object o, @Nullable Class<?> toType) {
		if (Special.isInvalid(o)) {
			return null;
		} else if (toType == null || toType == Void.TYPE || toType == Object.class || toType.isInstance(o)) {
			return o;
		} else if (toType == String.class || toType == CharSequence.class) {
			return asString(o, false);
		} else if (toType == Number.class) {
			return asNumber(o);
		} else if (toType == Boolean.class || toType == Boolean.TYPE) {
			return asBoolean(o);
		} else if (toType == Character.class || toType == Character.TYPE) {
			return asChar(o);
		} else if (toType == Byte.class || toType == Byte.TYPE) {
			return asNumber(o).byteValue();
		} else if (toType == Short.class || toType == Short.TYPE) {
			return asNumber(o).shortValue();
		} else if (toType == Integer.class || toType == Integer.TYPE) {
			return asInt(o);
		} else if (toType == Long.class || toType == Long.TYPE) {
			return asLong(o);
		} else if (toType == Float.class || toType == Float.TYPE) {
			return asNumber(o).floatValue();
		} else if (toType == Double.class || toType == Double.TYPE) {
			return asDouble(o);
		} else if (o instanceof TypeAdapter typeAdapter && typeAdapter.canAdapt(this, toType)) {
			return typeAdapter.adapt(this, toType);
		}

		var c = customAs(o, toType);

		if (c != Special.NOT_FOUND) {
			return c;
		}

		var p = getPrototype(o);
		var a = p.adapt(this, p.cast(o), toType);

		if (a != Special.NOT_FOUND) {
			return a;
		} else if (o instanceof Iterable<?> itr && toType.isArray()) {
			return JavaArray.adaptToArray(this, itr, toType);
		} else {
			throw new CastError(o, toType.getName());
		}
	}

	@Nullable
	protected Object customAs(Object o, Class<?> toType) {
		return Special.NOT_FOUND;
	}

	public Prototype<?> getPrototype(Object o) {
		if (o == null) {
			return Special.NULL.prototype;
		} else if (o instanceof PrototypeSupplier s) {
			return s.getPrototype(this);
		} else if (o instanceof Boolean) {
			return root.booleanPrototype;
		} else if (o instanceof Number) {
			return root.numberPrototype;
		} else if (o instanceof CharSequence) {
			return root.stringPrototype;
		} else if (o instanceof Class) {
			return root.classPrototype;
		} else if (o instanceof Pattern) {
			return root.regExpPrototype;
		} else if (o.getClass().isArray()) {
			return root.arrayPrototype;
		}

		return getClassPrototype(o.getClass());
	}

	public Prototype<?> getClassPrototype(Class<?> c) {
		if (c == Boolean.class) {
			return root.booleanPrototype;
		} else if (c == Number.class) {
			return root.numberPrototype;
		} else if (c == String.class) {
			return root.stringPrototype;
		} else if (c == Class.class) {
			return root.classPrototype;
		} else if (c == Map.class || c == HashMap.class || c == LinkedHashMap.class || c == IdentityHashMap.class || c == EnumMap.class) {
			return root.jsMapPrototype;
		} else if (c == Set.class || c == HashSet.class || c == LinkedHashSet.class || c == EnumSet.class) {
			return root.jsSetPrototype;
		} else if (c == List.class || c == ArrayList.class || c == LinkedList.class) {
			return root.listPrototype;
		} else if (c == Collection.class) {
			return root.collectionPrototype;
		} else if (c == Iterable.class) {
			return root.iterablePrototype;
		} else if (c == Pattern.class) {
			return root.regExpPrototype;
		} else if (c.isArray()) {
			return root.arrayPrototype;
		}

		var p = root.classPrototypes.get(c);

		if (p == null) {
			p = new Prototype<>(this, c);
			root.classPrototypes.put(c, p);
		}

		return p;
	}

	public boolean equals(Object left, Object right, boolean shallow) {
		if (left == right) {
			return true;
		} else if (left instanceof Number l && right instanceof Number r) {
			return Math.abs(l.doubleValue() - r.doubleValue()) < 0.00001D;
		} else if (left instanceof CharSequence || left instanceof Character || right instanceof CharSequence || right instanceof Character) {
			return asString(left, false).equals(asString(right, false));
		} else {
			var p = getPrototype(left);
			return p.equals(this, p.cast(left), right, shallow);
		}
	}

	public int compareTo(Object left, Object right) {
		if (left == right || Objects.equals(left, right)) {
			return 0;
		} else if (left instanceof Number l && right instanceof Number r) {
			return Math.abs(l.doubleValue() - r.doubleValue()) < 0.00001D ? 0 : Double.compare(l.doubleValue(), r.doubleValue());
		} else {
			var p = getPrototype(right);
			return p.compareTo(this, p.cast(left), right);
		}
	}
}
