package print_server;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFrameForm extends javax.swing.JFrame {
    private JTextField serverPortText;
    private JTextField txtChooser;
    private JButton btnChoose;
    private JPanel mainpanel;
    private JTextArea textArea1;
    private JButton btnStop;
    private JButton btnStart;
    private JLabel serverIpLabel;
    private JScrollPane scrollPane;
    static int port;
    static String dir_path = "";
    JFileChooser chooser;
    static LinkedList<String> linkedList;

    public void updateMsgWindow(String string) {
        textArea1.append(string);
    }

    public ServerFrameForm() {

        initComponents();

//       / jScrollPane1.setViewportView(textArea1);

        textArea1.setEditable(false);
        linkedList = new LinkedList<String> ();
        try {
            serverIpLabel.setText(InetAddress.getLocalHost().getHostAddress());
        } catch (UnknownHostException ex) {
            Logger.getLogger(ServerFrameForm.class.getName()).log(Level.SEVERE, null, ex);
        }

        btnStart.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startServer();
            }
        });
        btnStop.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopServer();
            }
        });
        btnChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseDir();
            }
        });
    }

    private void initComponents() {
        setTitle("Сервер для печати");
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    public void chooseDir() {
        chooser = new JFileChooser();
        chooser.setCurrentDirectory(new java.io.File("."));
        chooser.setDialogTitle("Выберите папку");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

        chooser.setAcceptAllFileFilterUsed(false);
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            String path = chooser.getSelectedFile().toString();
            dir_path = path.replace('\\','/' );
            dir_path += '/';
            System.out.println(dir_path);

            txtChooser.setText(dir_path);
        }
        else {
            dir_path = "";
        }
    }

    public void startServer() {
        try {
            port = Integer.parseInt(serverPortText.getText());
        }
        catch (Exception e) {
            updateMsgWindow("Ошибка! Порт должен быть числом от 0 до 65535!\n");
            serverPortText.requestFocus();
            serverPortText.selectAll();
            return;
        }

        if (dir_path.equals("")) {
            updateMsgWindow("Ошибка! Выберите корневую директорию сервера!\n");
            txtChooser.requestFocus();
            return;
        }

        txtChooser.setEnabled(false);
        btnChoose.setEnabled(false);
        btnStart.setEnabled(false);
        serverPortText.setEnabled(false);
        StartServer server = new StartServer(port, textArea1);
        server.start();
    }

    public void stopServer() {
        updateMsgWindow("Сервер останавливается...");
        System.exit(0);
    }

    public static void main(String args[]) {
        new ServerFrameForm();
    }
}
