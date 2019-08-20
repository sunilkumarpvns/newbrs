package com.elitecore.commons.base;

import static com.elitecore.commons.base.Preconditions.checkNotNull;

/**
 * An immutable object that may contain a non-null reference to another object. Each
 * instance of this type either contains a non-null reference, or contains nothing (in
 * which case we say that the reference is "absent"); it is never said to "contain {@code
 * null}".
 *
 * <p>A non-null {@code Optional<T>} reference can be used as a replacement for a nullable
 * {@code T} reference. It allows you to represent "a {@code T} that must be present" and
 * a "a {@code T} that might be absent" as two distinct types in your program, which can
 * aid clarity.
 *
 * <p>Some uses of this class include
 *
 * <ul>
 * <li>As a method return type, as an alternative to returning {@code null} to indicate
 *     that no value was available
 * <li>To distinguish between "unknown" (for example, not present in a map) and "known to
 *     have no value" (present in the map, with value {@code Optional.absent()})
 * </ul>
 * 
 * This class is a stripped down version of Google Guava library
 * 
 * @author narendra.pathai
 **/
public abstract class Optional<T> {

	/**
	 * Returns an {@code Optional} instance with no contained reference.
	 */
	public static <T> Optional<T> absent() {
		return Absent.<T>withType();
	}

	/**
	 * If {@code nullableReference} is non-null, returns an {@code Optional} instance containing that
	 * reference; otherwise returns {@link Optional#absent}.
	 */
	public static <T> Optional<T> of(T reference) {
		return reference == null ? Absent.<T>absent() : new Present<T>(reference); 
	}

	/**
	 * Returns {@code true} if this holder contains a (non-null) instance.
	 */
	public abstract boolean isPresent();

	/**
	 * Returns the contained instance, which must be present.
	 *
	 * @throws IllegalStateException if the instance is absent ({@link #isPresent} returns
	 *     {@code false})
	 */
	public abstract T get();

	/**
	 * Returns the contained instance if it is present; {@code defaultValue} otherwise. If
	 * no default value should be required because the instance is known to be present, use
	 * {@link #get()} instead. For a default value of {@code null}, use {@link #orNull}.
	 */
	public abstract T orElse(T defaultValue);

	/**
	 * Returns the contained instance if it is present; {@code null} otherwise. If the
	 * instance is known to be present, use {@link #get()} instead.
	 */
	public abstract T orNull();

	public abstract Optional<T> or(Optional<T> other);
}

class Absent<T> extends Optional<T> {
	static final Absent<Object> absent = new Absent<Object>();

	@SuppressWarnings("unchecked")
	static <T> Absent<T> withType() {
		return (Absent<T>) absent;
	}

	@Override
	public boolean isPresent() {
		return false;
	}

	@Override
	public T get() {
		throw new IllegalStateException("Optional.get() cannot be called on an absent value");
	}

	@Override
	public String toString() {
		return "Optional.absent()";
	}

	@Override
	public T orElse(T defaultValue) {
		return checkNotNull(defaultValue, "orElse reference should not be null. " +
		"Use orNull instead of orElse");
	}

	@Override
	public T orNull() {
		return null;
	}

	@Override
	public Optional<T> or(Optional<T> other) {
		return other;
	}
}

class Present<T> extends Optional<T> {

	private final T reference;

	public Present(T reference) {
		this.reference = reference;
	}

	@Override
	public boolean isPresent() {
		return true;
	}

	@Override
	public T get() {
		return reference;
	}

	@Override
	public String toString() {
		return String.valueOf(reference);
	}

	@Override
	public T orElse(T defaultValue) {
		return reference;
	}

	@Override
	public T orNull() {
		return reference;
	}

	@Override
	public Optional<T> or(Optional<T> other) {
		return this;
	}
}