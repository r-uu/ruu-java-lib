package de.ruu.lib.cdi.se.demo;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Getter
@Accessors(fluent = true)
@NoArgsConstructor
@Singleton
public class Context
{
	@Inject private Injectable injectable;
}