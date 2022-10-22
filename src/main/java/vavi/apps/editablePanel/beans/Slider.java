/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Hashtable;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 * 拡張スライダーコンポーネントです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 */
public class Slider extends JComponent {

    /** 乗数 */
    private int powerCount = -1;

    /** */
    private double minimum;

    /** */
    private double maximum;

    /** */
    private int dividingCount;

    /** スライダー */
    private JSlider slider;

    /** テキスト用パネル */
    private JPanel labelPanel;

    /** テキスト */
    private JTextField label;

    /**
     * 拡張スライダーを構築します．
     */
    public Slider() {

        this.setLayout(new BorderLayout());

        label = new JTextField(4);
        label.addActionListener(actionListener);

        labelPanel = new JPanel();
        labelPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        labelPanel.add(label);
        this.add(labelPanel, BorderLayout.NORTH);

        slider = new JSlider();
        slider.setPaintLabels(true);
        slider.setPaintTicks(true);
        slider.addChangeListener(changeListener);
        this.add(slider, BorderLayout.CENTER);

        this.maximum = getValidatedDouble(slider.getMaximum());
        this.minimum = getValidatedDouble(slider.getMinimum());
        setDividingCount(10);

        setLabelText();
    }

    /** 小数点以下何桁まであるかを設定する． */
    private void setPowerCount() {
        // 最小・最大値設定が小数点を含むかどうかチェックする。
        int minFP = getFloatingPoint(minimum);
        int maxFP = getFloatingPoint(maximum);

        powerCount = Math.max(minFP, maxFP);
    }

    /** */
    private ChangeListener changeListener = ev -> setLabelText();

    /** */
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            setValue(new Double(label.getText()));
            setLabelText();
        }
    };

    /** */
    private void setLabelText() {
        label.setText(getDoubleString(getValidatedDouble(slider.getValue())));
    }

    /**
     * スライダーの最小値を設定します．
     * 
     * @param minimum
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
        slider.setMinimum(getValidatedInt(minimum));
    }

    /**
     * スライダーの最小値を取得します．
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * スライダーの最大値を設定します．
     * 
     * @param maximum
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
        slider.setMaximum(getValidatedInt(maximum));
    }

    /**
     * スライダーの最大値を取得します．
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * スライダーの値を設定します．
     */
    public void setValue(double value) {
        // double の値を逆に適合するように int にしなくてはならない !!
        slider.setValue(getValidatedInt(value));

        label.validate();
        label.repaint();
    }

    /**
     * スライダーの値を取得します．
     */
    public double getValue() {
        return new Double(label.getText());
    }

    /**
     * スライダーの目盛りの分割を設定します．
     * 
     * @param dividingCount 目盛りの分割数
     */
    public void setDividingCount(int dividingCount) {
        this.dividingCount = dividingCount;

        int dif = slider.getMaximum() - slider.getMinimum();
        double majorTick = (double) dif / dividingCount;
        slider.setMajorTickSpacing(new Double(majorTick).intValue());
        Hashtable<Integer, JLabel> dic = getLabelTable(slider.getMajorTickSpacing());
        slider.setLabelTable(dic);
    }

    /**
     * スライダーの目盛りの分割数を取得します．
     */
    public int getDividingCount() {
        return dividingCount;
    }

    /**
     * スライダーのラベルテーブルを作成します．
     */
    private Hashtable<Integer, JLabel> getLabelTable(int majorSpace) {
        @SuppressWarnings("unused")
        Hashtable<Integer, JLabel> dic = new Hashtable<Integer, JLabel>();
        if (majorSpace == 0) {
            return dic;
        }
        int cnt = 0;
        while (true) {
            int intKey = slider.getMinimum() + majorSpace * cnt;
            if (intKey > slider.getMaximum()) {
                break;
            }
            double dKey = getValidatedDouble(intKey);
            String key = getDoubleString(dKey);
            dic.put(intKey, new JLabel(key));
            cnt++;
        }

        return dic;
    }

    /**
     * double 値をスライダーで表示可能な int 値に変換します．
     */
    private int getValidatedInt(double d) {
        setPowerCount();

        if (powerCount != -1) {
            d = d * Math.pow(10, powerCount);
        }

        return new Double(d).intValue();
    }

    /**
     * int 値をスライダーの有効桁数に従った double 値に変換します．
     */
    private double getValidatedDouble(int originValue) {
        setPowerCount();

        if (powerCount == -1) {
            return originValue;
        }
        double value = originValue;
        double powValue = Math.pow(10, -1 * powerCount);
        value = value * powValue;
        return value;
    }

    /**
     * double 値をスライダーの有効桁数に従った String に変換します．
     */
    private String getDoubleString(double value) {
        String numberString = Double.toString(value);
        int comma = numberString.indexOf(".");
        if (comma != -1) {
            int cutIndex = comma + powerCount + 1;
            if (cutIndex > numberString.length()) {
                int dif = cutIndex - numberString.length();
                for (int i = 0; i < dif; i++) {
                    numberString = numberString + "0";
                }
            }
            numberString = numberString.substring(0, cutIndex);
        }
        return numberString;
    }

    /**
     * 小数点何桁まで有効かを返します．
     */
    private static int getFloatingPoint(double number) {
        String numberString = String.valueOf(number);
        int comma = numberString.indexOf(".");
        if (comma == -1) {
            return comma;
        }
        int length = numberString.length();
        return length - comma - 1;
    }

    // -------------------------------------------------------------------------

    /**
     * すべてのコンポーネントに setEnabled するようにオーバーライドします．
     */
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        slider.setEnabled(isEnabled);
        label.setEnabled(isEnabled);
        labelPanel.setEnabled(isEnabled);
    }

    /**
     * すべてのコンポーネントに add するようオーバーライドします．
     * 
     * @param l マウスリスナ
     */
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        slider.addMouseListener(l);
        label.addMouseListener(l);
        labelPanel.addMouseListener(l);
    }

    /**
     * すべてのコンポーネントに add するようオーバーライドします．
     * 
     * @param l マウスモーションリスナ
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l);
        slider.addMouseMotionListener(l);
        label.addMouseMotionListener(l);
        labelPanel.addMouseMotionListener(l);
    }

    /**
     * すべてのコンポーネントに remove するようオーバーライドします．
     * 
     * @param l マウスリスナ
     */
    public synchronized void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        slider.removeMouseListener(l);
        label.removeMouseListener(l);
        labelPanel.removeMouseListener(l);
    }

    /**
     * すべてのコンポーネントに remove するようオーバーライドします．
     * 
     * @param l マウスモーションリスナ
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(l);
        slider.removeMouseMotionListener(l);
        label.removeMouseMotionListener(l);
        labelPanel.removeMouseMotionListener(l);
    }
}

/* */
