/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Customizer;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * {@link vavi.apps.editablePanel.beans.Slider} のカスタマイザです．．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano let it beans <br>
 */
public class SliderCustomizer extends JComponent implements Customizer {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** 編集対象 */
    private Slider slider;

    /** 最小値のフィールド */
    private JTextField minField = new JTextField();

    /** 最大値のフィールド */
    private JTextField maxField = new JTextField();

    /** 目盛りの分割数のフィールド */
    private JTextField divField = new JTextField();

    /** スライダーのプロパティエディタ */
    public SliderCustomizer() {

        this.setLayout(new GridLayout(3, 2));
        this.add(new JLabel(rb.getString("sliderCustomizer.label.min")));
        this.add(minField);
        this.add(new JLabel(rb.getString("sliderCustomizer.label.max")));
        this.add(maxField);
        this.add(new JLabel(rb.getString("sliderCustomizer.label.div")));
        this.add(divField);

        minField.addActionListener(actionListener);
        maxField.addActionListener(actionListener);
        divField.addActionListener(actionListener);
    }

    /** TODO */
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            slider.setMinimum(new Double(minField.getText()).doubleValue());
            slider.setMaximum(new Double(maxField.getText()).doubleValue());
            slider.setDividingCount(new Integer(divField.getText()).intValue());
        }
    };

    /**
     * 編集対象を設定します．
     */
    public void setObject(Object object) {
        slider = (Slider) object;

        minField.removeActionListener(actionListener);
        maxField.removeActionListener(actionListener);
        divField.removeActionListener(actionListener);

        minField.setText(String.valueOf(slider.getMinimum()));
        maxField.setText(String.valueOf(slider.getMaximum()));
        divField.setText(String.valueOf(slider.getDividingCount()));

        minField.addActionListener(actionListener);
        maxField.addActionListener(actionListener);
        divField.addActionListener(actionListener);
    }
}

/* */
