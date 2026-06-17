package de.ruu.lib.fx.css;

import javafx.scene.control.Button;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class ButtonUtil
{
	@NonNull private Button button;

	public void backgroundColor(Color color)
	{
		button.setStyle(ButtonProperties.BACKGROUND_COLOR.value + ": " + color.value);
	}

	public static void backgroundColor(Button button, Color color)
	{
		button.setStyle(ButtonProperties.BACKGROUND_COLOR.value + ": " + color.value);
	}
}