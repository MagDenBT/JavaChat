import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class ServerChat implements TCPConnectionListener{

    public static void main(String[] args) throws IOException {
        new ServerChat();
    }

    private final ArrayList<TCPConnection> connectionsList;
    private final ServerSocket serverSocket;
    private final int PORT = 8189;

    private ServerChat() throws IOException {
        serverSocket = new ServerSocket(PORT);
        connectionsList = new ArrayList<>();
            while (true) {
                try {
                    new TCPConnection(serverSocket.accept(), this);
                } catch (IOException e) {
                }
            }
    }

    @Override
    public synchronized void OnConnection(TCPConnection tcpConnection) {
        connectionsList.add(tcpConnection);
        sendMsgAll("Client connected " + tcpConnection);
    }

    @Override
    public synchronized void OnDisconnection(TCPConnection tcpConnection) {
        connectionsList.remove(tcpConnection);
        sendMsgAll("Client disconnected " + tcpConnection);
    }

    @Override
    public synchronized void OnRecieveMessage(TCPConnection tcpConnection, String msg) {
            sendMsgAll(msg);
    }

    @Override
    public synchronized void OnException(TCPConnection tcpConnection, Exception e) {
        System.out.println("Huston we have a problem with " + tcpConnection + " Error: " + e);
    }

    private synchronized void sendMsgAll(String msg) {
        int size = connectionsList.size();
        for (int i = 0; i < size; i++) {
            connectionsList.get(i).SendMsg(msg);
        }
    }
}