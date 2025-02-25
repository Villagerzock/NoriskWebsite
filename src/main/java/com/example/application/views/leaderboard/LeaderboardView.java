package com.example.application.views.leaderboard;

import com.example.application.NoriskAPI.LeaderboardPage;
import com.example.application.NoriskAPI.MinecraftPlayer;
import com.example.application.NoriskAPI.Stats;
import com.example.application.views.MainLayout;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.page.Push;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.function.ValueProvider;
import com.vaadin.flow.router.*;
import com.vaadin.flow.theme.lumo.LumoUtility;
import jakarta.validation.constraints.Min;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@Route(value = "leaderboard",layout = MainLayout.class)
@PageTitle("Leaderboard | HG Labor Stats")
public class LeaderboardView extends VerticalLayout implements HasUrlParameter<String> {
    @Override
    public void setParameter(BeforeEvent beforeEvent, @OptionalParameter String s) {
        H2 loadingText = new H2("Getting data from API");
        setAlignItems(Alignment.CENTER);
        add(loadingText);
        Thread thread = new Thread(()->{
            Location location = beforeEvent.getLocation();
            QueryParameters queryParameters = location.getQueryParameters();

            Map<String, List<String>> parametersMap  = queryParameters
                    .getParameters();
            int page = 0;
            LeaderboardPage.Sort sort = LeaderboardPage.Sort.xp;
            if (parametersMap.containsKey("page") && parametersMap.containsKey("sort")){
                page = Integer.parseInt(parametersMap.get("page").getFirst());
                sort = LeaderboardPage.Sort.valueOf(parametersMap.get("sort").getFirst());
            }

            LeaderboardPage leaderboardPage = new LeaderboardPage(page,sort);
            System.out.println("Got API Data");
            Grid<MinecraftPlayer> grid = new Grid<>(MinecraftPlayer.class,false);
            grid.addComponentColumn(createComponentProvide(MinecraftPlayer::getName,beforeEvent.getUI())).setHeader("Name");
            grid.addColumn(createProvider(Stats::xp)).setHeader(createSortButton("XP", LeaderboardPage.Sort.xp));
            grid.addColumn(createProvider(Stats::kills)).setHeader(createSortButton("Kills", LeaderboardPage.Sort.kills));
            grid.addColumn(createProvider(Stats::deaths)).setHeader(createSortButton("Deaths", LeaderboardPage.Sort.deaths));
            grid.addColumn(createProvider(Stats::currentKillStreak)).setHeader(createSortButton("Current Kill Streak", LeaderboardPage.Sort.currentKillStreak));
            grid.addColumn(createProvider(Stats::highestkillStreak)).setHeader(createSortButton("Highest Kill Streak", LeaderboardPage.Sort.highestKillStreak));
            grid.addColumn(createProvider(Stats::bounty)).setHeader(createSortButton("Bounty", LeaderboardPage.Sort.bounty));
            grid.addThemeVariants(GridVariant.LUMO_ROW_STRIPES);
            grid.setItems(leaderboardPage.getPlayers());
            beforeEvent.getUI().access(()->{
                removeAll();
                add(grid);

                beforeEvent.getUI().push();
            });
        });
        thread.start();
    }
    public <T> ValueProvider<MinecraftPlayer, T> createProvider(ValueProvider<Stats,T> provider){
        return (player -> {
            return provider.apply(player.getStats());
        });
    }
    public  ValueProvider<MinecraftPlayer, Span> createComponentProvide(ValueProvider<MinecraftPlayer,String> provider,UI ui){
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
    public Button createSortButton(String text, LeaderboardPage.Sort option){
        Button button = new Button(text, VaadinIcon.CARET_DOWN.create());
        button.addClickListener((buttonClickEvent -> {
            getUI().get().getPage().getHistory().pushState(null,"leaderboard?page=" + currentPage + "&sort=" + option.name());
        }));
        return button;
    }
    private int currentPage = 0;
    private LeaderboardPage.Sort currentSort = LeaderboardPage.Sort.xp;
    public LeaderboardView(){
    }
}
