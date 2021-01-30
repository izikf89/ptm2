package view;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

public class FlightMap extends Canvas {
    double min = Double.MAX_VALUE;
    double max = 0;
	int[][] arrayMap;
    public void reCreate() {
        if(arrayMap != null) {

            double he =  getHeight() / arrayMap.length;

            double wi = getWidth() / arrayMap[0].length;

            GraphicsContext graphic = getGraphicsContext2D();

            for (int i = 0; i < arrayMap.length; i++) {
                for (int j = 0; j < arrayMap[i].length; j++) {
                    int tmp = arrayMap[i][j];
                    graphic.setFill(Color.rgb((255 - tmp), (0 + tmp), 0));
                    graphic.fillRect((j * wi), (i * he), wi, he);
                }
            }
        }
    }

    private void updateTable(int[][] arrayMap) {
        double new_max = 255;
        double new_min = 0;

        for (int i = 0; i < arrayMap.length; i++)
            for (int j = 0; j < arrayMap[i].length; j++)
                insertValue(arrayMap, new_max, new_min, i, j);
    }

    private void insertValue(int[][] arrayMap, double new_max, double new_min, int i, int j) {
        arrayMap[i][j] = (int)((arrayMap[i][j] - min) / (max - min) * (new_max - new_min) + new_min);
    }

    private void updateMinmiumMaximum(int[][] arrayMap, int i, int j) {
        if(min > arrayMap[i][j]) { min = arrayMap[i][j]; }
        if(max < arrayMap[i][j]) { max = arrayMap[i][j]; }
    }



    public void setMap(int[][] arrayMap) {
        this.arrayMap = arrayMap;

        for(int i = 0; i < arrayMap.length; i++)
            for (int j = 0; j < arrayMap[i].length; j++)
                updateMinmiumMaximum(arrayMap, i, j);

        updateTable(arrayMap);

        reCreate();
    }

}