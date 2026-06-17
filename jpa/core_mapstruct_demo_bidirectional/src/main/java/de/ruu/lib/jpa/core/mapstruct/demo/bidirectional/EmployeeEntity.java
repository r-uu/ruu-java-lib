package de.ruu.lib.jpa.core.mapstruct.demo.bidirectional;

import de.ruu.lib.jpa.core.mapstruct.AbstractMappedEntity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
@Getter                   // generate getter methods for all fields using lombok unless configured otherwise ({@code
                          // @Getter(AccessLevel.NONE}))
@Accessors(fluent = true) // generate fluent accessors with lombok and java-bean-style-accessors in non-abstract classes
                          // with ide, fluent accessors will (usually / by default) be ignored by mapstruct
//@RequiredArgsConstructor// provide handmade required args constructor to properly handle relationships
@NoArgsConstructor(access = PROTECTED, force = true) // generate no args constructor for jsonb, jaxb, jpa, mapstruct, ...
public class EmployeeEntity extends AbstractMappedEntity<EmployeeDTO>
{
	/** mutable, but not nullable */
	@NonNull @Setter private String name;

	/** mutable, but not nullable */
	// no java-bean-style accessor here, mapstruct will ignore fields without bean-style-accessor so mapping can be
	// controlled in beforeMapping
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@NonNull
	@Setter
	private DepartmentEntity department;

	/** provide handmade required args constructor to properly handle relationships */
	EmployeeEntity(@NonNull DepartmentEntity department, @NonNull String name)
	{
		this.department = department;
		name(name);
		department.add(this);
	}

	// java bean style accessors for those who do not work with fluent style accessors (mapstruct)
	public @NonNull String getName() { return name(); }

	void beforeMapping(@NonNull EmployeeDTO target)
	{
		super.beforeMapping(target);
		log.debug("before mapping starting");
		log.debug("before mapping finished");
	}

	void afterMapping(@NonNull EmployeeDTO target)
	{
		super.afterMapping(target);
		log.debug("after mapping starting");
		log.debug("after mapping finished");
	}

	@Override public @NonNull EmployeeDTO toTarget() { return Mapper.INSTANCE.map(this); }
}