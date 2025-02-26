package com.example.application.NoriskAPI;

import com.example.application.exceptions.PlayerNotFoundException;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import jakarta.validation.constraints.Min;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Base64;
import java.util.UUID;

public class MinecraftPlayer {
    private final String name;
    private final String id;
    private final Stats stats;
    public MinecraftPlayer(UUID uuid, JsonObject apiValue){
        String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + uuid.toString().replace("-","");
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200){
                throw new PlayerNotFoundException("Minecraft Servers do no longer recognise PlayerID");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line : reader.lines().toList()){
                response.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(response.toString(), JsonObject.class);
            id = uuid.toString().replace("-","");
            name = object.get("name").getAsString();
            this.stats = Stats.Serializer.INSTANCE.read(apiValue,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public String getPlace(){
        return "#1";
    }
    public MinecraftPlayer(String name){
        this.name = name;
        try {
            String apiUrl = "https://api.mojang.com/users/profiles/minecraft/" + name;
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            if (conn.getResponseCode() != 200){
                throw new PlayerNotFoundException("There is no Player with that name registered in Minecraft Service");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line : reader.lines().toList()){
                response.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(response.toString(), JsonObject.class);
            id = object.get("id").getAsString();

            stats = getStatsFromAPI();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Stats getStats() {
        return stats;
    }
    private Stats getStatsFromAPI(){
        String apiUrl = "https://api.hglabor.de/stats/ffa/" + fromHexToUUID(getUuid());
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200){
                new PlayerNotFoundException("This player is not Registered in the HGLabor API").printStackTrace();
                return null;
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line : reader.lines().toList()){
                response.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(response.toString(),JsonObject.class);
            return Stats.Serializer.INSTANCE.read(object,this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String fromHexToUUID(String hex){
        return hex.substring(0,8) + "-" +
                hex.substring(8,12) + "-" +
                hex.substring(12,16) + "-" +
                hex.substring(16,20) + "-" +
                hex.substring(20,32);
    }
    public String getName() {
        return name;
    }

    public String getUuid() {
        return id;
    }
    public String getFormattedUUID(){
        return fromHexToUUID(id);
    }
    public String getSkinURL(){
        String apiUrl = "https://sessionserver.mojang.com/session/minecraft/profile/" + getUuid();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200){
                throw new PlayerNotFoundException("Minecraft Servers do no longer recognise PlayerID");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line : reader.lines().toList()){
                response.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            JsonObject object = gson.fromJson(response.toString(), JsonObject.class);
            JsonArray properties = object.getAsJsonArray("properties");
            String base64Encoded = properties.get(0).getAsJsonObject().get("value").getAsString();
            String decodedJson = new String(Base64.getDecoder().decode(base64Encoded));

            JsonObject texture = gson.fromJson(decodedJson,JsonObject.class);
            return texture.getAsJsonObject("textures").getAsJsonObject("SKIN").get("url").getAsString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
