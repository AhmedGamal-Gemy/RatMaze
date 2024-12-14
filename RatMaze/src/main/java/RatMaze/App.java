package RatMaze;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class App extends Application {

    // some important declartions
    private GridPane gridPane;
    private int mazeSize;
    private Rectangle[][] cells;
    private int[][] maze;
    private ExecutorService executor = Executors.newFixedThreadPool(2);

    public void initializeMaze(int size) {

        maze = new int[size][size];

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                maze[i][j] = 1;
            }
        }
        cells = new Rectangle[size][size];

    }

    @Override
    public void start(Stage primaryStage) {

        Label sizeLabel = new Label("Enter Maze Size:");
        TextField sizeField = new TextField();
        Button generateButton = new Button("Generate the Maze");
        Button SolveButton = new Button("Solve the Maze");

        gridPane = new GridPane();

        generateButton.setOnAction(event -> {

            mazeSize = Integer.parseInt(sizeField.getText());
            initializeMaze(mazeSize);

            for (int i = 0; i < mazeSize; i++) {

                for (int j = 0; j < mazeSize; j++) {

                    Rectangle cell = new Rectangle(20, 20);
                    cell.setFill(Color.WHITE);

                    final int row_i = i;
                    final int col_j = j;
                    cell.setOnMouseClicked(event1 -> {

                        if (cell.getFill() == Color.WHITE) {  // if it's white the user clicked it then it becomes black ( wall )

                            cell.setFill(Color.BLACK);
                            maze[row_i][col_j] = 0;

                        } else {
                            cell.setFill(Color.WHITE);
                            maze[row_i][col_j] = 1;
                        }

                    });
                    cells[i][j] = cell;
                    gridPane.add(cell, i, j);

                }

            }

        });

        SolveButton.setOnAction(event2 -> {

            try {
                solveMaze(0, 0, Color.GREEN);

            } catch (NumberFormatException e) {
                // Handle invalid input
                System.out.println("Invalid input. Please enter a valid integer.");
            }

        });

        VBox vbox = new VBox(10);
        vbox.setPadding(new Insets(10));
        vbox.getChildren().addAll(sizeLabel, sizeField, generateButton, SolveButton, gridPane);

        Scene scene = new Scene(vbox, 300, 300);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void solveMaze(int x, int y, Color color) {
        if (x == mazeSize - 1 && y == mazeSize - 1) {
            return; // Reached the destination
        }

        if (x >= 0 && y >= 0 && x < mazeSize && y < mazeSize && maze[x][y] == 1) {
            synchronized (this) {
                maze[x][y] = 2; // Mark as visited
            }
            Platform.runLater(() -> {
                cells[x][y].setFill(color);
            });

            try {
                Thread.sleep(200); // Adjust the delay as needed
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            if (x + 1 < mazeSize && maze[x + 1][y] == 1) {
                executor.submit(() -> solveMaze(x + 1, y, Color.RED));
            }
            if (y + 1 < mazeSize && maze[x][y + 1] == 1) {
                executor.submit(() -> solveMaze(x, y + 1, Color.YELLOW));
            }

        }
        if (x == mazeSize && y == mazeSize) {
            executor.shutdown();
        }
        if(maze[x][y] == 1){
            executor.shutdown();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
