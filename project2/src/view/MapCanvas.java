package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class MapCanvas extends Canvas {
    double max = 0;
    double min = Double.MAX_VALUE;
    int[][] map;

    public void redraw() {
        if (map == null) return;

        GraphicsContext GC = getGraphicsContext2D();
        double wi = getWidth() / map[0].length;
        double he = getHeight() / map.length;


        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++) {

                int tmp = map[i][j];
                GC.setFill(Color.rgb((255 - tmp), (0 + tmp), 0));

                GC.fillRect((j * wi), (i * he), wi, he);
            }

    }

    public void setMapData(int[][] map) {
        double newMin = 0;
        double newMax = 255;
        this.map = map;

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++) {
                if (min > map[i][j]) min = map[i][j];
                if (max < map[i][j]) max = map[i][j];
            }

        for (int i = 0; i < map.length; i++)
            for (int j = 0; j < map[i].length; j++)
                map[i][j] = (int) ((map[i][j] - min) / (max - min) * (newMax - newMin) + newMin);

        redraw();
    }
}