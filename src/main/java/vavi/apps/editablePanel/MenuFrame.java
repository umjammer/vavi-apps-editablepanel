/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.swing.JFrame;
import javax.swing.JScrollPane;

import vavi.awt.event.EventPlug;
import vavi.awt.event.EventPlugSupport;
import vavi.swing.JBeansTabbedPane;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;


/**
 * メニューのフレームです．
 *
 * @editor	receive	cancelToAdd		unselect button
 *		receive	setEditable		set enabled true or false
 *
 * @depends	./EditablePanelResource${I18N}.properties
 *
 * @author	<a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version	0.00	010823	nsano	initial version <br>
 *		0.01	020503	nsano	refine <br>
 */
public class MenuFrame extends JFrame {

    /** リソースバンドル */
    private static final ResourceBundle rb =
	ResourceBundle.getBundle(
	    "vavi.apps.editablePanel.EditablePanelResource",
	    Locale.getDefault());

    /** beans セレクタ */
    private JBeansTabbedPane palette;

    /** */
    private EventPlugSupport eps = new EventPlugSupport();

    /**
     * メニューのフレームを構築します．
     */
    public MenuFrame() {
	this.setTitle(rb.getString("menuFrame.title"));
	this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.addWindowListener(wl);
	this.setResizable(false);

	this.getContentPane().setLayout(new BorderLayout());

	// palette
	palette = new JBeansTabbedPane();
	JScrollPane sp = new JScrollPane(palette);
	this.getContentPane().add(sp, BorderLayout.CENTER);

        //
        eps.addEventPlug(new EventPlug("csl of editor", palette, null));
        eps.addEventPlug(new EventPlug("el for editor", null, el));
    }

    /** */
    public EventPlugSupport getEventPlugSupport() {
        return eps;
    }

    /**
     * メニューのフレームを初期化します．
     */
    private WindowListener wl = new WindowAdapter() {
        public void windowOpened(WindowEvent ev) {
            Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
            setLocation((screenSize.width - getSize().width) / 2, 0);
        }
    };

    /**
     * エディタから追加キャンセル，モード変更を受信します．
     */
    private EditorListener el = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("cancelToAdd".equals(name)) {
                palette.deselectAll();
//Debug.println("here");
            } else if ("setEditable".equals(name)) {
                boolean state = ((Boolean) ev.getArgument()).booleanValue();
                palette.setEnabled(state);
            } else if ("close".equals(name)) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(false);
                dispose();
            }
        }
    };
}

/* */
