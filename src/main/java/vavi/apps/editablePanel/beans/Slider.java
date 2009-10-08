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
 * �g���X���C�_�[�R���|�[�l���g�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 */
public class Slider extends JComponent {

    /** �搔 */
    private int powerCount = -1;

    /** */
    private double minimum;

    /** */
    private double maximum;

    /** */
    private int dividingCount;

    /** �X���C�_�[ */
    private JSlider slider;

    /** �e�L�X�g�p�p�l�� */
    private JPanel labelPanel;

    /** �e�L�X�g */
    private JTextField label;

    /**
     * �g���X���C�_�[���\�z���܂��D
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

    /** �����_�ȉ������܂ł��邩��ݒ肷��D */
    private void setPowerCount() {
        // �ŏ��E�ő�l�ݒ肪�����_���܂ނ��ǂ����`�F�b�N����B
        int minFP = getFloatingPoint(minimum);
        int maxFP = getFloatingPoint(maximum);

        powerCount = Math.max(minFP, maxFP);
    }

    /** */
    private ChangeListener changeListener = new ChangeListener() {
        public void stateChanged(ChangeEvent ev) {
            setLabelText();
        }
    };

    /** */
    private ActionListener actionListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            setValue(new Double(label.getText()).doubleValue());
            setLabelText();
        }
    };

    /** */
    private void setLabelText() {
        label.setText(getDoubleString(getValidatedDouble(slider.getValue())));
    }

    /**
     * �X���C�_�[�̍ŏ��l��ݒ肵�܂��D
     * 
     * @param minimum
     */
    public void setMinimum(double minimum) {
        this.minimum = minimum;
        slider.setMinimum(getValidatedInt(minimum));
    }

    /**
     * �X���C�_�[�̍ŏ��l���擾���܂��D
     */
    public double getMinimum() {
        return minimum;
    }

    /**
     * �X���C�_�[�̍ő�l��ݒ肵�܂��D
     * 
     * @param maximum
     */
    public void setMaximum(double maximum) {
        this.maximum = maximum;
        slider.setMaximum(getValidatedInt(maximum));
    }

    /**
     * �X���C�_�[�̍ő�l���擾���܂��D
     */
    public double getMaximum() {
        return maximum;
    }

    /**
     * �X���C�_�[�̒l��ݒ肵�܂��D
     */
    public void setValue(double value) {
        // double �̒l���t�ɓK������悤�� int �ɂ��Ȃ��Ă͂Ȃ�Ȃ� !!
        slider.setValue(getValidatedInt(value));

        label.validate();
        label.repaint();
    }

    /**
     * �X���C�_�[�̒l���擾���܂��D
     */
    public double getValue() {
        return new Double(label.getText()).doubleValue();
    }

    /**
     * �X���C�_�[�̖ڐ���̕�����ݒ肵�܂��D
     * 
     * @param dividingCount �ڐ���̕�����
     */
    public void setDividingCount(int dividingCount) {
        this.dividingCount = dividingCount;

        int dif = slider.getMaximum() - slider.getMinimum();
        double majorTick = dif / dividingCount;
        slider.setMajorTickSpacing(new Double(majorTick).intValue());
        Hashtable<Integer, JLabel> dic = getLabelTable(slider.getMajorTickSpacing());
        slider.setLabelTable(dic);
    }

    /**
     * �X���C�_�[�̖ڐ���̕��������擾���܂��D
     */
    public int getDividingCount() {
        return dividingCount;
    }

    /**
     * �X���C�_�[�̃��x���e�[�u�����쐬���܂��D
     */
    private Hashtable<Integer, JLabel> getLabelTable(int majorSpace) {
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
            dic.put(new Integer(intKey), new JLabel(key));
            cnt++;
        }

        return dic;
    }

    /**
     * double �l���X���C�_�[�ŕ\���\�� int �l�ɕϊ����܂��D
     */
    private int getValidatedInt(double d) {
        setPowerCount();

        if (powerCount != -1) {
            d = d * Math.pow(10, powerCount);
        }

        return new Double(d).intValue();
    }

    /**
     * int �l���X���C�_�[�̗L�������ɏ]���� double �l�ɕϊ����܂��D
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
     * double �l���X���C�_�[�̗L�������ɏ]���� String �ɕϊ����܂��D
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
     * �����_�����܂ŗL������Ԃ��܂��D
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
     * ���ׂẴR���|�[�l���g�� setEnabled ����悤�ɃI�[�o�[���C�h���܂��D
     */
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        slider.setEnabled(isEnabled);
        label.setEnabled(isEnabled);
        labelPanel.setEnabled(isEnabled);
    }

    /**
     * ���ׂẴR���|�[�l���g�� add ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���X�i
     */
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        slider.addMouseListener(l);
        label.addMouseListener(l);
        labelPanel.addMouseListener(l);
    }

    /**
     * ���ׂẴR���|�[�l���g�� add ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���[�V�������X�i
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l);
        slider.addMouseMotionListener(l);
        label.addMouseMotionListener(l);
        labelPanel.addMouseMotionListener(l);
    }

    /**
     * ���ׂẴR���|�[�l���g�� remove ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���X�i
     */
    public synchronized void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        slider.removeMouseListener(l);
        label.removeMouseListener(l);
        labelPanel.removeMouseListener(l);
    }

    /**
     * ���ׂẴR���|�[�l���g�� remove ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���[�V�������X�i
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(l);
        slider.removeMouseMotionListener(l);
        label.removeMouseMotionListener(l);
        labelPanel.removeMouseMotionListener(l);
    }
}

/* */
