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
 * {@link vavi.apps.editablePanel.beans.Slider} �̃v���p�e�B�G�f�B�^�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano let it beans <br>
 */
public class SliderEditor extends PropertyEditorSupport implements PropertyEditor {

    /** �X���C�_�[�̃v���p�e�B�G�f�B�^ */
    private JComponent customEditor = new SliderCustomizer();

    /**
     * �J�X�^���G�f�B�^���T�|�[�g���܂��D
     */
    public boolean supportsCustomEditor() {
        return true;
    }

    /**
     * �����̃v���p�e�B�G�f�B�^��Ԃ��܂��D
     */
    public Component getCustomEditor() {
        return customEditor;
    }
}

/* */
