package de.ruu.lib.gen.java.fx.tableview;

import lombok.AllArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
public class JavaModelDemoDTO
{
	boolean      aBoolean;
	int          anInteger;
	String       aString;
	BigDecimal   aBigDecimal;
	List<String> stringList;
}