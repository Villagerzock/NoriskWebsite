package com.example.application.NoriskAPI;

import com.google.gson.JsonObject;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springframework.boot.web.server.Http2;

import java.util.HashMap;
import java.util.Map;

public record Stats(int xp, int kills, int deaths, int currentKillStreak, int highestkillStreak, int bounty, Map<String,Map<String,Map<String, Integer>>> heroes, MinecraftPlayer player) implements IAmSerializable<Stats> {
    public float getKD(){
        return (kills() * 1f) / (deaths() * 1f);
    }
    public VerticalLayout getPlayerStatsLayout(){
        VerticalLayout layout = new VerticalLayout();
        H2 xp = new H2("XP: " + xp());
        H2 kills = new H2("Kills: " + kills());
        H2 deaths = new H2("Deaths: " + deaths());
        H2 kd = new H2("K/D: " + getKD());
        H2 currentKillStreak = new H2("Current Kill Streak: " + currentKillStreak());
        H2 highestKillStreak = new H2("Highest Kill Streak: " + highestkillStreak);
        H2 bounty = new H2("Bounty: " + bounty());
        HorizontalLayout characterSelection = new HorizontalLayout();
        characterSelection.setAlignItems(FlexComponent.Alignment.CENTER);
        Div selectedCharacter = new Div();
        selectedCharacter.removeAll();
        selectedCharacter.add(CharacterManager.characters[0].getStats(this));
        for (CharacterManager.Character character : CharacterManager.characters){
            Image image = new Image("images/" + character.icon(),character.DisplayName());
            image.addClassName("button-sprite");
            Button button = new Button(image);
            button.addClickListener((buttonClickEvent -> {
                selectedCharacter.removeAll();
                selectedCharacter.add(character.getStats(this));
            }));
            button.setWidth("128px");
            button.setHeight("128px");
            characterSelection.add(button);
        }
        layout.add(xp,kills,deaths,kd,currentKillStreak,highestKillStreak,bounty,characterSelection,selectedCharacter);
        return layout;
    }
    @Override
    public ISerializer<Stats> getSerializer() {
        return Serializer.INSTANCE;
    }
    public static class Serializer implements ISerializer<Stats>{
        public static final Serializer INSTANCE = new Serializer();
        @Override
        public Stats read(JsonObject object, MinecraftPlayer player) {
            int xp = object.get("xp").getAsInt();
            int kills = object.get("kills").getAsInt();
            int deaths = object.get("deaths").getAsInt();
            int currentKillStreak = object.get("currentKillStreak").getAsInt();
            int highestKillStreak = object.get("highestKillStreak").getAsInt();
            int bounty = object.get("bounty").getAsInt();
            Map<String,Map<String,Map<String, Integer>>> heroes = new HashMap<>();
            for (String key : object.getAsJsonObject("heroes").keySet()){
                JsonObject hero = object.getAsJsonObject("heroes").getAsJsonObject(key);
                Map<String,Map<String,Integer>> heroMap = new HashMap<>();
                for (String ability : hero.keySet()){
                    Map<String, Integer> abilityMap = new HashMap<>();
                    JsonObject abilityObject = hero.getAsJsonObject(ability);
                    for (String upgrade : abilityObject.keySet()){
                        JsonObject upgradeObject = abilityObject.getAsJsonObject(upgrade);
                        abilityMap.put(upgrade,upgradeObject.get("experiencePoints").getAsInt());
                    }
                    heroMap.put(ability,abilityMap);
                }
                heroes.put(key,heroMap);
            }
            return new Stats(xp,kills,deaths,currentKillStreak,highestKillStreak,bounty,heroes,player);
        }
    }
}
