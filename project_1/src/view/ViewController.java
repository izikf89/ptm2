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
    @FXML
    private Canvas FXMLairplane;
    @FXML
    private Canvas FXMLmarkX;
    @FXML
    private TextArea FXMLTextArea;
    @FXML
    private TextField FXMLport;
    @FXML
    private TextField FXMLip;
    @FXML
    private Button FXMLsend;
    @FXML
    private Slider FXMLthrottle;
    @FXML
    private Slider FXMLrudder;
    @FXML
    private RadioButton FXMLauto;
    @FXML
    private FlightMap FXMLmap;
    @FXML
    private RadioButton manual;
    @FXML
    private Circle border;
    @FXML
    private Circle FXMLwheel;
    @FXML
    private TitledPane background;

    private Stage stage = new Stage();

    private static int When;
    double sceneX;
    double sceneY;
    double transX;
    private ViewModel VM;
    double transY;
    public DoubleProperty markX;
    public DoubleProperty markY;
    public DoubleProperty aileron;
    public DoubleProperty elevator;
    public DoubleProperty airplaneX;
    public DoubleProperty airplaneY;
    public DoubleProperty inX;
    public DoubleProperty inY;
    public DoubleProperty choose;
    public DoubleProperty head;
    public double holdX;
    public double holdY;
    public int chart[][];
    private Image plane[];
    private Image loc;
    private BooleanProperty routh;
    private String[] res;

    public void Manual() {
        Select("b");
    }



    public void getServer() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopupConnect.fxml"));
            Parent parent = fxmlLoader.load();

            ViewController fc = fxmlLoader.getController();
            fc.VM = this.VM;

            stage.setTitle("Connect");
            stage.setScene(new Scene(parent));

            if (!stage.isShowing()) {
                stage.show();
                When = 0;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void script() {

        FXMLTextArea.clear();

        JFileChooser chooser = new JFileChooser();
        chooser.setCurrentDirectory(new File("./"));
        int v = chooser.showOpenDialog(null);

        if (v != JFileChooser.APPROVE_OPTION) return;

        try {
            Scanner scanner = new Scanner(new BufferedReader(new FileReader(chooser.getSelectedFile())));

            while (scanner.hasNextLine() == true) {
                FXMLTextArea.appendText(scanner.nextLine());
                FXMLTextArea.appendText("\n");
            }

            VM.playScripttext();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public void getRoute() {
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("PopupConnect.fxml"));
            Parent parent = fxmlLoader.load();
            ViewController fc = fxmlLoader.getController();

            fc.VM = this.VM;
            fc.chart = this.chart;
            fc.FXMLmarkX = this.FXMLmarkX;
            fc.routh = new SimpleBooleanProperty();
            fc.routh.bindBidirectional(this.routh);

            stage.setTitle("Connect");
            stage.setScene(new Scene(parent));

            if (!stage.isShowing()) {
                When = 1;
                stage.show();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void openCsv() {
        JFileChooser chooser = new JFileChooser();

        chooser.setFileFilter(new FileNameExtensionFilter("CSV Files", "csv"));

        chooser.setCurrentDirectory(new File("./"));

        int isApprove = chooser.showOpenDialog(null);
        if (isApprove == JFileChooser.APPROVE_OPTION) {
            String lines = "";
            String cvsSplitBy = ",";

            ArrayList<String[]> numbers = new ArrayList<>();
            try {
                BufferedReader bufferedReader = new BufferedReader(new FileReader(chooser.getSelectedFile()));
                String[] start = bufferedReader.readLine().split(cvsSplitBy);

                inX.setValue(Double.parseDouble(start[0]));
                inY.setValue(Double.parseDouble(start[1]));

                start = bufferedReader.readLine().split(cvsSplitBy);

                choose.setValue(Double.parseDouble(start[0]));

                while ((lines = bufferedReader.readLine()) != null)
                    numbers.add(lines.split(cvsSplitBy));


                chart = new int[numbers.size()][];

                for (int i = 0; i < numbers.size(); i++) {
                    chart[i] = new int[numbers.get(i).length];

                    for (int j = 0; j < numbers.get(i).length; j++) {
                        String tmp = numbers.get(i)[j];
                        chart[i][j] = Integer.parseInt(tmp);
                    }
                }

                this.VM.setChart(chart);
                this.createAirplane();

                FXMLmap.setMap(chart);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void AutoPilot() {
        Select("a");
    }

       public void Select(String s) {
        if (s.equals("a")) {
            if (manual.isSelected()) {
                manual.setSelected(false);
                FXMLauto.setSelected(true);

            }

            VM.run();
        } else if (s.equals("b")) {
            if (FXMLauto.isSelected()) {
                FXMLauto.setSelected(false);
                manual.setSelected(true);
                VM.closeAP();
            }
        }
    }

    public void createAirplane() {
        if ((airplaneX.getValue() != null) && (airplaneY.getValue() != null)) {
            double H = FXMLairplane.getHeight();
            double W = FXMLairplane.getWidth();
            double h = FXMLairplane.getHeight() / chart.length;
            double w = FXMLairplane.getWidth() / chart[0].length;

            GraphicsContext gc = FXMLairplane.getGraphicsContext2D();
            holdX = airplaneX.getValue();
            holdY = airplaneY.getValue() * -1;

            gc.clearRect(0, 0, FXMLairplane.getWidth(), FXMLairplane.getHeight());

            if (head.getValue() >= 0)
                gc.drawImage(plane[getHeadIndex(head.getValue())], (w * holdX), (holdY * h), 25, 25);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (location.getPath().contains("View.fxml")) {
            FXMLthrottle.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (manual.isSelected()) {
                    VM.speed();
                }
            });

            FXMLrudder.valueProperty().addListener((observable, oldValue, newValue) -> {
                if (manual.isSelected()) {
                    VM.rudder();
                }
            });

            FXMLwheel.setOnMousePressed(circleOnMousePressed);
            FXMLwheel.setOnMouseDragged(circleOnMouseDragged);
            FXMLwheel.setOnMouseReleased(circleOnMouseReleasedEventHandler);

            FXMLmarkX.setOnMouseClicked(MarkOnMousePressed);

            When = -1;
        }
    }

    @Override
    public void update(Observable o, Object arg) {
        if (o == VM) {
            if (arg == null) {
                createAirplane();
            } else {
                res = (String[]) arg;
                this.getLine();
            }
        }
    }
    private int getHeadIndex(double headVal) {
        if ((headVal < 39))
            return 0;
        else if  (headVal < 80)
            return 1;
        else if  (headVal < 129)
            return 2;
        else if (headVal < 170)
            return 3;
        else if  (headVal < 219)
            return 4;
        else if  (headVal < 260)
            return 5;
        else if  (headVal < 309)
            return 5;
        else
            return 6;
    }

    public void getMark() {
        double W = FXMLmarkX.getWidth();
        double H = FXMLmarkX.getHeight();
        
        double w = W / chart[0].length;
        
        double h = H / chart.length;

        GraphicsContext gc = FXMLmarkX.getGraphicsContext2D();

        gc.clearRect(0, 0, W, H);
        gc.drawImage(loc, (markX.getValue() - 13), markY.getValue(), 25, 25);

        if (routh.getValue()) {
            VM.getRoute(h, w);
        }
    }

    public void getLine() {
        double H = FXMLmarkX.getHeight();
        double W = FXMLmarkX.getWidth();
        
        double h = H / chart.length;
        
        double w = W / chart[0].length;

        GraphicsContext gc = FXMLmarkX.getGraphicsContext2D();

        String move = res[1];

        double x = airplaneX.getValue() * w + (10 * w);
        double y = airplaneY.getValue() * -h + 6 * h;
        gc.setStroke(Color.BLACK);

        for (int i = 2; i < res.length; i++) {
            switch (move) {
                case "Left":
                    gc.strokeLine(x, y, x - w, y);
                    x -= w;
                    break;
                case "Up":
                    gc.strokeLine(x, y, x, y - h);
                    y -= h;
                    break;
                case "Right":
                    gc.strokeLine(x, y, x + w, y);
                    x += w;
                    break;
                case "Down":
                    gc.strokeLine(x, y, x, y + h);
                    y += h;
                    break;
            }

            move = res[i];
        }
    }
    public void send() {
        this.VM.ip.bindBidirectional(FXMLip.textProperty());
        this.VM.port.bindBidirectional(FXMLport.textProperty());

        switch (When) {
            case 0:
                VM.getSimulator();
                Stage stage = (Stage) FXMLsend.getScene().getWindow();
                stage.close();
                break;
            case 1:
                double H = FXMLmarkX.getHeight();
                double W = FXMLmarkX.getWidth();
                double h = H / chart.length;
                double w = W / chart[0].length;

                VM.getRoute(h, w);

                routh.setValue(true);

                ((Stage) FXMLsend.getScene().getWindow()).close();
                break;
        }

        FXMLport.clear();

        FXMLip.clear();
    }

    EventHandler<MouseEvent> MarkOnMousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent e) {
            markX.setValue(e.getX());
            markY.setValue(e.getY());
            getMark();
        }
    };

    private double doX(double num) {
        double max = (border.getRadius() - FXMLwheel.getRadius()) + border.getCenterX();
        double min = border.getCenterX() - (border.getRadius() - FXMLwheel.getRadius());
        double new_max = 1;
        double new_min = -1;
        return (((num - min) / (max - min) * (new_max - new_min) + new_min));
    }

    private double doY(double num) {
        double min = (border.getRadius() - FXMLwheel.getRadius()) + border.getCenterY();
        double max = border.getCenterY() - (border.getRadius() - FXMLwheel.getRadius());
        double new_max = 1;
        double new_min = -1;
        return (((num - min) / (max - min) * (new_max - new_min) + new_min));
    }


    EventHandler<MouseEvent> circleOnMousePressed = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            sceneX = t.getSceneX();
            sceneY = t.getSceneY();
            transX = ((Circle) (t.getSource())).getTranslateX();
            transY = ((Circle) (t.getSource())).getTranslateY();
        }
    };

    EventHandler<MouseEvent> circleOnMouseDragged = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            double offsetY = t.getSceneY() - sceneY;
            double offsetX = t.getSceneX() - sceneX;
            double newTranslateX = transX + offsetX;
            double newTranslateY = transY + offsetY;

            if (isIn(newTranslateX, newTranslateY)) {
                ((Circle) (t.getSource())).setTranslateX(newTranslateX);
                ((Circle) (t.getSource())).setTranslateY(newTranslateY);

                if (manual.isSelected()) {
                    aileron.setValue(doX(newTranslateX));
                    elevator.setValue(doY(newTranslateY));
                    VM.setJoystick();
                }
            }
        }
    };

    EventHandler<MouseEvent> circleOnMouseReleasedEventHandler = new EventHandler<MouseEvent>() {
        @Override
        public void handle(MouseEvent t) {
            ((Circle) (t.getSource())).setTranslateX(transX);
            ((Circle) (t.getSource())).setTranslateY(transY);
        }
    };
    boolean isIn(double x, double y) {
        return ((Math.pow((x - border.getCenterX()), 2) + Math.pow((y - border.getCenterY()), 2))) <= (Math.pow(border.getRadius() - FXMLwheel.getRadius(), 2));
    }


    public void setViewModel(ViewModel VM) {
        this.VM = VM;
        FXMLthrottle.valueProperty().bindBidirectional(VM.throttleProp);
        FXMLrudder.valueProperty().bindBidirectional(VM.rudderProp);
        aileron = new SimpleDoubleProperty();
        elevator = new SimpleDoubleProperty();
        aileron.bindBidirectional(VM.aileronProp);
        elevator.bindBidirectional(VM.elevatorProp);
        airplaneX = new SimpleDoubleProperty();
        airplaneY = new SimpleDoubleProperty();
        inX = new SimpleDoubleProperty();
        inY = new SimpleDoubleProperty();
        airplaneX.bindBidirectional(VM.airplaneXProp);
        airplaneY.bindBidirectional(VM.airplaneYProp);
        inX.bindBidirectional(VM.startXProp);
        inY.bindBidirectional(VM.startYProp);
        choose = new SimpleDoubleProperty();
        choose.bindBidirectional(VM.offsetProp);
        VM.text.bindBidirectional(FXMLTextArea.textProperty());
        head = new SimpleDoubleProperty();
        head.bindBidirectional(VM.headingProp);
        markX = new SimpleDoubleProperty();
        markY = new SimpleDoubleProperty();
        markY.bindBidirectional(VM.markSceneYProp);
        markX.bindBidirectional(VM.markSceneXProp);
        routh = new SimpleBooleanProperty();
        routh.bindBidirectional(VM.route);
        routh.setValue(false);
        plane = new Image[8];

        try {
            plane[0] = new Image(new FileInputStream("./resources/plane0.png"));
            plane[1] = new Image(new FileInputStream("./resources/plane45.png"));
            plane[2] = new Image(new FileInputStream("./resources/plane90.png"));
            plane[3] = new Image(new FileInputStream("./resources/plane135.png"));
            plane[4] = new Image(new FileInputStream("./resources/plane180.png"));
            plane[5] = new Image(new FileInputStream("./resources/plane225.png"));
            plane[6] = new Image(new FileInputStream("./resources/plane270.png"));
            plane[7] = new Image(new FileInputStream("./resources/plane315.png"));
            loc = new Image(new FileInputStream("./resources/mark.png"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}