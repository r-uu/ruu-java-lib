package de.ruu.lib.jpa.core.mapstruct.demo.tree;

import de.ruu.lib.jpa.core.AbstractDTO;
import de.ruu.lib.jpa.core.mapstruct.AbstractMappedEntity;
import de.ruu.lib.util.Strings;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

/**
 * This is almost a complete copy of the code from {@link NodeSimple} or {@link AbstractMappedNode} just replacing generic type variables as T with concrete type like {@link NodeEntity} or {@link NodeDTO}.
 * TODO Is there a better way to go?
 * TODO Delegates perhaps?
 */
@Slf4j
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class NodeEntity extends AbstractMappedEntity<NodeDTO> implements Node<NodeEntity>
{
	@NonNull  private String           name;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Nullable private NodeEntity       parent;
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@NonNull  private List<NodeEntity> children;

	protected NodeEntity() { } // required by mapstruct

	public NodeEntity(@NonNull String name)
	{
		name(name);
		children = new ArrayList<>();
	}

	public NodeEntity(@NonNull String name, @Nullable NodeEntity parent)
	{
		this(name);
		this.parent = parent;
	}

	@Override
	@NonNull
	public String name() { return name; }
	@NonNull
	public NodeEntity name(@NonNull String name)
	{
		if (Strings.isEmptyOrBlank(name)) throw new IllegalArgumentException("name must not be empty nor blank");
		this.name = name;
		return this;
	}

	/**
	 * Manually implement this method because lombok can not generate accessor returning {@code Optional} as required by
	 * {@link Node} for a non-{@code Optional} field as {@link #parent}.
	 *
	 * @return {@link #parent} wrapped in an {@code Optional}
	 */
	@Override
	@NonNull
	public Optional<NodeEntity> parent() { return Optional.ofNullable(parent); }
	@Override
	@NonNull
	public NodeEntity parent(@Nullable NodeEntity parent)
	{
		this.parent = parent;
		return this;
	}

	/**
	 * Manually implement this method because lombok can not generate accessor returning unmodifiable list.;
	 *
	 * @return {@link #parent} wrapped in an {@code Optional}
	 */
	@Override
	@NonNull public List<NodeEntity> children() { return Collections.unmodifiableList(children); }

	/** handling of bidirectional relation */
	@Override
	public boolean add(@NonNull NodeEntity node)
	{
		node.parent(this);
		return children.add(node);
	}

	/** handling of bidirectional relation */
	@Override
	public boolean remove(@NonNull NodeEntity node)
	{
		boolean result = children.remove(node);
		if (result) node.parent(null);
		return result;
	}

	@Override
	public @NonNull NodeDTO toTarget() { return MapperNodeEntityNodeDTO.INSTANCE.map(this); }

	protected void beforeMapping(@NonNull NodeSimple input) { super.beforeMapping(input); }
	protected void beforeMapping(@NonNull NodeDTO    input) { super.beforeMapping(input); }

	private class AbstractDTOSimple extends AbstractDTO<NodeEntity>
	{
		private @NonNull NodeSimple nodeSimple;
		private AbstractDTOSimple(@NonNull NodeSimple nodeSimple) { this.nodeSimple = nodeSimple; }
	}

  /**
	 * to be called in mapstruct callbacks by subclasses
	 * <p>
	 * Picks the {@link AbstractDTO#getId()} and {@link AbstractDTO#getVersion()} values and assigns them to the
	 * respective fields in this class.
	 *
	 * @param source
	 * @throws NullPointerException if {@code source} is {@code null}
   */


	protected void mapIdAndVersion(@NonNull NodeSimple source)
	{
		// set fields that can not be modified from outside
		super.mapIdAndVersion(new AbstractDTOSimple(source));
	}
}