package sample;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;


public class Main extends Application {

    private String imageUrl;
    private File file;
    private final ImageView pic = new ImageView();
    private final BorderPane pane = new BorderPane();
    private Image img;
    private BufferedImage buffImg;
    BufferedImage noisedImg;

    @Override
    public void start(Stage primaryStage) {
        //Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("GlitchITBase64");

        GridPane grid = new GridPane();
        grid.setPadding(new Insets(10, 10, 10, 10));
        grid.setVgap(10);
        grid.setHgap(10);
        grid.setAlignment(Pos.TOP_CENTER);

        BorderPane border = new BorderPane();
        HBox hbox = new HBox();
        hbox.setPadding(new Insets(15, 12, 15, 12));
        hbox.setSpacing(10);
        border.setTop(grid);
        border.setCenter(pane);

        final Label errorCaption = new Label("Number of Errors:");
        final Label iterationCaption = new Label("Number of Iterations:");
        final Label noiseIntCaption = new Label("Noise Intensity:");
        GridPane.setConstraints(errorCaption, 0, 0);
        GridPane.setConstraints(iterationCaption, 0, 1);
        GridPane.setConstraints(noiseIntCaption, 0, 2);

        Slider errorSlider = new Slider();
        errorSlider.setMin(0);
        errorSlider.setMax(1000);
        errorSlider.setValue(100);
        errorSlider.setBlockIncrement(10);

        Slider iterationSlider = new Slider();
        iterationSlider.setMin(0);
        iterationSlider.setMax(20);
        iterationSlider.setValue(5);
        iterationSlider.setBlockIncrement(10);

        Slider noiseIntSlider = new Slider();
        noiseIntSlider.setMin(10);
        noiseIntSlider.setMax(20000);
        noiseIntSlider.setValue(1000);
        noiseIntSlider.setBlockIncrement(10);

        GridPane.setConstraints(errorSlider, 1, 0);
        GridPane.setConstraints(iterationSlider, 1, 1);
        GridPane.setConstraints(noiseIntSlider, 1, 2);
        final Label errorValue = new Label(Double.toString(errorSlider.getValue()));
        final Label iterationValue = new Label(Double.toString(iterationSlider.getValue()));
        final Label noiseIntValue = new Label(Double.toString(noiseIntSlider.getValue()));
        GridPane.setConstraints(errorValue, 2, 0);
        GridPane.setConstraints(iterationValue, 2, 1);
        GridPane.setConstraints(noiseIntValue, 2, 2);

        errorSlider.valueProperty().addListener((ov, old_val, new_val)
                -> errorValue.setText(String.format("%.2f", new_val)));

        iterationSlider.valueProperty().addListener((ov, old_val, new_val)
                -> iterationValue.setText(String.format("%.2f", new_val)));

        noiseIntSlider.valueProperty().addListener((ov, old_val, new_val)
                -> noiseIntValue.setText(String.format("%.2f", new_val)));

        Button glitchButton = new Button("Glitch");
        GridPane.setConstraints(glitchButton, 4, 0);
        glitchButton.setOnAction(e -> {
                    if (img == null) {
                        return;
                    }
                    BufferedImage glitchedImg = Glitch.execute(file, (int) iterationSlider.getValue(), (int) errorSlider.getValue());
                    img = SwingFXUtils.toFXImage(glitchedImg, null);
                    buffImg = glitchedImg;
                    updateImg(primaryStage);
                }
        );

        final FileChooser fileChooser = new FileChooser();
        final Button openButton = new Button("Open an Image");
        GridPane.setConstraints(openButton, 3, 0);
        openButton.setOnAction(e -> {
            configureFileChooser(fileChooser);
            file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                try {
                    imageUrl = file.toURI().toURL().toExternalForm();
                } catch (MalformedURLException ex) {
                    ex.printStackTrace();
                }
                img = new Image(imageUrl);
                updateImg(primaryStage);
                try {
                    buffImg = ImageIO.read(file);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        });

        Button saveButton = new Button("Save Image");
        GridPane.setConstraints(saveButton, 3, 1);
        saveButton.setOnAction(e -> {
            if (img == null) {
                return;
            }
            saveToFile(primaryStage);
        });

        Button helpButton = new Button("Help");
        GridPane.setConstraints(helpButton, 4, 1);
        helpButton.setOnAction(e -> {
            final Stage dialog = new Stage();
            dialog.initModality(Modality.APPLICATION_MODAL);
            dialog.initOwner(primaryStage);
            VBox dialogVbox = new VBox(20);
            dialogVbox.getChildren().add(new Text("This small application is used for randomized glitching of jpg images through base64. \nImage is encoded into string, we mess up the string and decode it back to image. \nThe glitched image will be saved as PNG. \n\n" +
                    "You can see three sliders: Errors, Iterations and Noise Intensity. \n\n" +
                    "Errors slider specify maximum number of errors that will affect the image's text representation. \n\n" +
                    "Iterations slider specify how many times will the algorithm be executed. \n\n" +
                    "Basically the less errors and the less iterations you'll choose the less damaged will the result be. \n" +
                    "However the results are always randomized so you wont get the same picture twice even when setting or keeping the same values.\n" +
                    "High values will result in almost fully broken image with most part of it being solid grey.\n\n" +
                    "NoiseON and NoiseOFF will create/remove a random colored noise on image.\n" +
                    "Noise Intensity slider will let you choose the intensity of graining when you click NoiseON."));
            Scene dialogScene = new Scene(dialogVbox, 695, 260);
            dialog.setScene(dialogScene);
            dialog.show();
        });

        Button noiseONButton = new Button("NoiseON");
        GridPane.setConstraints(noiseONButton, 3, 2);
        noiseONButton.setOnAction(e -> {
            if (img == null) {
                return;
            }
            noisedImg = Noise.execute(buffImg, (int) noiseIntSlider.getValue());
            img = SwingFXUtils.toFXImage(noisedImg, null);
            updateImg(primaryStage);
        });

        Button noiseOFFButton = new Button("NoiseOFF");
        GridPane.setConstraints(noiseOFFButton, 4, 2);
        noiseOFFButton.setOnAction(e -> {
            if (img == null) {
                return;
            }
            img = SwingFXUtils.toFXImage(buffImg, null);
            noisedImg = null;
            updateImg(primaryStage);
        });

        grid.getChildren().addAll(errorCaption, iterationCaption, noiseIntCaption,
                errorSlider, iterationSlider, noiseIntSlider,
                errorValue, iterationValue, noiseIntValue,
                glitchButton, openButton, saveButton, helpButton, noiseONButton, noiseOFFButton);

        Scene scene = new Scene(border, 650, 700);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void saveToFile(Stage stage) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save Image");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
        File file = fileChooser.showSaveDialog(stage.getScene().getWindow());
        if (file != null) {
            try {
                if (noisedImg != null) {
                    ImageIO.write(noisedImg, "png", file);
                } else {
                    ImageIO.write(buffImg, "png", file);
                }
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    private static void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("JPG", "*.jpg"));
    }

    private void updateImg(Stage stage) {
        pic.setImage(img);
        pic.setFitWidth(stage.getWidth());
        pic.setFitHeight(stage.getHeight());
        pic.setPreserveRatio(true);
        pane.setCenter(pic);
    }
}
