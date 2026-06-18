package de.ruu.lib.jpa.core.mapstruct.test;

import de.ruu.lib.jpa.core.mapstruct.AbstractMappedEntity;
import lombok.AccessLevel;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static de.ruu.lib.util.Strings.isEmptyOrBlank;
import static de.ruu.lib.util.Strings.isNotEmptyOrBlank;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
//@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Accessors(fluent = true)
class SimpleMappedEntity extends AbstractMappedEntity<SimpleMappedDTO>
{
	@NonNull private String name;
	
	// Override as public to allow MapStruct generated code access from different package
	@Override
	public void beforeMapping(de.ruu.lib.jpa.core.Entity<Long> input) {
		super.beforeMapping(input);
	}
	
	@Override public @NonNull SimpleMappedDTO toTarget() { return Mapper.INSTANCE.map(this); }

	void setName(@NonNull String name)
	{
		if (isEmptyOrBlank(name)) throw new IllegalArgumentException("name must not be null nor empty");
		this.name = name;
	}
}
