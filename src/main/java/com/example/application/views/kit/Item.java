package com.example.application.views.kit;

import com.example.application.NoriskAPI.IAmSerializable;
import com.example.application.NoriskAPI.ISerializer;
import com.example.application.NoriskAPI.MinecraftPlayer;
import com.google.gson.JsonObject;

public class Item {
    public Item(String type, int amount) {
        this.type = type.replaceFirst("minecraft:","");
        this.amount = amount;
    }
    public String getType() {
        return type;
    }

    public int getAmount() {
        return amount;
    }
    private final String type;
    private final int amount;

    public static Item serialize(JsonObject item){
        int amount = item.get("count").getAsInt();
        String type = item.get("id").getAsString();
        return new Item(type,amount);
    }
}
