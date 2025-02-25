package com.example.application.NoriskAPI;

import com.example.application.exceptions.LeaderboardNotFound;
import com.example.application.exceptions.PlayerNotFoundException;
import com.example.application.views.leaderboard.LeaderboardView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class LeaderboardPage {
    public enum Sort{
        xp,
        kills,
        deaths,
        currentKillStreak,
        highestKillStreak,
        bounty;
    }

    public List<MinecraftPlayer> getPlayers() {
        return players;
    }

    private List<MinecraftPlayer> players;
    public LeaderboardPage(int page, Sort sort){
        String apiUrl = "https://api.hglabor.de/stats/ffa/" + "top?page="+page+"&sort="+sort.name();
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            if (connection.getResponseCode() != 200){
                throw new LeaderboardNotFound("This page does not exist on the Leaderboard");
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            for (String line : reader.lines().toList()){
                response.append(line);
            }
            reader.close();
            Gson gson = new Gson();
            JsonArray array = gson.fromJson(response.toString(), JsonArray.class);
            players = new ArrayList<>();
            for (JsonElement element : array){
                JsonObject object = element.getAsJsonObject();
                UUID uuid = UUID.fromString(object.get("playerId").getAsString());
                players.add(new MinecraftPlayer(uuid));
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
