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
 * {@link vavi.apps.editablePanel.beans.Slider} のプロパティエディタです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano let it beans <br>
 */
public class SliderEditor extends PropertyEditorSupport implements PropertyEditor {

    /** スライダーのプロパティエディタ */
    private JComponent customEditor = new SliderCustomizer();

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
