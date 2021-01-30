package view;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import viewModel.ViewModel;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.io.*;
import java.net.URL;
import java.util.*;

public class ViewController implements Initializable, Observer {

    private static int where;
    double translateX;
    double sceneY;
    private ViewModel vm;
    public double translateY;
    double sceneX;
    public DoubleProperty propSceneY;
    public DoubleProperty porpMarkSceneX;
    public DoubleProperty propAileron;
    public DoubleProperty propElevator;
    public DoubleProperty propAirplaneX;
    public DoubleProperty propOffset;
    public DoubleProperty propStartX;
    private Image imgP[];
    public DoubleProperty propStartY;
    public double lY;
    public DoubleProperty propHeading;
    public int mapArr[][];
    private Image imgM;
    private BooleanProperty propPath;
    private String[] solArr;
    public DoubleProperty propAirplaneY;
    public double lX;
    private Stage stage = new Stage();



    @FXML
    private TextField ipFXML;
    @FXML
    private TextArea TextAreaFXML;
    @FXML
    private TextField portFXML;
    @FXML
    private Canvas airplaneFXML;
    @FXML
    private Slider rudderFXML;
    @FXML
    private Circle JoystickFXML;
    @FXML
    private Slider throttleFXML;
    @FXML
    private RadioButton manualFXML;
    @FXML
    private Button submitFXML;
    @FXML
    private AnchorPane backgroundFXML;
    @FXML
    private Canvas markXFXML;
    @FXML
    private MapCanvas mapFXML;
    @FXML
    private RadioButton autoFXML;
    @FXML
    private Circle borderFXML;

    public void ConnectServer() {
        Parent parent = null;
        try {
            FXMLLoader fl = new FXMLLoader(getClass().getResource("Dialog.fxml"));
            parent = fl.load();

            ViewController fc = fl.getController();
            fc.vm = this.vm;

            stage.setTitle("Connect");

            stage.setScene(new Scene(parent));

            if (!stage.isShowing()) {
                stage.show();

                where = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setLine() {
        double height = markXFXML.getHeight() / mapArr.length;
        double width = markXFXML.getWidth() / mapArr[0].length;

        GraphicsContext gc = markXFXML.getGraphicsContext2D();

        String move = solArr[1];

        double x = propAirplaneX.getValue() * width + (10 * width);
        double y = propAirplaneY.getValue() * -height + 6 * height;

        for (int i = 2; i < solArr.length; i++) {
            gc.setStroke(Color.BLACK);

            switch (move) {
                case "Left":
                    gc.strokeLine(x, y, x - width, y);
                    x -= width;
                    break;
                case "Right":
                    gc.strokeLine(x, y, x + width, y);
                    x += width;
                    break;
                case "Down":
                    gc.strokeLine(x, y, x, y + height);
                    y += height;
                    break;
                case "Up":
                    gc.strokeLine(x, y, x, y - height);
                    y -= height;
                    break;
            }

            move = solArr[i];
        }
    }

    public void getPath() {
        Parent parent = null;
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("Dialog.fxml"));

            parent = fxmlLoader.load();

            ViewController fc = fxmlLoader.getController();

            fc.vm = this.vm;

            fc.mapArr = this.mapArr;
            fc.markXFXML = this.markXFXML;

            fc.propPath = new SimpleBooleanProperty();
            fc.propPath.bindBidirectional(this.propPath);

            stage.setTitle("Connect");
            stage.setScene(new Scene(parent));

            if (!stage.isShowing()) {

                where = 1;
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void closeStatge() {
        ((Stage) submitFXML.getScene().getWindow()).close();
    }

    public void OpenCsv() {
        JFileChooser fc = new JFileChooser();
        fc.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));
        fc.setCurrentDirectory(new File("./"));
        int dialogStaus = fc.showOpenDialog(null);
        if (dialogStaus == JFileChooser.APPROVE_OPTION) {
            BufferedReader br = null;
            String line = "";
            String cvsSplitBy = ",";

            ArrayList<String[]> numbers = new ArrayList<>();
            try {
                br = new BufferedReader(new FileReader(fc.getSelectedFile()));
                String[] enter = br.readLine().split(cvsSplitBy);

                propStartX.setValue(Double.parseDouble(enter[0]));

                propStartY.setValue(Double.parseDouble(enter[1]));

                enter = br.readLine().split(cvsSplitBy);

                propOffset.setValue(Double.parseDouble(enter[0]));

                while ((line = br.readLine()) != null)
                    numbers.add(line.split(cvsSplitBy));

                mapArr = new int[numbers.size()][];

                for (int i = 0; i < numbers.size(); i++) {
                    mapArr[i] = new int[numbers.get(i).length];

                    for (int j = 0; j < numbers.get(i).length; j++) {
                        String tmp = numbers.get(i)[j];
                        mapArr[i][j] = Integer.parseInt(tmp);
                    }
                }

                this.vm.setMetrix(mapArr);

                this.setPlane();

                mapFXML.setMapData(mapArr);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void setPosition(String s) {
        if (s.equals("auto")) {
            if (manualFXML.isSelected()) {

                manualFXML.setSelected(false);

                autoFXML.setSelected(true);

            }

            vm.play();
        } else if (s.equals("manual")) {
            if (autoFXML.isSelected()) {
                autoFXML.setSelected(false);

                manualFXML.setSelected(true);

                vm.stopAutofly();
            }
        }
    }

    public void connect() {
        this.vm.propPort.bindBidirectional(portFXML.textProperty());
        this.vm.propIp.bindBidirectional(ipFXML.textProperty());

        if (where == 1) {
            double width = markXFXML.getWidth() / mapArr[0].length;
            double height = markXFXML.getHeight() / mapArr.length;

            vm.getPath(height, width);

            propPath.setValue(true);

            closeStatge();

        } else if (where == 0) {
            vm.connectServer();
            closeStatge();
        }

        ipFXML.clear();
        portFXML.clear();
    }


    public void OpenScript() {

        TextAreaFXML.clear();

        JFileChooser fc = new JFileChooser();
        fc.setCurrentDirectory(new File("./"));
        int dialogStatus = fc.showOpenDialog(null);

        if (dialogStatus != JFileChooser.APPROVE_OPTION) return;

        try {
            Scanner scr = new Scanner(new BufferedReader(new FileReader(fc.getSelectedFile())));

            while (scr.hasNextLine() == true) {
                TextAreaFXML.appendText(scr.nextLine());

                TextAreaFXML.appendText("\n");
            }

            vm.runScript();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void playAutoFly() {
        setPosition("auto");
    }


    public void setX() {

        double height = markXFXML.getHeight() / mapArr.length;
        double width = markXFXML.getWidth() / mapArr[0].length;

        GraphicsContext graphicsContext = markXFXML.getGraphicsContext2D();

        graphicsContext.clearRect(0, 0, markXFXML.getWidth(), markXFXML.getHeight());
        graphicsContext.drawImage(imgM, (porpMarkSceneX.getValue() - 13), propSceneY.getValue(), 25, 25);

        if (propPath.getValue())
            vm.getPath(height, width);

    }

    private double getStartX(double num) {
        double maximum = (borderFXML.getRadius() - JoystickFXML.getRadius()) + borderFXML.getCenterX();
        double minimum = borderFXML.getCenterX() - (borderFXML.getRadius() - JoystickFXML.getRadius());


        return ((calculateNirmul(num, minimum, maximum)));
    }


    private boolean isInCircle(double x, double y) {
        return ((Math.pow((x - borderFXML.getCenterX()), 2) + Math.pow((y - borderFXML.getCenterY()), 2))) <= (Math.pow(borderFXML.getRadius() - JoystickFXML.getRadius(), 2));
    }


    public void playManual() {
        setPosition("manual");
    }

    private double getStartY(double num) {
        double maximum = borderFXML.getCenterY() - (borderFXML.getRadius() - JoystickFXML.getRadius());
        double minimum = (borderFXML.getRadius() - JoystickFXML.getRadius()) + borderFXML.getCenterY();

        return calculateNirmul(num, minimum, maximum);
    }


    private void getImg() {
        try {
            imgP[0] = new Image(new FileInputStream("./resources/plane0.png"));
            imgP[1] = new Image(new FileInputStream("./resources/plane45.png"));
            imgP[2] = new Image(new FileInputStream("./resources/plane90.png"));
            imgP[3] = new Image(new FileInputStream("./resources/plane135.png"));
            imgP[4] = new Image(new FileInputStream("./resources/plane180.png"));
            imgP[5] = new Image(new FileInputStream("./resources/plane225.png"));
            imgP[6] = new Image(new FileInputStream("./resources/plane270.png"));
            imgP[7] = new Image(new FileInputStream("./resources/plane315.png"));
            imgM = new Image(new FileInputStream("./resources/mark.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }


    private double calculateNirmul(double num, double min, double max) {
        return (num - min) / (max - min) * 2 - 1;
    }

    public void setPlane() {
        if ((propAirplaneX.getValue() == null) || (propAirplaneY.getValue() == null)) return;
        lY = propAirplaneY.getValue() * -1;
        lX = propAirplaneX.getValue();

        double height = (airplaneFXML.getHeight() / mapArr.length) * lY;
        double width = (airplaneFXML.getWidth() / mapArr[0].length) * lX;

        GraphicsContext gc = airplaneFXML.getGraphicsContext2D();

        gc.clearRect(0, 0, airplaneFXML.getWidth(), airplaneFXML.getHeight());

        double head = propHeading.getValue();

        if ((head >= 0) && (head < 39))
            gc.drawImage(imgP[0], (width * lX), (lY * height), 25, 25);
        else if ((head >= 39) && (head < 80))
            gc.drawImage(imgP[1], (width * lX), (lY * height), 25, 25);
        else if ((head >= 80) && (head < 129))
            gc.drawImage(imgP[2], (width * lX), (lY * height), 25, 25);
        else if ((head >= 129) && (head < 170))
            gc.drawImage(imgP[3], (width * lX), (lY * height), 25, 25);
        else if ((head >= 170) && (head < 219))
            gc.drawImage(imgP[4], (width * lX), (lY * height), 25, 25);
        else if ((head >= 219) && head < 260)
            gc.drawImage(imgP[5], (width * lX), (lY * height), 25, 25);
        else if (head >= 260 && head < 309)
            gc.drawImage(imgP[6], (width * lX), (lY * height), 25, 25);
        else if (head >= 309)
            gc.drawImage(imgP[7], (width * lX), (lY * height), 25, 25);

    }

    public void setVM(ViewModel _vm) {
        this.vm = _vm;
        throttleFXML.valueProperty().bindBidirectional(_vm.propThrottle);
        rudderFXML.valueProperty().bindBidirectional(_vm.propRudder);

        propAileron = new SimpleDoubleProperty();
        propElevator = new SimpleDoubleProperty();

        propAileron.bindBidirectional(_vm.propAileron);
        propElevator.bindBidirectional(_vm.propElevator);

        propAirplaneX = new SimpleDoubleProperty();
        propAirplaneY = new SimpleDoubleProperty();

        propStartX = new SimpleDoubleProperty();
        propStartY = new SimpleDoubleProperty();

        propAirplaneX.bindBidirectional(_vm.propAirplaneX);
        propAirplaneY.bindBidirectional(_vm.propAirplaneY);
        propStartX.bindBidirectional(_vm.propStartX);
        propStartY.bindBidirectional(_vm.propStartY);

        propOffset = new SimpleDoubleProperty();
        propOffset.bindBidirectional(_vm.propOffset);

        _vm.propScript.bindBidirectional(TextAreaFXML.textProperty());
        propHeading = new SimpleDoubleProperty();

        propHeading.bindBidirectional(_vm.propHeading);
        porpMarkSceneX = new SimpleDoubleProperty();
        propSceneY = new SimpleDoubleProperty();

        propSceneY.bindBidirectional(_vm.propMarkSceneX);
        porpMarkSceneX.bindBidirectional(_vm.propMarkSceneY);

        propPath = new SimpleBooleanProperty();
        propPath.bindBidirectional(_vm.propPath);
        propPath.setValue(false);

        imgP = new Image[8];

        getImg();
    }


    @Override
    public void update(Observable o, Object arg) {
        if (o == vm)
            if (arg == null)
                setPlane();
            else {
                solArr = (String[]) arg;
                this.setLine();
            }
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (!location.getPath().contains("View.fxml")) return;

        throttleFXML.valueProperty().addListener((o, b, c) -> {
            if (manualFXML.isSelected())
                vm.setSpeed();
        });

        rudderFXML.valueProperty().addListener((a, b, c) -> {
            if (manualFXML.isSelected())
                vm.applyRudder();
        });

        JoystickFXML.setOnMouseDragged(OnMouseDraggedCircle);
        JoystickFXML.setOnMouseReleased(OnMouseReleasedCircle);
        JoystickFXML.setOnMousePressed(OnMousePressedCircle);

        markXFXML.setOnMouseClicked(OnMousePressedMark);

        where = -1;
    }


    EventHandler<MouseEvent> OnMouseReleasedCircle = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            ((Circle) (t.getSource())).setTranslateY(translateY);
            ((Circle) (t.getSource())).setTranslateX(translateX);
        }
    };


    EventHandler<MouseEvent> OnMousePressedCircle = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            sceneX = t.getSceneX();
            sceneY = t.getSceneY();
            translateX = ((Circle) (t.getSource())).getTranslateX();
            translateY = ((Circle) (t.getSource())).getTranslateY();
        }
    };

    EventHandler<MouseEvent> OnMousePressedMark = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            porpMarkSceneX.setValue(e.getX());
            propSceneY.setValue(e.getY());
            setX();
        }
    };

    EventHandler<MouseEvent> OnMouseDraggedCircle = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            double offsetY = t.getSceneY() - sceneY;
            double offsetX = t.getSceneX() - sceneX;

            double newX = translateX + offsetX;
            double newY = translateY + offsetY;

            if (isInCircle(newX, newY)) {

                ((Circle) (t.getSource())).setTranslateY(newY);
                ((Circle) (t.getSource())).setTranslateX(newX);

                if (manualFXML.isSelected()) {

                    propElevator.setValue(getStartY(newY));
                    propAileron.setValue(getStartX(newX));

                    vm.applyJoystick();
                }
            }
        }
    };

}