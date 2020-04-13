package lerkeveld.telsysteem.countSystem.supportElements;

import javafx.scene.control.ContextMenu;

// a contextMenu that forgets all its items when hiding
public class GoldfishContextMenu extends ContextMenu {
    public GoldfishContextMenu(){
        super();
    }

    @Override
    public void hide(){
        super.hide();
        getItems().clear();
    }
}
