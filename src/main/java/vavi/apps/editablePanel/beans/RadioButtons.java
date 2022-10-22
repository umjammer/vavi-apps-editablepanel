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
 * 複数のラジオボタンから選択できるコンポーネントです．
 * <p>
 * TODO use ListModel
 * </p>
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 *          1.01 020515 nsano add getItems <br>
 */
public class RadioButtons extends JComponent {

    /** ラジオボタングループ */
    private Map<String, JRadioButton> radioButtons;

    /** */
    private int orientation;

    /**
     * ラジオボタングループのコンポーネントを構築します．
     */
    @SuppressWarnings("unused")
    public RadioButtons() {
        radioButtons = new HashMap<String, JRadioButton>();

        this.setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        final JRadioButton dummy = new JRadioButton();
        this.add(dummy);
        dummy.setVisible(false);
    }

    /**
     * ラジオボタンの配置を設定します．
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
     * ラジオボタンのテキストを設定します．
     * 
     * @param items ラジオボタンのテキストの配列
     */
    public synchronized void setItems(String[] items) {

        this.removeAll();

        radioButtons.clear();

        ButtonGroup group = new ButtonGroup();

        for (String item : items) {
            JRadioButton button = new JRadioButton();
            button.setEnabled(isEnabled());
            button.setText(item);
            group.add(button);
            this.add(button);
            radioButtons.put(item, button);
        }

        this.validate();
        this.repaint();
    }

    /**
     * ラジオボタンのテキストの配列を取得します．
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
     * 指定したテキストのラジオボタンを選択します．
     * 
     * @param item 選択したいボタンのテキスト
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
     * 選択されたラジオボタンのテキストを取得します．
     */
    public String getSelectedItem() {
        for (JRadioButton radioButton : radioButtons.values()) {
            if (radioButton.isSelected()) {
                return radioButton.getText();
            }
        }
        return null;
    }

    // -------------------------------------------------------------------------

    /**
     * すべてのコンポーネントに setEnabled するようオーバーライドします．
     */
    public void setEnabled(boolean isEnabled) {
        super.setEnabled(isEnabled);
        for (JRadioButton jRadioButton : radioButtons.values()) {
            jRadioButton.setEnabled(isEnabled);
        }
    }

    /**
     * すべてのコンポーネントに add するようオーバーライドします．
     * 
     * @param l マウスリスナ
     */
    public synchronized void addMouseListener(MouseListener l) {
        super.addMouseListener(l);
        for (JRadioButton jRadioButton : radioButtons.values()) {
            jRadioButton.addMouseListener(l);
        }
    }

    /**
     * すべてのコンポーネントに add するようオーバーライドします．
     * 
     * @param l マウスモーションリスナ
     */
    public synchronized void addMouseMotionListener(MouseMotionListener l) {
        super.addMouseMotionListener(l);
        for (JRadioButton jRadioButton : radioButtons.values()) {
            jRadioButton.addMouseMotionListener(l);
        }
    }

    /**
     * すべてのコンポーネントに remove するようオーバーライドします．
     * 
     * @param l マウスリスナ
     */
    public synchronized void removeMouseListener(MouseListener l) {
        super.removeMouseListener(l);
        for (JRadioButton jRadioButton : radioButtons.values()) {
            jRadioButton.removeMouseListener(l);
        }
    }

    /**
     * すべてのコンポーネントに remove するようオーバーライドします．
     * 
     * @param l マウスモーションリスナ
     */
    public synchronized void removeMouseMotionListener(MouseMotionListener l) {
        super.removeMouseMotionListener(l);
        for (JRadioButton jRadioButton : radioButtons.values()) {
            jRadioButton.removeMouseMotionListener(l);
        }
    }
}

/* */
