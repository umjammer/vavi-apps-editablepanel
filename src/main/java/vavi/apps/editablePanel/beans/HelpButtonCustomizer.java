/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.BorderLayout;
import java.beans.Customizer;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JComponent;
import javax.swing.JLabel;

import vavi.swing.JFileChooserTextField;


/**
 * {@link vavi.apps.editablePanel.beans.HelpButton} �̃J�X�^�}�C�U�ł��D
 * <p>
 * TODO JFileChooserURLTextField
 * </p>
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020515 nsano initial version <br>
 */
public class HelpButtonCustomizer extends JComponent implements Customizer {

    /** ���\�[�X�o���h�� */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** �ҏW�Ώ� */
    private HelpButton helpButton;

    /** �w���v�̃t�@�C���`���[�U */
    private JFileChooserTextField helpChooser = new JFileChooserTextField();

    /** �w���v�{�^���̃v���p�e�B�G�f�B�^ */
    public HelpButtonCustomizer() {

        this.setLayout(new BorderLayout());
        JLabel l = new JLabel(rb.getString("helpButtonCustomizer.label"));
        this.add(l, BorderLayout.WEST);
        this.add(helpChooser);

        helpChooser.setText("http://");
        helpChooser.addPropertyChangeListener(pcl);
    }

    /** �t�@�C���`���[�U�̃��X�i */
    private PropertyChangeListener pcl = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            String name = ev.getPropertyName();
            if ("text".equals(name)) {
                helpButton.setUrl((String) ev.getNewValue());
            } else if ("selectedFile".equals(name)) {
                helpButton.setUrl(((File) ev.getNewValue()).toString());
            }
        }
    };

    /**
     * �ҏW�Ώۂ�ݒ肵�܂��D
     */
    public void setObject(Object object) {
        helpButton = (HelpButton) object;
    }
}

/* */
