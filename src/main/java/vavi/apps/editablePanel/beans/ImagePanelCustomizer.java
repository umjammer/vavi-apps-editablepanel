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
 * {@link vavi.apps.editablePanel.beans.ImagePanel} のプロパティエディタです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020515 nsano initial version <br>
 */
public class ImagePanelCustomizer extends JComponent implements Customizer {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.swing.resource", Locale.getDefault());

    /** 編集対象 */
    private ImagePanel imagePanel;

    /** イメージのファイルチューザ */
    private JFileChooserTextField imageChooser = new JFileChooserTextField();

    /** イメージパネルのプロパティエディタ */
    public ImagePanelCustomizer() {

        this.setLayout(new BorderLayout());
        JLabel l = new JLabel(rb.getString("imagePanelCustomizer.label"));
        this.add(l, BorderLayout.WEST);
        this.add(imageChooser);

        imageChooser.addPropertyChangeListener(pcl);
    }

    /** */
    private PropertyChangeListener pcl = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            // Debug.println(ev.getSource().getClass().getName());
            String name = ev.getPropertyName();
            if ("text".equals(name)) {
                imagePanel.setImagePath((String) ev.getNewValue());
            } else if ("selectedFile".equals(name)) {
                imagePanel.setImagePath(((File) ev.getNewValue()).getAbsolutePath());
            }
        }
    };

    /** 編集対象を設定します． */
    public void setObject(Object object) {
        imagePanel = (ImagePanel) object;
    }
}

/* */
