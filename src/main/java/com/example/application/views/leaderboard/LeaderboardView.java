package com.example.application.views.leaderboard;

import com.example.application.NoriskAPI.LeaderboardPage;
import com.example.application.NoriskAPI.MinecraftPlayer;
import com.example.application.NoriskAPI.Stats;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Route(value = "leaderboard",layout = MainLayout.class)
@PageTitle("Leaderboard | HG Labor Stats")
public class LeaderboardView extends VerticalLayout implements HasUrlParameter<Integer> {
    private static List<LeaderboardPage> lastUpdate = new ArrayList<>();
    public static void updateLeaderboard(){
        if (lastUpdate.isEmpty()){
            int page = 0;
            while (true){
                LeaderboardPage leaderboardPage = null;
                try {
                    leaderboardPage = new LeaderboardPage(page, LeaderboardPage.Sort.xp);
                } catch (IOException e) {
                    break;
                }
                lastUpdate.add(page,leaderboardPage);
                page++;
            }

            return;
        }
        for (int i = 0; i < lastUpdate.size(); i++) {
            LeaderboardPage leaderboardPage = null;
            try {
                leaderboardPage = new LeaderboardPage(i, LeaderboardPage.Sort.xp);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            lastUpdate.set(i,leaderboardPage);
        }
    }
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter Integer s) {
        Thread thread = new Thread(()->{
            int page = 0;
            if (s != null){
                page = s;
            }
            if (lastUpdate.size() <= page){
                add(new H2("Sry... we're currently Loading Data from the API, try again in 30 Seconds"));
            }else {
                update(beforeEvent,page);
            }
        });
        thread.start();
    }
    private int index = 0;
    private void update(BeforeEvent beforeEvent,int page){
        Grid<MinecraftPlayer> grid = new Grid<>(MinecraftPlayer.class,false);
        grid.setHeight("1024px");
        index = 0;
        grid.addColumn(new ValueProvider<MinecraftPlayer, Object>() {
            @Override
            public Object apply(MinecraftPlayer player) {
                index++;
                return "#" + (index + (page * 100));
            }
        });
        grid.addComponentColumn(createComponentProvide(MinecraftPlayer::getName,beforeEvent.getUI())).setHeader("Name");
        grid.addColumn(createProvider(Stats::xp)).setHeader("XP").setSortable(true);
        grid.addColumn(createProvider(Stats::kills)).setHeader("Kills").setSortable(true);
        grid.addColumn(createProvider(Stats::deaths)).setHeader("Deaths").setSortable(true);
        grid.addColumn(createProvider(Stats::currentKillStreak)).setHeader("Current Kill Streak").setSortable(true);
        grid.addColumn(createProvider(Stats::highestkillStreak)).setHeader("Highest Kill Streak").setSortable(true);
        grid.addColumn(createProvider(Stats::bounty)).setHeader("Bounty").setSortable(true);
        grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
        grid.setItems(lastUpdate.get(page).getPlayers());
        beforeEvent.getUI().access(()->{
            removeAll();
            add(grid);
            add(createNextPageButtons(page));
            beforeEvent.getUI().push();
        });
    }
    public <T> ValueProvider<MinecraftPlayer, T> createProvider(ValueProvider<Stats,T> provider){
        return (player -> {
            return provider.apply(player.getStats());
        });
    }
    public  ValueProvider<MinecraftPlayer, Component> createComponentProvide(ValueProvider<MinecraftPlayer,String> provider, UI ui){
        return (player -> {
            Span text = new Span(provider.apply(player));
            text.getStyle()
                    .set("cursor","pointer")
                    .set("text-decoration","underline");
            text.addClickListener((spanClickEvent -> {
                ui.navigate("player_data/" + player.getName());
            }));
            return text;
        });
    }
    public HorizontalLayout createNextPageButtons(int page){
        HorizontalLayout layout = new HorizontalLayout();
        Button backButton = new Button(VaadinIcon.CARET_LEFT.create());
        backButton.addClickListener(b ->{
            getUI().get().navigate("leaderboard/" + (page - 1));
        });
        Button forwardsButton = new Button(VaadinIcon.CARET_RIGHT.create());
        forwardsButton.addClickListener(b ->{
            getUI().get().navigate("leaderboard/" + (page + 1));
        });
        if (page > 0){
            layout.add(backButton);
        }
        if (page < lastUpdate.size()){
            layout.add(forwardsButton);
        }
        return layout;
    }
    public LeaderboardView(){
    }
}
