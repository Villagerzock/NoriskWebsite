package com.example.application.views.playerData;

import com.example.application.NoriskAPI.MinecraftPlayer;
import com.example.application.views.MainLayout;
import com.example.application.views.kit.Inventory;
import com.example.application.views.kit.KitView;
import com.vaadin.flow.component.Key;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.*;
import org.vaadin.lineawesome.LineAwesomeIconUrl;

@PageTitle("Player Data | HG Labor Stats")
@Route(value = "player_data", layout = MainLayout.class)
@Menu(order = 0, icon = LineAwesomeIconUrl.GLOBE_SOLID)
public class PlayerDataView extends VerticalLayout implements HasUrlParameter<String> {

    private TextField playerName;
    private Button searchButton;
    private Div playerHead = new Div();
    private Div playerStats = new Div();
    private MinecraftPlayer player;
    private VerticalLayout inventoryKit = new VerticalLayout();
    private Div currentMode = new Div();
    private void buildView(String playerName){
        this.playerName = new TextField("Playername",playerName,"");
        searchButton = new Button("Search", VaadinIcon.SEARCH.create());
        playerHead.removeAll();
        if (player != null){
            Image playerIcon = new Image("images/steve.png","Steve");
            if (player.getSkinURL() != null && player != null){
                playerIcon = new Image(player.getSkinURL(),player.getName());
            }
            playerIcon.addClassName("sprite");
            playerHead.add(playerIcon);

            playerStats.removeAll();
            if (player.getStats() == null){
                playerStats.add(new H2(player.getName() + " has never Player This GameMode"));
            }else {
                playerStats.add(player.getStats().getPlayerStatsLayout());
            }
            inventoryKit.removeAll();
            HorizontalLayout modeButtons = new HorizontalLayout();
            modeButtons.setAlignItems(Alignment.CENTER);
            Button UHC = new Button("UHC");
            currentMode.removeAll();
            UHC.addClickListener((buttonClickEvent -> {
                currentMode.add(new KitView(player, Inventory.GameMode.UHC));
            }));
            Button Soup = new Button("Soup");
            Soup.addClickListener((buttonClickEvent -> {
                currentMode.add(new KitView(player, Inventory.GameMode.Soup));
            }));
            modeButtons.add(UHC,Soup);
            inventoryKit.add(modeButtons,currentMode);
        }else {
            Image image = new Image("images/steve.png","Steve");
            image.addClassName("sprite");
            playerHead.add(image);
        }
        playerHead.addClassName("sprite-container");
        searchButton.addClickListener(e -> {
            removeAll();
            getUI().get().navigate("player_data/" + this.playerName.getValue());
            /*Div div = new Div(playerHead);
            div.addClassName("sprite-container");
            add(div);*/
        });
        searchButton.addClickShortcut(Key.ENTER);
        Div div = new Div(playerHead);
        div.addClassName("player-head-container");

        Div spacer = new Div();
        spacer.setHeight("40px");

        setMargin(true);
        setAlignItems(Alignment.CENTER);
        setHorizontalComponentAlignment(Alignment.CENTER, this.playerName, searchButton, div,currentMode,inventoryKit);
        add(spacer,div, this.playerName, searchButton, playerStats,inventoryKit);
    }
    public PlayerDataView() {
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        if (s != null){
            player = new MinecraftPlayer(s);
            buildView(s);
        }else {
            buildView("");
        }

    }
}
