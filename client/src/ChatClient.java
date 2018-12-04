import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.scene.PointLight;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;

public class ChatClient extends Application implements TCPConnectionListener{

    private TextField fieldNick, fieldInput;
    private TextArea log;
    private Label connectionStatusLabel;
    private Button reconnect;
    private double width;
    private double height;
    private TCPConnection tcpConnection;
    private final String IPAdress = "127.0.0.1";
    private final int PORT = 8190;
    private boolean connectionStarted;

    public static void main(String[] args) {
        launch(args);

    }



    private synchronized void printMsg(String value) {
        Platform.runLater(()->{
            log.appendText(value + "\n");
        });
    }

    private void startConnection(){
        try {
            tcpConnection = new TCPConnection(IPAdress, PORT, this);
        } catch (IOException e) {
            setConnectionLabelText("Ошибка подключения: " + e.getMessage());
        };
    }

    private void restartConnection(){
        try {
            tcpConnection.closeConnection();
        } catch (IOException e) {
            setConnectionLabelText("Не удалось закрыть сокет: " + e.getMessage());
        }finally {
            startConnection();
        }
    }

    private void setConnectionLabelText(String text) {
        Platform.runLater(() -> {
            connectionStatusLabel.setText(text);
        });
    }

    /*
           Override methods
     */
    @Override
    public void start(Stage primaryStage) throws Exception {


        width = 600.00;
        height = 400.00;
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);


        connectionStatusLabel = new Label("Оффлайн");


        reconnect = new Button("Переподключиться");


        fieldNick = new TextField();
        fieldNick.setPromptText("Введите Ваш ник");
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(fieldNick, connectionStatusLabel,reconnect);

        log = new TextArea();
        log.setEditable(false);
        fieldInput = new TextField();
        fieldInput.setPromptText("Написать");

        vBox.getChildren().addAll(hBox, log, fieldInput);

        fieldInput.setOnAction(event -> {
            String nick = fieldNick.getText();
            if(nick.equals("")){
                fieldNick.requestFocus();
                return;
            }
            tcpConnection.SendMsg(nick + " пишет: " + fieldInput.getText());
            fieldInput.clear();
        });

        reconnect.setOnAction((e) ->{
            restartConnection();
        });

        primaryStage.show();
        startConnection();






    }

    @Override
    public void OnConnection(TCPConnection tcpConnection) {
        setConnectionLabelText("Онлайн");
    }

    @Override
    public void OnDisconnection(TCPConnection tcpConnection) {
        setConnectionLabelText("Оффлайн");
    }

    @Override
    public void OnRecieveMessage(TCPConnection tcpConnection, String msg) {
        printMsg(msg);
    }

    @Override
    public void OnException(TCPConnection tcpConnection, Exception e) {
        printMsg("Не удалось получить/передать сообщение в " + tcpConnection + " err - " + e.getMessage());
    }

}
