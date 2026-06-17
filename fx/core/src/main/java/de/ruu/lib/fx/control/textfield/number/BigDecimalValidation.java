package de.ruu.lib.fx.control.textfield.number;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

import javafx.scene.control.TextField;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

/** Validates {@link BigDecimal} against {@link #predicate} and, if present, runs {@link #postValidateAction}. */
@RequiredArgsConstructor
public class BigDecimalValidation implements BiConsumer<TextField, BigDecimal>
{
	@NonNull
	@Getter
	private Predicate<BigDecimal> predicate;
	private Optional<TextFieldPostValidateAction> postValidateAction;

	public BigDecimalValidation(Predicate<BigDecimal> predicate, TextFieldPostValidateAction postValidateAction)
	{
		this(predicate);
		this.postValidateAction = Optional.ofNullable(postValidateAction);
	}

	@Override public void accept(TextField textField, BigDecimal integer)
	{
		boolean valid = predicate.test(integer);

		if (postValidateAction.isPresent())
		{
			postValidateAction.get().accept(textField, valid);
		}
	}
}