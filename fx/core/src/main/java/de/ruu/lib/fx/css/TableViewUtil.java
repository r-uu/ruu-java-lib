package de.ruu.lib.fx.css;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.skin.TableColumnHeader;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

import java.util.HashSet;
import java.util.Set;

import static de.ruu.lib.fx.css.TableViewSelector.COLUMN_HEADER;
import static de.ruu.lib.fx.css.TableViewSelector.LABEL;

@RequiredArgsConstructor
public class TableViewUtil
{
	@NonNull private TableView<?> tableView;

	public Set<TableColumnHeader> tableColumnHeaders()
	{
		Set<TableColumnHeader> result = new HashSet<>();

		for (Node node : tableView.lookupAll(COLUMN_HEADER.value))
		{
			if (node instanceof TableColumnHeader)
			{
				result.add((TableColumnHeader) node);
			}
		}

		return result;
	}

	public Set<Label> tableColumnHeaderLabels()
	{
		Set<Label> result = new HashSet<>();

		for (TableColumnHeader tableColumnHeader : tableColumnHeaders())
		{
			for (Node node : tableColumnHeader.lookupAll(LABEL.value))
			{
				if (node instanceof Label)
				{
					result.add(((Label) node));
				}
			}
		}

		return result;
	}
}