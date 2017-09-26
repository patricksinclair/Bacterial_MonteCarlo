import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

class NutrientPanel extends JPanel{


    NutrientSystem nutriSys;

    public NutrientPanel(NutrientSystem nutriSys){
        this.nutriSys = nutriSys;
        setBackground(Color.BLACK);
    }

    @Override
    public void paintComponent(Graphics g){
        super.paintComponent(g);

        int L = nutriSys.getL();
        int K = nutriSys.getK();
        int w = getWidth()/L;
        int h = getHeight()/K;

        for(int l = 0; l < L; l++) {
            if(nutriSys.getMicrohabitat(l).getN() > 0){

                for(int k = 0; k < nutriSys.getMicrohabitat(l).getN(); k++) {

                    if(nutriSys.getBacteria(l, k).getM() == 1) {
                        g.setColor(Color.RED);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(nutriSys.getBacteria(l, k).getM() == 2) {
                        g.setColor(Color.ORANGE);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(nutriSys.getBacteria(l, k).getM() == 3) {
                        g.setColor(Color.YELLOW);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(nutriSys.getBacteria(l, k).getM() == 4) {
                        g.setColor(Color.GREEN);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(nutriSys.getBacteria(l, k).getM() == 5) {
                        g.setColor(Color.BLUE);
                        g.fillRect(w*l, h*k, w, l);
                    } else if(nutriSys.getBacteria(l, k).getM() == 6) {
                        g.setColor(Color.MAGENTA);
                        g.fillRect(w*l, h*k, w, l);
                    }else {
                        g.setColor(Color.DARK_GRAY);
                        g.fillRect(w*l, h*k, w, l);
                    }
                }
            }
        }
    }


    public void monteAnimate(){
        for(int i = 0; i < 10000; i++){
            nutriSys.performAction();
        }
        repaint();
    }

    public void updateAlpha(double newAlpha){
        nutriSys = new NutrientSystem(nutriSys.getL(), nutriSys.getK(), newAlpha, nutriSys.getS());
        repaint();
    }

    public void updateS(int newS){
        nutriSys = new NutrientSystem(nutriSys.getL(), nutriSys.getK(), nutriSys.getAlpha(), newS);
    }
}




public class NutrientFrame extends JFrame {

    int L = 500, K = 100, S = 100;
    double alpha = 0.05;

    NutrientPanel nutriPan;
    NutrientSystem nutriSys;
    Timer monteTimer;

    JButton goButton = new JButton("Go");
    //stuff for allowing GUI variance of alpha and S (no. of nutrients)
    JLabel alphaLabel = new JLabel("alpha: ");
    JTextField alphaField = new JTextField(String.valueOf(alpha), 10);
    JLabel sLabel = new JLabel("S: ");
    JTextField sField = new JTextField(String.valueOf(S), 10);

    public NutrientFrame(){

        nutriSys = new NutrientSystem(L, K, alpha, S);

        nutriPan = new NutrientPanel(nutriSys);
        nutriPan.setPreferredSize(new Dimension(1000, 200));

        JPanel controlPanel = new JPanel();
        controlPanel.add(goButton);
        controlPanel.add(alphaLabel);
        controlPanel.add(alphaField);

        getContentPane().add(nutriPan, BorderLayout.CENTER);
        getContentPane().add(controlPanel, BorderLayout.SOUTH);
        pack();

        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                e.getWindow().dispose();
                System.exit(0);
            }
        });

        setTitle("Bacterial Monte Carlo");
        setLocation(100, 20);
        setVisible(true);
        setBackground(Color.LIGHT_GRAY);

        monteAnimate();
        updateAlpha();
        updateS();
    }



    public void monteAnimate(){
        monteTimer = new Timer(0, (e)->{nutriPan.monteAnimate();});

        goButton.addActionListener((e)->{
            if(!monteTimer.isRunning()) {
                monteTimer.start();
                goButton.setText("Stop");
            }
            else {
                monteTimer.stop();
                goButton.setText("Go");
            }
        });
    }

    public void updateAlpha(){
        alphaField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                alphaField.setText("");
            }
        });

        alphaField.addActionListener((e)->{
            double alpha = Double.parseDouble(alphaField.getText());
            nutriPan.updateAlpha(alpha);
        });
    }

    public void updateS(){
        sField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                sField.setText("");
            }
        });

        sField.addActionListener((e)->{
            int S = Integer.parseInt((sField.getText()));
            nutriPan.updateS(S);
        });
    }
}
