package DocSignVerification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class Verifier extends JFrame {
    private JPanel mainPanel;
    private JButton chooseFileButton;
    private JTextField filePathField;
    private JTextField keyField;
    private JButton okButton;
    String decryptedHash;
    static String key;
    static String KEY;
    String fileHash;
    String enteredHash;
    public Verifier() {
        setTitle("Verifier Side");
        setSize(450, 300);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // Center the window on the screen
        initComponents();
        setVisible(true);

    }

    private void initComponents() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(225, 241, 255));

        JPanel filePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel fileLabel = new JLabel("Selected File:");
        filePathField = new JTextField(20);
        filePathField.setEditable(false); // Make the file path field read-only
        chooseFileButton = new JButton("Choose File");
        filePanel.add(fileLabel);
        filePanel.add(filePathField);
        filePanel.add(chooseFileButton);


        JPanel inputPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel keyLabel = new JLabel("Enter Key:");
        keyField = new JTextField(10);
        okButton = new JButton("Verify");
        inputPanel.add(keyLabel);
        inputPanel.add(keyField);
        inputPanel.add(okButton);

        JPanel hashPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel hashLabel = new JLabel("Enter Hash:");
        JTextField hashField = new JTextField(20);
        hashField.setEditable(true);
        hashPanel.add(hashLabel);
        hashPanel.add(hashField);

        mainPanel.add(hashPanel, BorderLayout.SOUTH);

        mainPanel.add(filePanel, BorderLayout.NORTH);
        mainPanel.add(inputPanel, BorderLayout.CENTER);

        chooseFileButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                int response = fileChooser.showOpenDialog(null);
                if (response == JFileChooser.APPROVE_OPTION) {
                    File file = fileChooser.getSelectedFile();
                    String path = file.getAbsolutePath();
                    fileHash = MD5FileHash.doFinal(path);

                    filePathField.setText(path);
                }
            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                enteredHash = hashField.getText();// Get the user-entered hash
                KEY = keyField.getText();
                RSAFileEncryption2 rsaFileEncryption2 = null;
                try {
                    rsaFileEncryption2 = new RSAFileEncryption2(KEY);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
                try {
                    decryptedHash=rsaFileEncryption2.decryptString(enteredHash);
                    System.out.println(decryptedHash);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }

            }
        });
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                KEY = keyField.getText();
                try {
                    RSAFileEncryption2 rsaFileEncryption2 = new RSAFileEncryption2(KEY);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }


                if (fileHash.equals(decryptedHash)) {
                    JOptionPane.showMessageDialog(null, "Key matches. Not tampered.");
                } else {
                    JOptionPane.showMessageDialog(null, "Key does not match. Tampered.");
                }
            }
        });

        setContentPane(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Verifier myFrame = new Verifier();
        });
    }
}