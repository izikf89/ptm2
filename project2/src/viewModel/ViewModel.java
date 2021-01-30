package viewModel;

import javafx.beans.property.*;
import model.Model;

import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.Scanner;

public class ViewModel extends Observable implements Observer {
    private int map[][];
    private Model modelRef;
    public DoubleProperty propRudder;
    public DoubleProperty propAileron;
    public DoubleProperty propStartY;
    public DoubleProperty propElevator;
    public DoubleProperty propHeading;
    public DoubleProperty propMarkSceneX;
    public DoubleProperty propAirplaneY;
    public DoubleProperty propStartX;
    public DoubleProperty propAirplaneX;
    public BooleanProperty propPath;
    public StringProperty propIp;
    public DoubleProperty propOffset;
    public StringProperty propPort;
    public DoubleProperty propMarkSceneY;
    public DoubleProperty propThrottle;
    public StringProperty propScript;

    public void play() {
        modelRef.execute();
    }

    public void setMetrix(int[][] data) {
        this.map = data;
        modelRef.GetPlane(propStartX.getValue(), propStartY.doubleValue(), propOffset.getValue());
    }

    public void setSpeed() {
        String command = "set /controls/engines/current-engine/throttle ";
        String[] cmds = {command + propThrottle.getValue()};
        modelRef.send(cmds);
    }

    public void apllyModel(Model _model) {
        this.modelRef = _model;
    }

    public void applyJoystick() {
        String command1 = "set /controls/flight/aileron ";
        String command2 = "set /controls/flight/elevator ";
        String[] cmds = {
                command1 + propAileron.getValue(),
                command2 + propElevator.getValue(),
        };

        modelRef.send(cmds);
    }

    public void getPath(double height, double width) {
        if (!propPath.getValue())
            modelRef.connectPath(propIp.getValue(), Integer.parseInt(propPort.getValue()));


        modelRef.getPath((int) (propAirplaneY.getValue() / (-1)),
                (int) (propAirplaneX.getValue() + (15)),
                Math.abs((int) (propMarkSceneY.getValue() / height)),
                Math.abs((int) (propMarkSceneX.getValue() / width)),
                map);
    }

    public void applyRudder() {
        String[] cmd = {"set /controls/flight/rudder " + propRudder.getValue()};
        modelRef.send(cmd);
    }

    public void runScript() {
        Scanner scr = new Scanner(propScript.getValue());
        ArrayList<String> scriptList = new ArrayList<>();

        while (scr.hasNextLine() == true)
            scriptList.add(scr.nextLine());

        String[] temp = new String[scriptList.size()];

        for (int i = 0; i < scriptList.size(); i++)
            temp[i] = scriptList.get(i);

        modelRef.parse(temp);

        scr.close();
    }

    @Override
    public void update(Observable observable, Object arg) {
        if (observable != modelRef) return;

        String[] args = (String[]) arg;

        if (args[0].equals("plane")) {
            double arg1 = Double.parseDouble(args[1]);
            double arg2 = Double.parseDouble(args[2]);

            arg1 = (arg1 - propStartX.getValue() + propOffset.getValue());
            arg1 /= propOffset.getValue();
            arg2 = (arg2 - propStartY.getValue() + propOffset.getValue()) / propOffset.getValue();

            propAirplaneY.setValue(arg2);
            propAirplaneX.setValue(arg1);
            propHeading.setValue(Double.parseDouble(args[3]));

            setChanged();
            notifyObservers();

        } else if (args[0].equals("path")) {

            setChanged();

            notifyObservers(args);
        }

    }


    public void connectServer() {
        modelRef.connectManual(propIp.getValue(), Integer.parseInt(propPort.getValue()));
    }


    public ViewModel() {
        propStartY = new SimpleDoubleProperty();
        propAirplaneX = new SimpleDoubleProperty();
        propElevator = new SimpleDoubleProperty();
        propThrottle = new SimpleDoubleProperty();
        propPath = new SimpleBooleanProperty();
        propRudder = new SimpleDoubleProperty();
        propHeading = new SimpleDoubleProperty();
        propAirplaneY = new SimpleDoubleProperty();
        propIp = new SimpleStringProperty();
        propPort = new SimpleStringProperty();
        propStartX = new SimpleDoubleProperty();
        propScript = new SimpleStringProperty();
        propMarkSceneY = new SimpleDoubleProperty();
        propAileron = new SimpleDoubleProperty();
        propOffset = new SimpleDoubleProperty();
        propMarkSceneX = new SimpleDoubleProperty();
    }

    public void stopAutofly() {
        modelRef.stopAutofly();
    }

}