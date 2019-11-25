package ru.hse.alyokhina;

import javax.swing.*;
import java.util.Hashtable;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class FractalSettingsFrame extends JFrame {
    private final Fractal fractal;
    private final JSlider maxIterSlider;
    private final JSlider RSlider;
    private final JSlider cXSlider;
    private final JSlider cYSlider;
    ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();


    FractalSettingsFrame(final Fractal fractal,
                         final int width,
                         final int height) {
        this.fractal = fractal;
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        Box box = Box.createVerticalBox();

        box.add(new JLabel(" множество Жюлиа"));
        box.add(new JLabel(" f(z) = z2 + с"));
        box.add(new JLabel(" c = cX + cY * i"));

        final JButton autoButton = new JButton(fractal.getAuto() ? "Stop" : "Auto");
        autoButton.addActionListener(e -> {
            fractal.revertAuto();
            autoButton.setText(fractal.getAuto() ? "Stop" : "Auto");
        });
        box.add(autoButton);

        maxIterSlider = new JSlider(100, 300, fractal.getMaxIter());
        maxIterSlider.setPaintLabels(true);
        maxIterSlider.setMajorTickSpacing(50);
        maxIterSlider.addChangeListener(e -> {
            int maxIter = ((JSlider) e.getSource()).getValue();
            fractal.setMaxIter(maxIter);
        });
        box.add(new JLabel("Количество итераций"));
        box.add(maxIterSlider);


        RSlider = new JSlider(2, 202, ((int) fractal.getR() * 20));
        Hashtable<Integer, JLabel> rLabelTable = new Hashtable<>();
        rLabelTable.put(2, new JLabel("0.1"));
        rLabelTable.put(200, new JLabel("10"));
        rLabelTable.put(26, new JLabel("1.3"));
        rLabelTable.put(100, new JLabel("5"));
        rLabelTable.put(150, new JLabel("7.5"));
        rLabelTable.put(80, new JLabel("4"));
        RSlider.setLabelTable(rLabelTable);
        RSlider.setPaintLabels(true);
        RSlider.setMajorTickSpacing(50);
        RSlider.addChangeListener(e -> {
            float R = ((JSlider) e.getSource()).getValue() / 20.0f;
            fractal.setR(R);
        });
        box.add(new JLabel("порог сходимости"));
        box.add(RSlider);


        cXSlider = new JSlider(100, 300, ((int) (fractal.getParameterX() * 100f) + 200));
        cXSlider.setPaintLabels(true);
        Hashtable<Integer, JLabel> cXLabelTable = new Hashtable<>();
        cXLabelTable.put(100, new JLabel("-1"));
        cXLabelTable.put(300, new JLabel("1"));
        cXLabelTable.put(200, new JLabel("0.0"));
        cXSlider.setLabelTable(cXLabelTable);
        cXSlider.addChangeListener(e -> {
            float cX = (((JSlider) e.getSource()).getValue() - 200f) / 100f;
            fractal.setParameterX(cX);
        });
        box.add(new JLabel("cX"));
        box.add(cXSlider);

        cYSlider = new JSlider(100, 300, ((int) (fractal.getParameterY() * 100f) + 200));
        cYSlider.setPaintLabels(true);
        cYSlider.setLabelTable(cXLabelTable);
        cYSlider.addChangeListener(e -> {
            float cY = (((JSlider) e.getSource()).getValue() - 200f) / 100f;
            fractal.setParameterY(cY);
        });
        box.add(new JLabel("cY"));
        box.add(cYSlider);

        setContentPane(box);
        setSize(250, height);
        service.scheduleAtFixedRate(this::update, 0, 10, TimeUnit.MILLISECONDS);
    }

    private void update() {
        cXSlider.setValue(((int) (fractal.getParameterX() * 100f) + 200));
        cYSlider.setValue(((int) (fractal.getParameterY() * 100f) + 200));
        RSlider.setValue(((int) fractal.getR() * 20));
        maxIterSlider.setValue(fractal.getMaxIter());
    }
}
