package com.vladpopa;

import com.vladpopa.client.MainGUI;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;

import java.awt.*;

@SpringBootApplication(scanBasePackages = "com.vladpopa")
public class TrainApp {
    public static void main(String[] args) {
        var context = new SpringApplicationBuilder(TrainApp.class)
                .headless(false)
                .run(args);

        EventQueue.invokeLater(() -> {
            MainGUI gui = context.getBean(MainGUI.class);
            gui.init();
        });
    }
}
