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
 * ブラウザを表示するヘルプボタンです．
 * 
 * @todo browser property
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 */
public class HelpButton extends JButton {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** ブラウザで表示する URL */
    private String url;

    /**
     * ブラウザを表示するヘルプボタンを構築します．
     */
    public HelpButton() {
        setText(rb.getString("common.button.help.text"));
        addActionListener(helpActionListener);
    }

    /** ボタンが押された処理。ブラウザを起動 */
    private ActionListener helpActionListener = new ActionListener() {
        /** ローカルファイルにあるブラウザのパス */
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
     * ブラウザで表示する URL をセットします．
     * 
     * @param url
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * ブラウザで表示する URL を取得します．
     */
    public String getUrl() {
        return url;
    }

    // -------------------------------------------------------------------------

    /** プロパティ */
    private static Properties props = new Properties();

    /**
     * 初期化します．
     */
    static {
        // プロパティファイルのパス
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
