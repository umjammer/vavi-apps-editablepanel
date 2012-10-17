/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.Component;
import java.beans.PropertyEditor;
import java.beans.PropertyEditorSupport;

import javax.swing.JComponent;


/**
 * {@link vavi.apps.editablePanel.beans.RadioButtons} のプロパティエディタです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020515 nsano initial version <br>
 *          1.00 010830 nsano let it beans <br>
 */
public class RadioButtonsEditor extends PropertyEditorSupport implements PropertyEditor {

    /** ラジオボタンのプロパティエディタ */
    private JComponent customEditor = new RadioButtonsCustomizer();

    /**
     * カスタムエディタをサポートします．
     */
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * 内蔵のプロパティエディタを返します．
     */
    public Component getCustomEditor() {
        return customEditor;
    }
}

/* */
