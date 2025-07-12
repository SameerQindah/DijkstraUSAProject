package application;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.geometry.Insets;
import java.io.File;
import java.util.*;

public class Main extends Application {

    private Graph graph;
    private File testFile;
    private TextArea outputArea;
    private Canvas canvas;
    private final int CANVAS_WIDTH = 800;
    private final int CANVAS_HEIGHT = 700;
    private CoordinateProjector projector;
    private TextField sourceField;
    private TextField targetField;
    private Integer selectedSource = null;
    private Integer selectedTarget = null;

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Dijkstra Map");

        // Controls
        Button loadMapButton = new Button("Load Map");
        Button loadTestButton = new Button("Load Queries");
        Button runButton = new Button("Show Path from loaded");

        sourceField = new TextField();
        targetField = new TextField();
        sourceField.setPromptText("Source ID");
        targetField.setPromptText("Target ID");

        Button goButton = new Button("Show Path from text");
        goButton.setOnAction(e -> runSingleQuery());

        outputArea = new TextArea();
        outputArea.setEditable(false);
        outputArea.setPrefWidth(400);
        outputArea.setPrefHeight(CANVAS_HEIGHT);

        canvas = new Canvas(CANVAS_WIDTH, CANVAS_HEIGHT);

        VBox controls = new VBox(10,
                loadMapButton, loadTestButton, runButton,
                new Label("Source:"), sourceField,
                new Label("Target:"), targetField, goButton);
        controls.setPadding(new Insets(10));

        HBox root = new HBox(10, controls, canvas, outputArea);
        root.setPadding(new Insets(10));

        // Events
        loadMapButton.setOnAction(e -> loadMap(primaryStage));
        loadTestButton.setOnAction(e -> loadTestFile(primaryStage));
        runButton.setOnAction(e -> runQueries());

        canvas.setOnMouseClicked(event -> {
            double mouseX = event.getX(), mouseY = event.getY();
            double minDist = Double.MAX_VALUE;
            int nearest = -1;

            for (int i = 0; i < graph.coordinates.size(); i++) {//to determine the location of the click
                double[] coord = graph.coordinates.get(i);
                if (coord == null) continue;
                double x = projector.projectX(coord[0]);
                double y = projector.projectY(coord[1]);
                double dist = Math.hypot(x - mouseX, y - mouseY);
                if (dist < minDist && dist <= 10) {
                    minDist = dist;
                    nearest = i;
                }
            }

            if (nearest != -1) {
                if (selectedSource == null) {
                    selectedSource = nearest;
                    outputArea.appendText("Selected source: " + nearest + "\n");
                } else if (selectedTarget == null) {
                    selectedTarget = nearest;
                    outputArea.appendText("Selected target: " + nearest + "\n");
                    runMouseSelectedQuery();
                } else {
                    selectedSource = nearest;
                    selectedTarget = null;
                    outputArea.appendText("Reset. New source: " + nearest + "\n");
                }
            }
        });

        primaryStage.setScene(new Scene(root));
        primaryStage.show();
    }

    private void loadMap(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open usa.txt");
        File file = fc.showOpenDialog(stage);

        if (file != null) {
            try (Scanner sc = new Scanner(file)) {
                int V = sc.nextInt(), E = sc.nextInt();
                graph = new Graph(V);

                for (int i = 0; i < V; i++) {
                    int id = sc.nextInt();
                    double x = sc.nextDouble(), y = sc.nextDouble();
                    graph.addVertex(id, x, y);
                }
                for (int i = 0; i < E; i++) {
                    graph.addEdge(sc.nextInt(), sc.nextInt());
                }

                projector = new CoordinateProjector(graph.coordinates, CANVAS_WIDTH, CANVAS_HEIGHT);
                outputArea.appendText("Map loaded: " + V + " vertices, " + E + " edges.\n");
                drawGraph(null);
            } catch (Exception ex) {
                outputArea.appendText("Error: " + ex.getMessage() + "\n");
            }
        }
    }

    private void loadTestFile(Stage stage) {
        FileChooser fc = new FileChooser();
        fc.setTitle("Open test.txt");
        testFile = fc.showOpenDialog(stage);
        if (testFile != null) outputArea.appendText("Test file loaded.\n");
    }

    private void runQueries() {
        if (graph == null || testFile == null) {
            outputArea.appendText("Please load map and test file first.\n");
            return;
        }

        try (Scanner sc = new Scanner(testFile)) {
            while (sc.hasNextInt()) {
                int source = sc.nextInt(), dest = sc.nextInt();
                if (source >= graph.V || dest >= graph.V) {
                    outputArea.appendText("Invalid query: " + source + " to " + dest + "\n");
                    continue;
                }
                runAndDraw(source, dest);
            }
        } catch (Exception e) {
            outputArea.appendText("Error reading test file: " + e.getMessage() + "\n");
        }
    }

    private void runSingleQuery() {
        try {
            int source = Integer.parseInt(sourceField.getText().trim());
            int target = Integer.parseInt(targetField.getText().trim());
            runAndDraw(source, target);
        } catch (Exception e) {
            outputArea.appendText("Please enter valid source and target IDs.\n");
        }
    }

    private void runMouseSelectedQuery() {
        if (selectedSource != null && selectedTarget != null) {
            runAndDraw(selectedSource, selectedTarget);
            selectedSource = null;
            selectedTarget = null;
        }
    }

    private void runAndDraw(int source, int target) {
        Dijkstra.compute(graph, source, target);
        double distance = Dijkstra.dist[target];
        List<Integer> path = Dijkstra.getPath(target);

        outputArea.appendText("From " + source + " to " + target + ": Distance = " + distance + "\n");
        outputArea.appendText("Path: " + path + "\n\n");

        drawGraph(path);
    }

    private void drawGraph(List<Integer> pathToHighlight) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.WHITE);
        gc.fillRect(0, 0, CANVAS_WIDTH, CANVAS_HEIGHT);

        if (graph == null || projector == null) return;

        // Draw edges
        gc.setStroke(Color.DARKGRAY);
        gc.setLineWidth(0.5);
        for (int u : graph.adj.keySet()) {
            double[] from = graph.coordinates.get(u);
            for (Node neighbor : graph.adj.get(u)) {
                double[] to = graph.coordinates.get(neighbor.id);
                if (from != null && to != null) {
                    gc.strokeLine(projector.projectX(from[0]), projector.projectY(from[1]),
                            projector.projectX(to[0]), projector.projectY(to[1]));
                }
            }
        }

        // Highlight shortest path
        if (pathToHighlight != null && pathToHighlight.size() > 1) {
            gc.setStroke(Color.RED);
            gc.setLineWidth(3);
            for (int i = 0; i < pathToHighlight.size() - 1; i++) {
                int u = pathToHighlight.get(i);
                int v = pathToHighlight.get(i + 1);
                double[] from = graph.coordinates.get(u);
                double[] to = graph.coordinates.get(v);
                if (from != null && to != null) {
                    gc.strokeLine(projector.projectX(from[0]), projector.projectY(from[1]),
                            projector.projectX(to[0]), projector.projectY(to[1]));
                }
            }
        }

        // Draw nodes
        gc.setFill(Color.LIGHTGREEN);
        for (double[] coord : graph.coordinates) {
            if (coord != null) {
                double x = projector.projectX(coord[0]);
                double y = projector.projectY(coord[1]);
                gc.fillOval(x - 1.5, y - 1.5, 3, 3);
            }
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
