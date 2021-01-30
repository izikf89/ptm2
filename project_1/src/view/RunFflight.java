package view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Model;
import model.interpreter.ParserAutoPilot;
import model.interpreter.commands.DisconnectCommand;
import viewModel.ViewModel;

public class RunFflight extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception{
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("View.fxml"));

        Parent parent = fxmlLoader.load();

        ViewModel vm = new ViewModel();
        ViewController vc = fxmlLoader.getController();
        Model m = new Model();


        m.addObserver(vm);
        vm.setM(m);
        vm.addObserver(vc);
        vc.setViewModel(vm);

        initStage(stage, parent, m);
    }

    private void initStage(Stage stage, Parent parent, Model m) {
        stage.setTitle("Flight Gear");
        stage.setScene(new Scene(parent));
        stage.show();
        stage.setOnCloseRequest(e -> onCloseProgram(m));
    }

    private void onCloseProgram(Model m) {
        DisconnectCommand cmd = new DisconnectCommand();
        String[] disconnect = {""};
        cmd.doCommand(disconnect);
        ParserAutoPilot.exe.interrupt();
        m.exit();
        System.out.println("*********** done ************");
    }

}