/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JRadioButton;


/**
 * �����̃��W�I�{�^������I���ł���R���|�[�l���g�ł��D
 * <p>
 * TODO use ListModel
 * </p>
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 *          1.01 020515 nsano add getItems <br>
 */
public class RadioButtons extends JComponent {

    /** ���W�I�{�^���O���[�v */
    private Map<String, JRadioButton> radioButtons;

    /** */
    private int orientation;

    /**
     * ���W�I�{�^���O���[�v�̃R���|�[�l���g���\�z���܂��D
     */
    public RadioButtons() {
        radioButtons = new HashMap<String, JRadioButton>();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        final JRadioButton dummy = new JRadioButton();
        this.add(dummy);
        dummy.setVisible(false);
    }

    /**
     * ���W�I�{�^���̔z�u��ݒ肵�܂��D
     * 
     * @param orientation 0, 1
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;

        if (orientation == 0)
            this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        else
            this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        this.validate();
        this.repaint();
    }

    /** */
    public int getOrientation() {
        return orientation;
    }

    /**
     * ���W�I�{�^���̃e�L�X�g��ݒ肵�܂��D
     * 
     * @param items ���W�I�{�^���̃e�L�X�g�̔z��
     */
    public synchronized void setItems(String[] items) {

        this.removeAll();

        radioButtons.clear();

        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < items.length; i++) {
            JRadioButton button = new JRadioButton();
            button.setEnabled(isEnabled());
            button.setText(items[i]);
            group.add(button);
            this.add(button);
            radioButtons.put(items[i], button);
        }

        this.validate();
        this.repaint();
    }

    /**
     * ���W�I�{�^���̃e�L�X�g�̔z����擾���܂��D
     */
    public synchronized String[] getItems() {
        String[] items = new String[radioButtons.size()];

        Iterator<JRadioButton> e = radioButtons.values().iterator();
        int i = 0;
        while (e.hasNext()) {
            JRadioButton radioButton = e.next();
            items[i++] = radioButton.getText();
        }

        return items;
    }

    /**
     * �w�肵���e�L�X�g�̃��W�I�{�^����I�����܂��D
     * 
     * @param item �I���������{�^���̃e�L�X�g
     */
    public synchronized void setSelectedItem(String item) {
        JRadioButton selectedButton = radioButtons.get(item);
        if (selectedButton == null) {
            selectedButton = (JRadioButton) this.getComponent(0);
        }
        selectedButton.setSelected(true);

        validate();
        repaint();
    }

    /**
     * �I�����ꂽ���W�I�{�^���̃e�L�X�g���擾���܂��D
     */
    public String getSelectedItem() {
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            JRadioButton radioButton = e.next();
            if (radioButton.isSelected()) {
                return radioButton.getText();
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------

    /**
     * ���ׂẴR���|�[�l���g�� setEnabled ����悤�I�[�o�[���C�h���܂��D
     */
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            e.next().setEnabled(isEnabled);
        }
    }

    /**
     * ���ׂẴR���|�[�l���g�� add ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���X�i
     */
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            e.next().addMouseListener(l);
        }
    }

    /**
     * ���ׂẴR���|�[�l���g�� add ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���[�V�������X�i
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l);
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            e.next().addMouseMotionListener(l);
        }
    }

    /**
     * ���ׂẴR���|�[�l���g�� remove ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���X�i
     */
    public synchronized void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            e.next().removeMouseListener(l);
        }
    }

    /**
     * ���ׂẴR���|�[�l���g�� remove ����悤�I�[�o�[���C�h���܂��D
     * 
     * @param l �}�E�X���[�V�������X�i
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(l);
        Iterator<JRadioButton> e = radioButtons.values().iterator();
        while (e.hasNext()) {
            e.next().removeMouseMotionListener(l);
        }
    }
}

/* */
