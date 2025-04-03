package com.example.ael.Utility;

import com.example.ael.DataModels.OrderDataModel;
import com.example.ael.Main;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.TableCell;

public abstract class HyperlinkViewCell<T> extends TableCell<T, Void> {
    protected final Hyperlink link;

    protected HyperlinkViewCell(String screenName) {
        link = new Hyperlink("VIEW");
        link.setVisited(true);
        link.setOnAction(e -> {
            String dataToSend = getDataToSend();
            Main.setPersistentData(dataToSend);
            Main.popUpModalScreen(screenName);
        });
    }

    abstract protected String getDataToSend();

    @Override
    protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : link);
    }
}


