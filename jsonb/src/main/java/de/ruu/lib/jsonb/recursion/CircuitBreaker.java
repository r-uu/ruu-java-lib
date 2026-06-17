package de.ruu.lib.jsonb.recursion;

import de.ruu.lib.util.bimapped.BiMap;
import de.ruu.lib.jsonb.JsonbConfigurator;
import jakarta.json.Json;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.adapter.JsonbAdapter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Optional;

@Slf4j
@RequiredArgsConstructor
public class CircuitBreaker<ORIGINAL> implements JsonbAdapter<ORIGINAL, JsonValue>
{
	private static final Jsonb JSONB = new JsonbConfigurator().getContext();

	private BiMap biMap = new BiMap();

	private @NonNull Class<ORIGINAL> type;

	@Override public JsonValue adaptToJson(@NonNull ORIGINAL original) throws Exception
	{
		log.debug("marshalling {}", original.getClass().getName());
		Object adapted = null;

		Optional<?> optionalAdapted = biMap.lookup(original, original.getClass());

		if (optionalAdapted.isPresent())
		{
			adapted = optionalAdapted.get();
		}
		else
		{
			adapted = original;
			biMap.put(original, adapted);
		}

		return Json.createValue(JSONB.toJson(adapted));
	}

	@Override public ORIGINAL adaptFromJson(@NonNull JsonValue adapted) throws Exception
	{
		log.debug("unmarshalling {}", type.getName());
		return JSONB.fromJson(adapted.toString(), type);
	}
}