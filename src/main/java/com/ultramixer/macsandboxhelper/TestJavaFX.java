package com.ultramixer.macsandboxhelper;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;

/**
 * Created by TB on 06.12.16.
 */
public class TestJavaFX extends Application
{
    @Override
    public void start(Stage primaryStage) throws Exception
    {
        Button b = new Button("Grant Access");
        b.setOnAction(new EventHandler<ActionEvent>()
        {
            @Override
            public void handle(ActionEvent event)
            {
                grantAccess();
            }
        });
        StackPane root = new StackPane(b);
        Scene scene = new Scene(root, 800, 600);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void grantAccess()
    {
        List<File> files = MacSandBoxHelper.NSOpenPanel_openPanel_runModal_javafx("Title", false, true, false, null);
        System.out.println("files = " + files);

        File dir = files.get(0);
        System.out.println("dir.getAbsolutePath() = " + dir.getAbsolutePath());


        String bookmark = MacSandBoxHelper.NSURL_bookmarkDataWithOptions(dir.getAbsolutePath());
        System.out.println("bookmark = " + bookmark);

        Object url = MacSandBoxHelper.NSURL_URLByResolvingBookmarkData_startAccessingSecurityScopedResource(bookmark);
        System.out.println("url = " + url);

        boolean isLockedFolder = MacSandBoxHelper.isLockedFolder(dir);
        System.out.println("isLockedFolder = " + isLockedFolder);

        //File dir = new File("/");
        File[] dirFiles = dir.listFiles();
        for (File dirFile : dirFiles)
        {
            System.out.println("dirFile = " + dirFile);
        }
    }
}
