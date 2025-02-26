package com.example.application.views.kit;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.awt.*;

public class InventoryLayout extends Div {
    public InventoryLayout(Inventory inventory){
        Image image = new Image("images/inventory.png","Inventory");
        image.addClassName("inventory");
        add(image,0f,0f);
        this.inventory = inventory;
        addSlot(0,48,53);
        addSlot(1,48,71);
        addSlot(2,48,89);
        addSlot(3,48,107);
        addSlot(4,117,107);
        for (int i = 0; i < 3; i++) {
            int y = 129 + i * 18;
            for (int xi = 0; xi < 9; xi++) {
                int x = 48 + i * 18;
                addSlot(i + 5,x,y);
            }
        }
    }
    private Inventory inventory;
    public void add(Component component, float xPercentage, float yPercentage){
        String top = ((yPercentage / 256f) * 100) + "%";
        String left = ((xPercentage / 256f) * 100) + "%";
        component.getStyle().set("top",top).set("left",left);
        add(component);
    }
    public void addSlot(int index,int x, int y){
        add(new Slot(inventory.get(index)),x,y);
    }
}
