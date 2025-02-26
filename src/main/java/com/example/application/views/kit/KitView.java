package com.example.application.views.kit;

import com.example.application.NoriskAPI.MinecraftPlayer;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route(value = "kit",layout = MainLayout.class)
public class KitView extends VerticalLayout {
    public KitView(MinecraftPlayer player, Inventory.GameMode mode){
        InventoryLayout layout = new InventoryLayout(Inventory.loadFromAPI(player,mode));
        layout.addClassName("inventory");
        setAlignItems(Alignment.CENTER);
        add(layout);
    }
}
