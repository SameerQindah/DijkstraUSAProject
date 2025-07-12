package application;

import java.util.List;

public class CoordinateProjector {

    private double minX, maxX, minY, maxY;
    private double scaleX, scaleY;
    private double offsetX, offsetY;
    private int canvasWidth, canvasHeight;

    public CoordinateProjector(List<double[]> coordinates, int canvasWidth, int canvasHeight) {
        this.canvasWidth = canvasWidth;
        this.canvasHeight = canvasHeight;

        // Find the smallest and largest x and y values among all points
        minX = Double.MAX_VALUE;
        maxX = Double.MIN_VALUE;
        minY = Double.MAX_VALUE;
        maxY = Double.MIN_VALUE;

        for (double[] coord : coordinates) {
            if (coord == null) continue;
            minX = Math.min(minX, coord[0]);
            maxX = Math.max(maxX, coord[0]);
            minY = Math.min(minY, coord[1]);
            maxY = Math.max(maxY, coord[1]);
        }

        // Calculate how much to scale x and y so everything fits in the canvas
        double rangeX = maxX - minX;
        double rangeY = maxY - minY;

        scaleX = (canvasWidth * 0.9) / rangeX;
        scaleY = (canvasHeight * 0.9) / rangeY;

        // Use the same scale for both directions to avoid stretching
        double scale = Math.min(scaleX, scaleY);
        scaleX = scale;
        scaleY = scale;

        // Calculate offsets to center the drawing on the canvas
        offsetX = (canvasWidth - rangeX * scaleX) / 2;
        offsetY = (canvasHeight - rangeY * scaleY) / 2;
    }

    // Convert x coordinate from data to canvas coordinate
    public double projectX(double x) {
        return (x - minX) * scaleX + offsetX;
    }

    // Convert y coordinate from data to canvas coordinate (invert y-axis)
    public double projectY(double y) {
        return canvasHeight - ((y - minY) * scaleY + offsetY);
    }
}
