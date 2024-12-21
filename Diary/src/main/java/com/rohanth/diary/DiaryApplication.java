package com.rohanth.diary;
import com.rohanth.diary.gui.LoginFrame;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import javax.swing.SwingUtilities;

@SpringBootApplication
public class DiaryApplication {
    public static void main(String[] args) {
        var ctx = new SpringApplicationBuilder(DiaryApplication.class)
                .headless(false)
                .run(args);

        SwingUtilities.invokeLater(() -> {
            var loginFrame = ctx.getBean(LoginFrame.class);
            loginFrame.setVisible(true);


        });
    }
}