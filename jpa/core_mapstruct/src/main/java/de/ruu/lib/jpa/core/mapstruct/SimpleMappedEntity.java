package de.ruu.lib.jpa.core.mapstruct;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;

import static de.ruu.lib.util.Strings.isEmptyOrBlank;
import static lombok.AccessLevel.PACKAGE;

@RequiredArgsConstructor(access = PACKAGE)
//@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Accessors(fluent = true)
class SimpleMappedEntity extends AbstractMappedEntity<SimpleMappedDTO>
{
	@NonNull private String name;
	@Override public @NonNull SimpleMappedDTO toTarget() { return Mapper.INSTANCE.map(this); }

	void setName(@NonNull String name)
	{
		if (isEmptyOrBlank(name)) throw new IllegalArgumentException("name must not be null nor empty");
		this.name = name;
	}
}
