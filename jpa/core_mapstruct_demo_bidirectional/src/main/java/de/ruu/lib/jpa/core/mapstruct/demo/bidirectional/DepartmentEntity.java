package de.ruu.lib.jpa.core.mapstruct.demo.bidirectional;

import de.ruu.lib.jpa.core.mapstruct.AbstractMappedEntity;
import de.ruu.lib.util.Strings;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static java.util.Objects.isNull;
import static lombok.AccessLevel.NONE;
import static lombok.AccessLevel.PROTECTED;

@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Slf4j
@Getter                   // generate getter methods for all fields using lombok unless configured otherwise ({@code
                          // @Getter(AccessLevel.NONE}))
@Accessors(fluent = true) // generate fluent accessors with lombok and java-bean-style-accessors in non-abstract classes
                          // with ide, fluent accessors will (usually / by default) be ignored by mapstruct
@NoArgsConstructor(access = PROTECTED, force = true) // generate no args constructor for jsonb, jaxb, jpa, mapstruct, ...
public class DepartmentEntity extends AbstractMappedEntity<DepartmentDTO>
{
	/** mutable non-null */
	// no lombok-generation of setter because of additional validation in manually created method
	@NonNull
	@Setter(NONE)
	private String name;

	/** mutable */
	@Setter
	private String description;

	/** no direct access to nullable modifiable set */
	@EqualsAndHashCode.Exclude
	@ToString.Exclude
	@Getter(AccessLevel.NONE)
	private Set<EmployeeEntity> employees;

	/* do not use lombok to make sure that fluent setter with its validation is called */
	public DepartmentEntity(@NonNull String name)
	{
		this();     // just in case something important happens in the default constructor
		name(name); // use fluent setter with its validation
	}

	public @NonNull DepartmentEntity name(@NonNull String name)
	{
		if (Strings.isEmptyOrBlank(name)) throw new IllegalArgumentException("name must not be empty nor blank");
		this.name = name;
		return this;
	}

	void beforeMapping(@NonNull DepartmentDTO input)
	{
		super.beforeMapping(input);
		if (input.optionalEmployees().isPresent())
				input.optionalEmployees().get().forEach(e -> add(e.toSource()));
		name(input.name());
	}

	void afterMapping(@NonNull DepartmentDTO input)
	{
		super.afterMapping(input);
		log.debug("starting");
		log.debug("finished");
	}

	@Override public @NonNull DepartmentDTO toTarget() { return Mapper.INSTANCE.map(this); }

	/** return optional unmodifiable */
	public Optional<Set<EmployeeEntity>> optionalEmployees()
	{
		if (isNull(employees)) return Optional.empty();
		return Optional.of(Set.copyOf(employees));
	}

	public boolean add(@NonNull EmployeeEntity employee)
	{
		if (employee.department() == this)
		{
			if (employeesContains(employee)) return true;
			return nonNullEmployees().add(employee);
		}
		else
		{
			// following check should never return true
			if (employeesContains(employee))
					log.error("employee with {} is already contained in {}", employee.department(), this);

			// assign this department as department of employee and update employees
			employee.department(this);
			return nonNullEmployees().add(employee);
		}
	}

	public boolean remove(@NonNull EmployeeEntity employee)
	{
		if (isNull(employees)) return false;
		return employees.remove(employee);
	}

	private Set<EmployeeEntity> nonNullEmployees()
	{
		if (isNull(employees)) employees = new HashSet<>();
		return employees;
	}

	private boolean employeesContains(EmployeeEntity employee)
	{
		if (isNull(employees)) return false;
		return employees.contains(employee);
	}
}