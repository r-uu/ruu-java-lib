package de.ruu.lib.jsonb.recursion;

import de.ruu.lib.util.json.Sanitiser;
import de.ruu.lib.jsonb.AbstractSetAdapter;
import jakarta.json.JsonValue;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.Set;

@Slf4j
public class ChildrenAdapter extends AbstractSetAdapter<Child>
{
	private final static Type CHILDREN_TYPE = new HashSet<Child>() { }.getClass().getGenericSuperclass();

	@Override protected Type getType() { return CHILDREN_TYPE; }

	@Override public JsonValue adaptToJson(Set<Child> param) throws Exception
	{
		try
		{
			return super.adaptToJson(param);
		}
		catch (Exception e)
		{
			log.error("failure adapting to json", e);
			throw e;
		}
	}

	@Override public Set<Child> adaptFromJson(JsonValue jsonValue) throws Exception
	{
		try
		{
			return super.adaptFromJson(jsonValue);
		}
		catch (Exception e)
		{
			log.error("failure adapting from json, jsonValue (sanitised)\n" + Sanitiser.sanitise(jsonValue.toString()), e);
			throw e;
		}
	}
}