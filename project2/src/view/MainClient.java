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

public class MainClient extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader loader = new FXMLLoader(getClass().getResource("View.fxml"));
        Parent root = loader.load();
        ViewController ctrlRef = loader.getController();
        ViewModel viewModelRef = new ViewModel();

        Model model = new Model();
        model.addObserver(viewModelRef);

        viewModelRef.apllyModel(model);
        viewModelRef.addObserver(ctrlRef);
        ctrlRef.setVM(viewModelRef);

        initStage(primaryStage, root, model);
    }

    private void initStage(Stage primaryStage, Parent root, Model model) {
        primaryStage.setTitle("Simulator");
        primaryStage.setScene(new Scene(root));

        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            DisconnectCommand command = new DisconnectCommand();
            String[] disconnect = {""};
            command.doCommand(disconnect);
            ParserAutoPilot.exe.interrupt();
            model.stopAll();
            System.out.println("finish");
        });
    }

    public static void main(String[] args) { launch(args); }
}