package print_server;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;
import static print_server.ServerFrameForm.*;

public class StartServer extends Thread {

    int port;
    ServerSocket serverSocket = null;
    Socket socket = null;
    static JTextArea _ta;
    public StartServer(int port, JTextArea ta) {
        this.port = port;
        _ta = ta;
    }

    @Override
    public void run() {
        try {
            serverSocket = new ServerSocket(port);
            _ta.append("Сервер успешно запущен!\n");
            _ta.append("Сервер начал прием клиентов\n");
        } catch (IOException ex) {
            //ServerFrameForm.updateMsgWindow("Ошибка сервера!");
            System.exit(1);
            Logger.getLogger(ServerFrameForm.class.getName()).log(Level.SEVERE, null, ex);
        }
        do
        {                    
            try {
                socket = serverSocket.accept();

                _ta.append("Адрес клиента: " + socket.getInetAddress().getHostName()+"\n");
                AddClientRequest addClientRequest = new AddClientRequest(socket);
                addClientRequest.start();
            } catch (IOException ex) {
                _ta.append("Ошибка принятия клиента!\n");
                Logger.getLogger(ServerFrameForm.class.getName()).log(Level.SEVERE, null, ex);
            }
        } while(true);
    }
    
       
}
