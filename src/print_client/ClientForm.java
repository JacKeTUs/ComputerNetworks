package print_client;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientForm extends javax.swing.JFrame {
    private JTextField serverIp;
    private JTextField serverPort;
    private JButton btnChoose;
    private JButton btnSent;
    private JButton btnExit;
    private JTextField txtChoose;
    private JPanel mainpanel;

    String path;
    String name;

    Socket socket = null;
    DataOutputStream dataOutputStream = null;

    FileInputStream fileInputStream = null;

    public ClientForm() {

        initComponents();

        btnSent.setEnabled(false);
        btnSent.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendFile();
            }
        });
        btnExit.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
        btnChoose.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                chooseFile();
            }
        });
    }

    private void initComponents() {
        setTitle("Клиент для печати");
        setContentPane(mainpanel);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setVisible(true);
    }

    private void chooseFile() {
        JFileChooser jfc=new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Images", "png", "jpg");
        jfc.setFileFilter(filter);
        int res;
        try
        {
            res=jfc.showOpenDialog(null);
            if(res == JFileChooser.APPROVE_OPTION)
            {
                File file=jfc.getSelectedFile();
                path=file.getPath();
                System.out.println(path);
                name=file.getName();
                txtChoose.setText(name);
                txtChoose.setEditable(false);

                btnSent.setEnabled(true);
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendFile() {
        btnSent.setEnabled(false);
        btnChoose.setEnabled(false);
        serverIp.setEnabled(false);
        serverPort.setEnabled(false);
        int port;
        if (serverIp.getText().equals("")) {
            JOptionPane.showMessageDialog(this, "Введите IP адрес сервера!");
            serverIp.requestFocus();
            btnSent.setEnabled(true);

            serverIp.setEnabled(true);
            serverPort.setEnabled(true);
            return;
        }
        try {
            port = Integer.parseInt(serverPort.getText());
        }
        catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Порт должен состоять из цифр!");
            serverPort.requestFocus();
            serverPort.selectAll();
            btnSent.setEnabled(true);

            serverIp.setEnabled(true);
            serverPort.setEnabled(true);
            return;
        }
        try {
            socket = new Socket(serverIp.getText(), port);
            dataOutputStream = new DataOutputStream(socket.getOutputStream());

            fileInputStream = new FileInputStream(path);
            dataOutputStream.writeUTF(name);
            while(true)
            {
                int b = fileInputStream.read();
                if(b == -1)
                    break;
                dataOutputStream.write(b);
            }
            socket.close();

            btnChoose.setEnabled(true);
            txtChoose.setText("");
            serverIp.setEnabled(true);
            serverPort.setEnabled(true);

        } catch (IOException ex) {
            Logger.getLogger(ClientForm.class.getName()).log(Level.SEVERE, null, ex);
            JOptionPane.showMessageDialog(rootPane, "Ошибка соединения: " + ex.toString());
            btnSent.setEnabled(true);
            btnChoose.setEnabled(true);

            serverIp.setEnabled(true);
            serverPort.setEnabled(true);
            return;
        }
    }

    public static void main(String args[]) {
        new ClientForm();
    }

}
