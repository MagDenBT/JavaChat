public interface TCPConnectionListener {

    void OnConnection(TCPConnection tcpConnection);

    void OnDisconnection(TCPConnection tcpConnection);

    void OnRecieveMessage(TCPConnection tcpConnection, String msg);

    void OnException(TCPConnection tcpConnection, Exception e);
}
