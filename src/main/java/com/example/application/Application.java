package com.example.application;

import com.example.application.views.leaderboard.LeaderboardView;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.server.AppShellSettings;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * The entry point of the Spring Boot application.
 *
 * Use the @PWA annotation make the application installable on phones, tablets
 * and some desktop browsers.
 *
 */
@SpringBootApplication
@Theme(value = "noriskwebsite",variant = Lumo.DARK)
public class Application implements AppShellConfigurator {
    @Override
    public void configurePage(AppShellSettings settings) {
        settings.addFavIcon("icon","icons/icon.png","256x256");
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
        Thread updateThread = new Thread(()->{
            new Timer().scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    System.out.println("Updating Leaderboard");
                    LeaderboardView.updateLeaderboard();
                }
            }, 0, 1200000);
        });
        updateThread.run();

    }
}
