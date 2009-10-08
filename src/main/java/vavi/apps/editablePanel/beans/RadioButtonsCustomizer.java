/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.beans.Customizer;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.StringTokenizer;

import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTextField;


/**
 * {@link vavi.apps.editablePanel.beans.RadioButtons} �̃J�X�^�}�C�U�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020515 nsano initial version <br>
 */
public class RadioButtonsCustomizer extends JComponent implements Customizer {

    /** ���\�[�X�o���h�� */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** �ҏW�Ώ� */
    private RadioButtons radioButtons;

    /** ���W�I�{�^���̃A�C�e����ҏW����t�B�[���h */
    private JTextField items = new JTextField();

    /** ���W�I�{�^���̔z�u�����̃R���{�{�b�N�X */
    private JComboBox orientation = new JComboBox();

    /** ���W�I�{�^���̃v���p�e�B�G�f�B�^ */
    public RadioButtonsCustomizer() {

        this.setAutoscrolls(true);
        this.setLayout(new GridLayout(2, 2));
        this.add(new JLabel(rb.getString("radioButtonsCustomizer.label.items")));
        this.add(items);
        this.add(new JLabel(rb.getString("radioButtonsCustomizer.label.orientation")));
        this.add(orientation);

        //
        items.addActionListener(itemsListener);

        //
        orientation.addItem(rb.getString("radioButtonsCustomizer.orientation.horizontal"));
        orientation.addItem(rb.getString("radioButtonsCustomizer.orientation.vertical"));

        orientation.addItemListener(orientationListener);
    }

    /** */
    private ActionListener itemsListener = new ActionListener() {
        public void actionPerformed(ActionEvent ev) {
            StringTokenizer st = new StringTokenizer(items.getText(), " \t,");
            String[] values = new String[st.countTokens()];
            int i = 0;
            while (st.hasMoreTokens()) {
                values[++i] = st.nextToken();
            }
            radioButtons.setItems(values);
        }
    };

    /** ���W�I�{�^���̔z�u�����̃R���{�{�b�N�X�̃��X�i */
    private ItemListener orientationListener = new ItemListener() {
        public void itemStateChanged(ItemEvent ev) {
            radioButtons.setOrientation(orientation.getSelectedIndex());
        }
    };

    /**
     * �ҏW�Ώۂ�ݒ肵�܂��D
     */
    public void setObject(Object object) {
        radioButtons = (RadioButtons) object;

        orientation.removeItemListener(orientationListener);
        orientation.setSelectedIndex(radioButtons.getOrientation());
        orientation.addItemListener(orientationListener);

        String value = new String();
        String[] values = radioButtons.getItems();
        for (int i = 0; i < values.length; i++) {
            value += ", " + values[i + 1];
        }

        items.removeActionListener(itemsListener);
        items.setText(value.substring(2));
        items.addActionListener(itemsListener);
    }
}

/* */
