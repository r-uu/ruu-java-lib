package de.ruu.lib.jackson;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.annotation.Nullable;
import lombok.*;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

import static java.util.Objects.isNull;

@JsonIdentityInfo(generator=ObjectIdGenerators.PropertyGenerator.class, property="jsonId", scope = Task.class)
@Getter
@Accessors(fluent = true)
@EqualsAndHashCode
@ToString
@Slf4j
public class Task
{
//	@Serial private static final long serialVersionUID = 1L;

	@EqualsAndHashCode.Exclude
	private final long jsonId = ThreadLocalRandom.current().nextLong();
	/** may be <pre>null</pre> if corresponding jpa instance was not (yet) persisted. */
	@Nullable private Long   id;
	/** may be <pre>null</pre> if corresponding jpa instance was not (yet) persisted. */
	@Nullable private Short  version;
	@NonNull  private String name; // mutable non-null, see handmade setter

	@Setter @Nullable private String    description;
	@Setter @Nullable private LocalDate start;
	@Setter @Nullable private LocalDate end;
	@Setter @NonNull  private Boolean   closed;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@JsonBackReference("taskGroup-task")
	@NonNull  private TaskGroup group;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@Nullable private Task      superTask;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@Nullable private Set<Task> subTasks;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@Nullable private Set<Task> predecessors;

	@EqualsAndHashCode.Exclude
	@ToString         .Exclude
	@Nullable private Set<Task> successors;

	private Task() {} // with this no-args constructor jackson does not need a @JsonCreator annotated method

	public Task(@NonNull TaskGroup group, @NonNull String name)
	{
		this.group = group;
		this.name  = name;

		group.addTask(this);
	}

	public boolean superTask(@NonNull Task task) { return task.addSubTask(this); }

	public boolean addSubTask(@NonNull Task task)
	{
		if (isNull(subTasks)) subTasks = new HashSet<>();
		task.superTask = this;
		return subTasks.add(task);
	}

	public boolean addPredecessor(@NonNull Task task)
	{
		if (    predecessors == null)      predecessors = new HashSet<>();
		if (task.successors  == null) task.successors   = new HashSet<>();

		if (task.successors.add(this))
				if (predecessors.add(task)) return true;
				else task.successors.remove(this); // rollback
		return false;
	}

	public boolean addSuccessor(@NonNull Task task)
	{
		if (     successors   == null)      successors   = new HashSet<>();
		if (task.predecessors == null) task.predecessors = new HashSet<>();

		if (task.predecessors.add(this))
				if (successors.add(task)) return true;
				else task.predecessors.remove(this); // rollback
		return false;
	}
}
