/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import vavi.awt.event.EventPlug;
import vavi.awt.event.EventPlugSupport;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.propertyeditor.JPropertyEditorPanel;


/**
 * プロパティエディタのフレームです．
 * 
 * @depends ./EditablePanelResource${I18N}.properties
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 020513 nsano let it bean <br>
 */
public class PropertyEditorFrame extends JFrame {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.editablePanel.EditablePanelResource", Locale.getDefault());

    /** プロパティエディタ */
    private JPropertyEditorPanel propertyEditorPanel;

    /** */
    private EventPlugSupport eps = new EventPlugSupport();

    /**
     * プロパティエディタのフレームを構築します．
     */
    public PropertyEditorFrame() {

        this.setTitle(rb.getString("peFrame.title"));
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(wl);
        this.getContentPane().setLayout(new BorderLayout());

        propertyEditorPanel = new JPropertyEditorPanel();
        propertyEditorPanel.addPropertyChangeListener(ecl);
        this.getContentPane().add(propertyEditorPanel);
        //
        eps.addEventPlug(new EventPlug("el for editor", null, el1));
        eps.addEventPlug(new EventPlug("el for container", null, el2));
    }

    /** */
    public EventPlugSupport getEventPlugSupport() {
        return eps;
    }

    /**
     * プロパティエディタのフレームを初期化します．
     */
    private WindowListener wl = new WindowAdapter() {
        public void windowOpened(WindowEvent ev) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            Rectangle bounds = getBounds();
            setLocation((screenSize.width - bounds.width), bounds.y);
        }
    };

    // -------------------------------------------------------------------------

    /** internal for propertyEditorPanel */
    private PropertyChangeListener ecl = new PropertyChangeListener() {
        public void propertyChange(PropertyChangeEvent ev) {
            String name = ev.getPropertyName();
            Object newValue = ev.getNewValue();
            Object oldValue = ev.getOldValue();

            if ("message".equals(name)) {
                // Forward the message to the status bar.
                firePropertyChange("message", oldValue, newValue);
            } else if ("add".equals(name)) {
                // This is a message from the property panel.
                // Bean added should be added to the design panel.
                // designPanel.addBean(newValue);
            }
        }
    };

    // -------------------------------------------------------------------------

    /**
     * < {setEditable,close}@editor
     */
    private EditorListener el1 = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
// Debug.println(name + ": " + Debug.getCallerMethod(2));
            if ("setEditable".equals(name)) {
                if (((Boolean) ev.getArgument()).booleanValue()) {
                    eps.setConnected("el for container", true);
                    pack();
                    setVisible(true);
                } else {
                    eps.setConnected("el for container", false);
                    setVisible(false);
                }
            } else if ("close".equals(name)) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(false);
                dispose();
            }
        }
    };

    /**
     * < {select,location,bounds}@editor
     */
    private EditorListener el2 = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("select".equals(name)) {
                @SuppressWarnings("unchecked")
                final List<Component> selected = (List<Component>) ev.getArgument();
// Debug.println(selected.size());
                if (selected.size() != 1) {
                    propertyEditorPanel.setSelectedItem(null);
                } else {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            Component component = selected.get(0);
                            propertyEditorPanel.setSelectedItem(component);
                        }
                    });
                }
            } else if ("location".equals(name)) {
            } else if ("bounds".equals(name)) {
            }
        }
    };
}

/* */
