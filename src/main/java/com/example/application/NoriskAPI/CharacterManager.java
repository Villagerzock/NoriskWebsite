package com.example.application.NoriskAPI;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class CharacterManager {
    public static final Character[] characters = new Character[]{new Character("Aang","aang.png","aang"),new Character("Katara","katara.png","katara"),new Character("Toph","toph.png","toph")};
    public static final JsonObject keyReplacements;

    static {
        try (InputStream inputStream = CharacterManager.class.getResourceAsStream("/replacements.json");
             Reader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)){
            keyReplacements = new Gson().fromJson(reader, JsonObject.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public record Character(String DisplayName,String icon, String apiName){
        public VerticalLayout getStats(Stats stats){
            VerticalLayout verticalLayout = new VerticalLayout();
            Map<String, Map<String, Integer>> hero = stats.heroes().get(apiName);
            if (hero == null){
                verticalLayout.add(new H2("This hero was not played with yet"));
                return verticalLayout;
            }
            JsonObject object = keyReplacements;
            hero.forEach((s, map) -> {
                String replacement = s;
                if (object.has(s))
                    replacement = object.get(s).getAsString();
                H2 ability = new H2(replacement + ":");
                verticalLayout.add(ability);
                map.forEach((s1, integer) -> {
                    String rep = s1;
                    if (object.has(s1))
                        rep = object.get(s1).getAsString();
                    HorizontalLayout layout = new HorizontalLayout();
                    Div spacer = new Div();
                    spacer.setWidth("20px");
                    H2 text = new H2(rep + ": " + integer);
                    layout.add(spacer,text);
                    verticalLayout.add(layout);
                });
            });
            return verticalLayout;
        }
    }
}
