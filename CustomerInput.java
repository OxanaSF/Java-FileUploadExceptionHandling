import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import javafx.application.*;
import javafx.event.*;
import javafx.geometry.Pos;
import javafx.scene.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.text.*;
import javafx.stage.*;

public class CustomerInput extends Application {

    private Stage primaryStage;
    private Text statusText, resultText;
    private Button uploadButton;

    private final static Font RESULT_FONT = Font.font("Helvetica", 24);
    private final static Font INPUT_FONT = Font.font("Helvetica", 20);

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        VBox primaryBox = new VBox();
        primaryBox.setAlignment(Pos.CENTER);
        primaryBox.setSpacing(20);
        primaryBox.setStyle("-fx-background-color: white");

        VBox uploadBox = new VBox();
        uploadBox.setAlignment(Pos.CENTER);
        uploadBox.setSpacing(20);
        Text uploadLabel = new Text("Upload a comma-separated file with customer data.");
        uploadLabel.setFont(INPUT_FONT);
        uploadButton = new Button("Upload data");
        uploadButton.setOnAction(this::processDataUpload);

        uploadBox.getChildren().add(uploadLabel);
        uploadBox.getChildren().add(uploadButton);
        primaryBox.getChildren().add(uploadBox);

        VBox resultsBox = new VBox();
        resultsBox.setAlignment(Pos.CENTER);
        resultsBox.setSpacing(20);
        statusText = new Text("");
        statusText.setVisible(false);
        statusText.setFont(RESULT_FONT);
        statusText.setFill(Color.RED);
        resultText = new Text("");
        resultText.setVisible(false);
        resultText.setFont(RESULT_FONT);
        resultsBox.getChildren().add(statusText);
        resultsBox.getChildren().add(resultText);
        primaryBox.getChildren().add(resultsBox);

        Scene scene = new Scene(primaryBox, 475, 200, Color.TRANSPARENT);
        primaryStage.setTitle("Customer Data Upload");
        primaryStage.setScene(scene);
        primaryStage.setResizable(true);
        primaryStage.show();
    }

    private void processDataUpload(ActionEvent event) {
        statusText.setVisible(false);
        resultText.setVisible(false);
        FileChooser fileChooser = new FileChooser();
        FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);
        File file = fileChooser.showOpenDialog(primaryStage);
        parseFile(file);

    }

    private void parseFile(File file) {
        String message = "";
        String id = "";
        String numberOfOrdersString = "";
        try {
            Path path = file.toPath();
            List<String> lines = Files.readAllLines(path);
            List<Customer> customers = new ArrayList<>();

            for (String line : lines) {
                String[] data = line.split(",");
                id = data[0].trim();

                for (int i = 0; i < id.length(); i++) {
                    char c = id.charAt(i);
                    if (c == '@') throw new InvalidCharacterException();
                }

                numberOfOrdersString = data[1].trim();
                int numberOfOrders = Integer.parseInt(numberOfOrdersString);

                Customer customer = new Customer(id, numberOfOrders);
                customers.add(customer);
            }

            message = customers.size() + (customers.size() > 1 ? " customers" : " customer") + " were successfully created!";

            resultText.setText("Total number of orders across all customers is " + getTotalNumberOfOrders(customers) + ".");
            resultText.setVisible(true);
            uploadButton.setDisable(true);


        } catch (IOException ex) {
            message = ex.getMessage();
        } catch (NullPointerException ex) {
            message = "You must choose a file!";
        } catch (NumberFormatException ex) {
            message = "The customer's number of orders " + numberOfOrdersString + " must be integer!";
        } catch (InvalidCharacterException ex) {
            message = "The customer ID " + "'" + id + "'" + " " + ex.getMessage() + "!";
        } finally {
            statusText.setText(message);
            statusText.setVisible(true);
        }

    }

    public static void main(String[] args) {
        launch(args);
    }

    private int getTotalNumberOfOrders(List<Customer> customers) {
        int totalNumberOfOrders = 0;
        for (Customer customer : customers) {
            totalNumberOfOrders += customer.getNumberOfOrders();
        }
        return totalNumberOfOrders;
    }


}