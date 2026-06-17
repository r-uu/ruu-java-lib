package de.ruu.lib.jsonb.recursion;

import jakarta.json.bind.annotation.JsonbTypeAdapter;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

/**
 * data class for Jsonb tests
 * <p>
 * data classes have to be top-level, {@code public} and equipped with a {@code public} no-args default constructor
 *
 * @author ruu
 */
@Getter
@ToString
public class Parent
{
	@NonNull
	private String      field;
	@JsonbTypeAdapter(ChildrenAdapter.class)
	private Set<Child> children = new HashSet<>();

	/** has to be {@code public} for Jsonb */
	public Parent() {}
	Parent(@NonNull String field) { this.field = field; }
}