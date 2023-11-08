package DocSignVerification;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Welcome {
    public static void main(String[] args) {
        WelcomeFrame wf = new WelcomeFrame();
    }
}

class WelcomeFrame extends JFrame{
    public WelcomeFrame(){
        setLayout(new FlowLayout());
        this.setTitle("Document Signature Verification");
        this.setVisible(true);
        this.setSize(400,400);
        JLabel l1 = new JLabel("Role");
        ButtonGroup radioGroup = new ButtonGroup();
        JRadioButton signer = new JRadioButton("Signer");

        JRadioButton verifier = new JRadioButton("Verifier");
        radioGroup.add(signer);
        radioGroup.add(verifier);
        JButton confirm = new JButton("Confirm");
        confirm.setSize(40, 20);
        this.add(l1);
        this.add(signer);
        this.add(verifier);
        this.add(confirm);
        confirm.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (signer.isSelected()){
                    new Signer2();
                    dispose();
                }
                else if(verifier.isSelected()){
                    new Signer();
                    dispose();
                }

            }
        });
    }
}