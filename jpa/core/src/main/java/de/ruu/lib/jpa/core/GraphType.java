package de.ruu.lib.jpa.core;

public enum GraphType
{
	FETCH("jakarta.persistence.fetchgraph"),
	LOAD ("jakarta.persistence.loadgraph" );

	private String name;
	private GraphType(String name) { this.name = name; }
	public String getName() { return name; }
}