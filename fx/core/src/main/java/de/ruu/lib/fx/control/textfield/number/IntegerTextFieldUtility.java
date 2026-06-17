package de.ruu.lib.fx.control.textfield.number;

import javafx.scene.control.TextField;

import java.util.Optional;

import static de.ruu.lib.util.BooleanFunctions.not;
import static java.util.Objects.isNull;

public abstract class IntegerTextFieldUtility
{
	public static Optional<IntegerTextFieldValueChangeListener> getValueChangeListener(TextField textField)
	{
		Object object = textField.getProperties().get(IntegerTextFieldValueChangeListener.class.getName());
	
		if (not(isNull(object)))
		{
			if (object instanceof IntegerTextFieldValueChangeListener)
			{
				return Optional.of((IntegerTextFieldValueChangeListener) object);
			}
		}
	
		return Optional.empty();
	}

	public static Optional<IntegerValidation> getValidation(TextField textField)
	{
		Optional<IntegerTextFieldValueChangeListener> listenerOptional = getValueChangeListener(textField);
	
		if (listenerOptional.isPresent())
		{
			return listenerOptional.get().getValidation();
		}
	
		return Optional.empty();
	}

	public static Optional<Integer> getCurrentTextFieldValueAsInteger(TextField textField)
	{
		Optional<IntegerTextFieldValueChangeListener> listenerOptional = getValueChangeListener(textField);
	
		if (listenerOptional.isPresent())
		{
			return listenerOptional.get().getCurrentTextFieldValueAsInteger();
		}
	
		return Optional.empty();
	}

	public static boolean isCurrentTextFieldValueValid(TextField textField)
	{
		Optional<IntegerTextFieldValueChangeListener> listenerOptional = getValueChangeListener(textField);
	
		if (listenerOptional.isPresent())
		{
			return listenerOptional.get().isCurrentTextFieldValueValid();
		}
	
		return true;
	}
}