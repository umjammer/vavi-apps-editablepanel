/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.JButton;

import vavi.apps.editablePanel.EditablePanel;
import vavi.util.Debug;


/**
 * �u���E�U��\������w���v�{�^���ł��D
 * 
 * @todo browser property
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 */
public class HelpButton extends JButton {

    /** ���\�[�X�o���h�� */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** �u���E�U�ŕ\������ URL */
    private String url;

    /**
     * �u���E�U��\������w���v�{�^�����\�z���܂��D
     */
    public HelpButton() {
        setText(rb.getString("common.button.help.text"));
        addActionListener(helpActionListener);
    }

    /** �{�^���������ꂽ�����B�u���E�U���N�� */
    private ActionListener helpActionListener = new ActionListener() {
        /** ���[�J���t�@�C���ɂ���u���E�U�̃p�X */
        final String browserPath = props.getProperty("ep.path.browser");

        public void actionPerformed(ActionEvent ev) {
            try {
                Runtime.getRuntime().exec(browserPath + " " + url);
            } catch (Exception e) {
                Debug.println(Level.SEVERE, "browser run error: " + e);
            }
        }
    };

    /**
     * �u���E�U�ŕ\������ URL ���Z�b�g���܂��D
     * 
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * �u���E�U�ŕ\������ URL ���擾���܂��D
     */
    public String getUrl() {
        return url;
    }

    // -------------------------------------------------------------------------

    /** �v���p�e�B */
    private static Properties props = new Properties();

    /**
     * ���������܂��D
     */
    static {
        // �v���p�e�B�t�@�C���̃p�X
        final String path = "EditablePanel.properties";

        try {
            props.load(EditablePanel.class.getResourceAsStream(path));
        } catch (Exception e) {
            Debug.println(Level.SEVERE, "no properties file");
            System.exit(1);
        }
    }
}

/* */
