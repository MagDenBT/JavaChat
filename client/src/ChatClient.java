import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
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
    private double width;
    private double height;
    private TCPConnection tcpConnection;
    private final String IPAdress = "127.0.0.1";
    private final int PORT = 8189;
    private boolean connectionStarted;

    public static void main(String[] args) {
        launch(args);

    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Platform.runLater( () -> {
            try {
                tcpConnection = new TCPConnection(IPAdress,PORT,ChatClient.this);
            }catch (IOException e){
            }
        });

        width = 600.00;
        height = 400.00;
        VBox vBox = new VBox();
        Scene scene = new Scene(vBox);
        primaryStage.setScene(scene);
        primaryStage.setWidth(width);
        primaryStage.setHeight(height);


        connectionStatusLabel = new Label("Оффлайн");
        fieldNick = new TextField();
        fieldNick.setPromptText("Введите Ваш ник");
        HBox hBox = new HBox(10);
        hBox.getChildren().addAll(fieldNick, connectionStatusLabel);

        log = new TextArea();
        log.setEditable(false);
        fieldInput = new TextField();
        fieldInput.setPromptText("Написать");

        vBox.getChildren().addAll(hBox, log, fieldInput);

        fieldInput.setOnAction(event -> {
            String nick = fieldNick.getText();
            if(nick.equals("")){
                fieldNick.setFocusTraversable(true);
                return;
            }
            tcpConnection.SendMsg(nick + " пишет: " + fieldInput.getText());
        });

        primaryStage.show();



    }

    @Override
    public void OnConnection(TCPConnection tcpConnection) {
        connectionStatusLabel.setText("Онлайн");
    }

    @Override
    public void OnDisconnection(TCPConnection tcpConnection) {
        connectionStatusLabel.setText("Оффлайн");
    }

    @Override
    public void OnRecieveMessage(TCPConnection tcpConnection, String msg) {
            log.appendText(msg + "\n");
    }

    @Override
    public void OnException(TCPConnection tcpConnection, Exception e) {
        log.appendText("Problem with " + tcpConnection + " err - " + e);
    }

}
