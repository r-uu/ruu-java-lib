package de.ruu.lib.fx.control;

import de.ruu.lib.fx.css.FontWeight;
import de.ruu.lib.fx.css.TableViewUtil;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import lombok.NonNull;

public class TableViewConfig
{
	@NonNull private TableView<?>  tableView;
	@NonNull private TableViewUtil tableViewUtil;

	public TableViewConfig(@NonNull TableView<?> tableView)
	{
		this.tableView = tableView;
		tableViewUtil = new TableViewUtil(tableView);
	}

	public void setColumnHeaderFontWeight(FontWeight fontWeight)
	{
		for (Label tableColumnHeaderLabel : tableViewUtil.tableColumnHeaderLabels())
		{
			tableColumnHeaderLabel.setStyle("-fx-font-weight: normal");
		}
	}
}