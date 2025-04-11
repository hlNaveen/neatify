import javafx.application.Application;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class SmartFileOrganizer extends Application {

    private TextArea logArea;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Smart File Organizer");

        Button selectFolderBtn = new Button("Select Folder");
        Button organizeBtn = new Button("Organize Files");
        logArea = new TextArea();
        logArea.setEditable(false);

        Label folderLabel = new Label("No folder selected.");
        File[] selectedFolder = new File[1];

        selectFolderBtn.setOnAction(e -> {
            DirectoryChooser chooser = new DirectoryChooser();
            chooser.setTitle("Select Folder to Organize");
            File folder = chooser.showDialog(primaryStage);
            if (folder != null) {
                selectedFolder[0] = folder;
                folderLabel.setText("Selected: " + folder.getAbsolutePath());
            }
        });

        organizeBtn.setOnAction(e -> {
            if (selectedFolder[0] != null) {
                organizeFiles(selectedFolder[0]);
            } else {
                log("Please select a folder first.");
            }
        });

        VBox layout = new VBox(10);
        layout.setPadding(new Insets(10));
        layout.getChildren().addAll(selectFolderBtn, folderLabel, organizeBtn, logArea);

        Scene scene = new Scene(layout, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void organizeFiles(File folder) {
        log("Organizing files in: " + folder.getAbsolutePath());

        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            log("No files to organize.");
            return;
        }

        Map<String, String> extensionMap = getExtensionCategoryMap();

        for (File file : files) {
            if (file.isFile()) {
                String ext = getFileExtension(file.getName());
                String category = extensionMap.getOrDefault(ext, "Others");
                Path targetDir = Paths.get(folder.getAbsolutePath(), category);
                Path targetPath = targetDir.resolve(file.getName());

                try {
                    Files.createDirectories(targetDir);
                    Files.move(file.toPath(), targetPath, StandardCopyOption.REPLACE_EXISTING);
                    log("Moved: " + file.getName() + " → " + category);
                } catch (IOException e) {
                    log("Failed to move: " + file.getName() + " - " + e.getMessage());
                }
            }
        }
        log("Organizing completed.");
    }

    private Map<String, String> getExtensionCategoryMap() {
        Map<String, String> map = new HashMap<>();
        map.put("jpg", "Images");
        map.put("jpeg", "Images");
        map.put("png", "Images");
        map.put("gif", "Images");
        map.put("bmp", "Images");
        map.put("doc", "Documents");
        map.put("docx", "Documents");
        map.put("pdf", "Documents");
        map.put("txt", "Documents");
        map.put("xls", "Documents");
        map.put("xlsx", "Documents");
        map.put("mp4", "Videos");
        map.put("mov", "Videos");
        map.put("avi", "Videos");
        map.put("mp3", "Audio");
        map.put("wav", "Audio");
        map.put("zip", "Archives");
        map.put("rar", "Archives");
        map.put("7z", "Archives");
        return map;
    }

    private String getFileExtension(String fileName) {
        int index = fileName.lastIndexOf('.');
        if (index > 0 && index < fileName.length() - 1) {
            return fileName.substring(index + 1).toLowerCase();
        }
        return "";
    }

    private void log(String message) {
        logArea.appendText(message + "\n");
    }
} 
