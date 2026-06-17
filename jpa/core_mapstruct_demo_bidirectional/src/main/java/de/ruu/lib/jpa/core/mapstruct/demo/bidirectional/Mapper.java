package de.ruu.lib.jpa.core.mapstruct.demo.bidirectional;

import de.ruu.lib.mapstruct.ReferenceCycleTracking;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.mapstruct.BeforeMapping;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.ObjectFactory;
import org.mapstruct.factory.Mappers;

@Slf4j
@org.mapstruct.Mapper
abstract class Mapper
{
	static Mapper INSTANCE = Mappers.getMapper(Mapper.class);

	private static ReferenceCycleTracking CONTEXT  = new ReferenceCycleTracking();

	// name and description are set in the @ObjectFactory methods, not by MapStruct field mapping
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "description", ignore = true)
	abstract DepartmentEntity map(DepartmentDTO    input);
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "description", ignore = true)
	abstract DepartmentDTO    map(DepartmentEntity input);

	// name, description and department are set in the @ObjectFactory methods, not by MapStruct field mapping
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "department", ignore = true)
	abstract EmployeeEntity   map(EmployeeDTO    input);
	@Mapping(target = "name", ignore = true)
	@Mapping(target = "department", ignore = true)
	abstract EmployeeDTO      map(EmployeeEntity input);

	/** annotating parameter {@code target} with {@link MappingTarget} is essential for this method being called */
	@BeforeMapping void beforeMapping(DepartmentEntity source, @MappingTarget DepartmentDTO target)
	{
		log.debug("\nsource\n{}\ntarget\n{}", source, target);
		target.beforeMapping(source); // invoke callback for mapping
	}

	/** annotating parameter {@code target} with {@link MappingTarget} is essential for this method being called */
	@BeforeMapping void beforeMapping(DepartmentDTO source, @MappingTarget DepartmentEntity target)
	{
		log.debug("\nsource\n{}\ntarget\n{}", source, target);
		target.beforeMapping(source); // invoke callback for mapping
	}

	/** annotating parameter {@code target} with {@link MappingTarget} is essential for this method being called */
	@BeforeMapping void beforeMapping(EmployeeEntity source, @MappingTarget EmployeeDTO target)
	{
		log.debug("\nsource\n{}\ntarget\n{}", source, target);
		target.beforeMapping(source); // invoke callback for mapping
	}

	/** annotating parameter {@code target} with {@link MappingTarget} is essential for this method being called */
	@BeforeMapping void beforeMapping(EmployeeDTO source, @MappingTarget EmployeeEntity target)
	{
		log.debug("\nsource\n{}\ntarget\n{}", source, target);
		target.beforeMapping(source); // invoke callback for mapping
	}

	@ObjectFactory @NonNull DepartmentEntity lookupOrCreate(@NonNull DepartmentDTO input)
	{
		DepartmentEntity result = CONTEXT.get(input, DepartmentEntity.class);
		if (result == null)
		{
			result = new DepartmentEntity(input.name());
			CONTEXT.put(input, result);
			CONTEXT.put(result, input);
		}
		return result;
	}

	@ObjectFactory @NonNull DepartmentDTO lookupOrCreate(@NonNull DepartmentEntity input)
	{
		DepartmentDTO result = CONTEXT.get(input, DepartmentDTO.class);
		if (result == null)
		{
			result = new DepartmentDTO(input.name());
			CONTEXT.put(input, result);
			CONTEXT.put(result, input);
		}
		return result;
	}

	@ObjectFactory @NonNull EmployeeEntity lookupOrCreate(@NonNull EmployeeDTO input)
	{
		EmployeeEntity result = CONTEXT.get(input, EmployeeEntity.class);
		if (result == null)
		{
			DepartmentEntity department = CONTEXT.get(input.department(), DepartmentEntity.class);
			if (department == null)
					department = new DepartmentEntity(input.department().name());
			result = new EmployeeEntity(department, input.name());
			CONTEXT.put(input, result);
			CONTEXT.put(result, input);
		}
		return result;
	}

	@ObjectFactory @NonNull EmployeeDTO lookupOrCreate(@NonNull EmployeeEntity input)
	{
		EmployeeDTO result = CONTEXT.get(input, EmployeeDTO.class);
		if (result == null)
		{
			DepartmentDTO department = CONTEXT.get(input.department(), DepartmentDTO.class);
			if (department == null)
					department = new DepartmentDTO(input.department().name());
//					throw new IllegalStateException("department " + input.department() + " for employee " + result + " not in context");
			result = new EmployeeDTO(department, input.name());
			CONTEXT.put(input, result);
			CONTEXT.put(result, input);
		}
		return result;
	}
}