package com.example.application.views.helloworld;

import com.example.application.NoriskAPI.MinecraftPlayer;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Menu;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

import java.util.HashSet;

@PageTitle("Hello World")
@Route("")
@Menu(order = 0, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class HelloWorldView extends VerticalLayout {

    private TextField name;
    private Button sayHello;
    private Div playerHead = new Div();
    private Div playerStats = new Div();
    public HelloWorldView() {
        name = new TextField("Playername");
        sayHello = new Button("Search");
        playerHead.removeAll();
        Image image = new Image("images/steve.png","Steve");
        image.addClassName("sprite");
        playerHead.add(image);
        playerHead.addClassName("sprite-container");
        sayHello.addClickListener(e -> {
            MinecraftPlayer player = new MinecraftPlayer(name.getValue());
            playerHead.removeAll();
            Image playerIcon = new Image("images/steve.png","Steve");
            if (player.getSkinURL() != null && player != null){
                playerIcon = new Image(player.getSkinURL(),player.getName());
            }
            System.out.println(player.getFormattedUUID());
            playerIcon.addClassName("sprite");
            playerHead.add(playerIcon);

            playerStats.removeAll();
            if (player.getStats() == null){
                playerStats.add(new H2(player.getName() + " has never Player This GameMode"));
            }else {
                playerStats.add(player.getStats().getPlayerStatsLayout());
            }
            /*Div div = new Div(playerHead);
            div.addClassName("sprite-container");
            add(div);*/
        });
        sayHello.addClickShortcut(Key.ENTER);
        Div div = new Div(playerHead);
        div.addClassName("player-head-container");

        Div spacer = new Div();
        spacer.setHeight("40px");

        setMargin(true);
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER, name, sayHello, div);
        add(spacer,div, name, sayHello, playerStats);
    }

}
