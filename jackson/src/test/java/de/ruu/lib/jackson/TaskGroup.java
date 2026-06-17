package de.ruu.lib.jackson;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nullable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.experimental.Accessors;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="jsonId", scope = TaskGroup.class)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
public class TaskGroup
{
	@EqualsAndHashCode.Exclude
	private long jsonId = ThreadLocalRandom.current().nextLong();

	@NonNull private String name;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@JsonManagedReference("taskGroup-task")
	@Nullable private Set<Task> tasks;

	private TaskGroup() {} // with this no-args constructor jackson does not need a @JsonCreator annotated method

	public TaskGroup(@NonNull String name) { this.name = name; }

	public boolean addTask(@NonNull Task task)
	{
		if (isNull(tasks)) tasks = new HashSet<>();
		return tasks.add(task);
	}
}