package DocSignVerification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.security.PrivateKey;

public class FileVerifierApp extends JFrame implements ActionListener {
    private JTextField fileTextField;
    private JTextField keyFileTextField;
    private JTextField hashTextField;
    private String selectedFilePath;
    private String fileHash;
    private String decryptedHash;

    public FileVerifierApp() {
        setTitle("Document Signing");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(400, 300);
        setLocationRelativeTo(null); // Center the frame on the screen

        JPanel mainPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        mainPanel.setBackground(new Color(225, 241, 255));

        JLabel heading = new JLabel("FILE VERIFICATION");
        heading.setFont(new Font("Serif", Font.BOLD, 24));
        heading.setForeground(new Color(232, 142, 64));

        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(heading, gbc);

        gbc.gridwidth = 1;
        gbc.weightx = 0.0;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel selectFileLabel = new JLabel("Select File:");
        gbc.gridy = 1;
        gbc.gridx = 0;
        mainPanel.add(selectFileLabel, gbc);

        fileTextField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(fileTextField, gbc);

        JButton selectFileButton = new JButton("Select File");
        gbc.gridx = 2;
        selectFileButton.addActionListener(this);
        mainPanel.add(selectFileButton, gbc);

        JLabel chooseKeyFileLabel = new JLabel("Choose Key File:");
        gbc.gridy = 2;
        gbc.gridx = 0;
        mainPanel.add(chooseKeyFileLabel, gbc);

        keyFileTextField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(keyFileTextField, gbc);

        JButton chooseKeyFileButton = new JButton("Choose Key File");
        gbc.gridx = 2;
        chooseKeyFileButton.addActionListener(this);
        mainPanel.add(chooseKeyFileButton, gbc);

        JLabel hashLabel = new JLabel("HASH:");
        gbc.gridy = 3;
        gbc.gridx = 0;
        mainPanel.add(hashLabel, gbc);

        hashTextField = new JTextField(20);
        gbc.gridx = 1;
        mainPanel.add(hashTextField, gbc);

        JButton verifyButton = new JButton("Verify");
        gbc.gridy = 4;
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.anchor = GridBagConstraints.CENTER;
        mainPanel.add(verifyButton, gbc);

        add(mainPanel);
        setVisible(true);

        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String filePath = fileTextField.getText();
                String keyFilePath = keyFileTextField.getText();
                String hash = hashTextField.getText();
                if (fileHash.equals(decryptedHash)) {
                    JOptionPane.showMessageDialog(null, "Key matches. Not tampered.");
                } else {
                    JOptionPane.showMessageDialog(null, "Key does not match. Tampered.");
                }

            }
        });


        selectFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    fileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    File file = fileChooser.getSelectedFile();
                    selectedFilePath = file.getAbsolutePath();
                    fileHash = MD5FileHash.doFinal(selectedFilePath);
                }
            }
        });
        chooseKeyFileButton.addActionListener(this);
    }//constructor ends------

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new FileVerifierApp());
    }

    public void actionPerformed(ActionEvent e) {
        // Handle button actions
        if(e.getSource() instanceof JButton){
            JButton sourceButton = (JButton) e.getSource();
            if (sourceButton.getText().equals("Choose Key File")) {
                JFileChooser fileChooser = new JFileChooser();
                int returnValue = fileChooser.showOpenDialog(null);
                if (returnValue == JFileChooser.APPROVE_OPTION) {
                    keyFileTextField.setText(fileChooser.getSelectedFile().getAbsolutePath());
                    File keyFile = fileChooser.getSelectedFile();
                    String selectedKeyPath = keyFile.getAbsolutePath();

                    PrivateKey pKey;
                    try {
                        PrivateKey Key = RSACrypto.loadPrivateKeyFromFile(selectedKeyPath);
                        String hash = hashTextField.getText();
                        decryptedHash = RSACrypto.decrypt(Key, hash);
                    } catch (Exception ex) {
                        throw new RuntimeException(ex);
                    }


                }
            }
        }
    }
}
