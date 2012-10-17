/*
 * Copyright (c) 2002 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.Image;
import java.beans.BeanDescriptor;
import java.beans.BeanInfo;
import java.beans.SimpleBeanInfo;


/**
 * {@link vavi.apps.editablePanel.beans.RadioButtons} の BeanInfo です．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020510 nsano initial version <br>
 */
public class RadioButtonsBeanInfo extends SimpleBeanInfo {

    /** */
    private final static Class<?> beanClass = RadioButtons.class;

    /** */
    private final Class<?> customEditorClass = RadioButtonsCustomizer.class;

    /** */
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(beanClass, customEditorClass);
    }

    /** */
    public Image getIcon(int iconKind) {
        if (iconKind == BeanInfo.ICON_MONO_16x16 || iconKind == BeanInfo.ICON_COLOR_16x16) {
            Image image = loadImage("resources/RadioButtons.gif");
            return image;
        }
        return null;
    }
}

/* */
