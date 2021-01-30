package viewModel;

import javafx.beans.property.*;
import model.Model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ViewModel extends Observable implements Observer {
    private String setCommand = "set /controls/";
    public DoubleProperty airplaneYProp;
    public DoubleProperty elevatorProp;
    public DoubleProperty headingProp;
    public DoubleProperty markSceneXProp;
    public DoubleProperty markSceneYProp;
    public DoubleProperty airplaneXProp;
    public DoubleProperty rudderProp;
    public DoubleProperty aileronProp;
    public DoubleProperty startYProp;
    public DoubleProperty throttleProp;
    public DoubleProperty startXProp;
    public DoubleProperty offsetProp;
    public StringProperty ip;
    public StringProperty port;
    public BooleanProperty route;
    public StringProperty text;
    private Model m;
    private int chart[][];

    public void run() {
        m.execute();
    }

    public ViewModel() {
        throttleProp = new SimpleDoubleProperty();
        rudderProp = new SimpleDoubleProperty();
        aileronProp = new SimpleDoubleProperty();
        elevatorProp = new SimpleDoubleProperty();
        airplaneXProp = new SimpleDoubleProperty();
        airplaneYProp = new SimpleDoubleProperty();
        startXProp = new SimpleDoubleProperty();
        startYProp = new SimpleDoubleProperty();
        offsetProp = new SimpleDoubleProperty();
        headingProp = new SimpleDoubleProperty();
        markSceneXProp = new SimpleDoubleProperty();
        markSceneYProp = new SimpleDoubleProperty();
        text = new SimpleStringProperty();
        ip = new SimpleStringProperty();
        port = new SimpleStringProperty();
        route = new SimpleBooleanProperty();
    }

    public void setJoystick() {
        m.send(new String[]{
                setCommand + "flight/aileron " + aileronProp.getValue(),
                setCommand + "flight/elevator " + elevatorProp.getValue(),
        });
    }

    public void speed() {
        m.send(new String[]{setCommand + "engines/current-engine/throttle " + throttleProp.getValue()});
    }

    private double getLocation(String s) {
        return (Double.parseDouble(s) - startXProp.getValue() + offsetProp.getValue()) / offsetProp.getValue();
    }

    public void getSimulator() {
        m.connectManual(
                ip.getValue(),
                Integer.parseInt(port.getValue())
        );
    }

    public void playScripttext() {
        Scanner s = new Scanner(text.getValue());
        ArrayList<String> scriptArr = new ArrayList<>();

        while (s.hasNextLine() == true)
            scriptArr.add(s.nextLine());

        String[] tmp = new String[scriptArr.size()];

        for (int i = 0; i < scriptArr.size(); i++)
            tmp[i] = scriptArr.get(i);

        m.parse(tmp);
        s.close();
    }

    public void setM(Model m) {
        this.m = m;
    }

    public void getRoute(double h, double w) {

        if (!route.getValue())
            m.getp(
                    ip.getValue(),
                    Integer.parseInt(port.getValue())
            );

//        int routhX = (int)Math.round(airplaneYProp.getValue() / (-1));
//        int routhY = (int) Math.round(airplaneXProp.getValue() + (15));
//        int getX = Math.abs((int) Math.round(markSceneYProp.getValue() / h));
//        int getY = Math.abs((int) Math.round(markSceneXProp.getValue() / w));

        m.getRoute((int) (airplaneYProp.getValue() / (-1)),
                (int) (airplaneXProp.getValue() + (15)),
                Math.abs((int) (markSceneYProp.getValue() / h)),
                Math.abs((int) (markSceneXProp.getValue() / w)), chart);
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == m) {
            String[] arr = (String[]) arg;

            switch (arr[0]) {
                case "plane":
//                    airplaneXProp.setValue(getLocation(arr[1]));
//                    airplaneYProp.setValue(getLocation(arr[2]));
                    double x = Double.parseDouble(arr[1]);
                    double y = Double.parseDouble(arr[2]);
                    x = (x - startXProp.getValue() + offsetProp.getValue());
                    x /= offsetProp.getValue();
                    y = (y - startYProp.getValue() + offsetProp.getValue()) / offsetProp.getValue();
                    airplaneXProp.setValue(x);
                    airplaneYProp.setValue(y);

                    headingProp.setValue(Double.parseDouble(arr[3]));

                    setChanged();
                    notifyObservers();
                    break;
                case "route":

                    setChanged();

                    notifyObservers(arr);
                    break;
            }
        }
    }

    public void setChart(int[][] chart) {

        this.chart = chart;

        m.route(
                startXProp.getValue(),
                startYProp.doubleValue(),
                offsetProp.getValue()
        );
    }

    public void rudder() {
        m.send(new String[]{setCommand + "flight/rudder " + rudderProp.getValue()});
    }

    public void closeAP() {
        m.closeAP();
    }
}