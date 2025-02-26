package com.example.application.views.kit;


import com.example.application.NoriskAPI.MinecraftPlayer;
import com.example.application.exceptions.PlayerNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import io.swagger.v3.oas.models.security.SecurityScheme;
import jakarta.validation.constraints.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;

public class Inventory implements Iterable<Item> {
    private List<Item> items = empty();
    private List<Item> empty(){
        Item[] result = new Item[41];
        for (int i = 0; i < result.length; i++) {
            result[i] = new Item("air",0);
        }
        return Arrays.stream(result).toList();
    }
    @Override
    public Iterator<Item> iterator() {
        return items.iterator();
    }
    public void set(int index,Item item){

    }
    public Item get(int index){
        return items.get(index);
    }
    public enum GameMode{
        UHC,
        Soup;
    }
    public static Inventory loadFromAPI(MinecraftPlayer player,GameMode mode){
        String apiUrl = "https://api.hglabor.de/ffa/inventory/" + player.getFormattedUUID() + "/" + mode.name();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200){
                throw new PlayerNotFoundException("Player does not have a Registered Kit");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder respone = new StringBuilder();
             for (String line : reader.lines().toList()){
                 System.out.println("Reading Line for Inventory: " + line);
                 respone.append(line.replace("\n", "").replace(" ",""));
             }
             reader.close();
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(respone.toString(),JsonObject.class);
            return serialize(jsonObject);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static Inventory serialize(JsonObject object){
        Inventory inventory = new Inventory();
        JsonArray armor = object.getAsJsonArray("armor");
        for (int i = 0; i < 4; i++) {
            inventory.set(i,serializeItem(armor.get(i)));
        }
        JsonElement offhand = object.getAsJsonArray("offhand").get(0);
        inventory.set(4,serializeItem(offhand));
        JsonArray main = object.getAsJsonArray("main");
        for (int i = 0; i < 36; i++) {
            inventory.set(i + 5,serializeItem(main.get(i + 5)));
        }
        return inventory;
    }
    private static Item serializeItem(JsonElement element){
        if (element.getAsString().equals("EMPTY")){
            return new Item("air",0);
        }else {
            return Item.serialize(element.getAsJsonObject());
        }
    }
}
