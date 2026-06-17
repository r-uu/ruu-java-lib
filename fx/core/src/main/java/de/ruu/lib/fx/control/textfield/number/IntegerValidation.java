package de.ruu.lib.fx.control.textfield.number;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Validates integer against {@link #predicate} and, if present, runs {@link #postValidateAction}. */
@RequiredArgsConstructor
public class IntegerValidation implements BiConsumer<TextField, Integer>
{
	@NonNull
	@Getter
	private Predicate<Integer> predicate;
	private Optional<TextFieldPostValidateAction> postValidateAction;

	public IntegerValidation(Predicate<Integer> predicate, TextFieldPostValidateAction postValidateAction)
	{
		this(predicate);
		this.postValidateAction = Optional.ofNullable(postValidateAction);
	}

	@Override public void accept(TextField textField, Integer integer)
	{
		boolean valid = predicate.test(integer);

		if (postValidateAction.isPresent())
		{
			postValidateAction.get().accept(textField, valid);
		}
	}
}