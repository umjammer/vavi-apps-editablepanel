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
 * {@link vavi.apps.editablePanel.beans.ImagePanel} の BeanInfo です．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 020510 nsano initial version <br>
 */
public class ImagePanelBeanInfo extends SimpleBeanInfo {

    /** */
    private final static Class<?> beanClass = ImagePanel.class;

    /** */
    private final Class<?> customEditorClass = ImagePanelCustomizer.class;

    /** */
    public BeanDescriptor getBeanDescriptor() {
        return new BeanDescriptor(beanClass, customEditorClass);
    }

    /** */
    public Image getIcon(int iconKind) {
        if (iconKind == BeanInfo.ICON_MONO_16x16 || iconKind == BeanInfo.ICON_COLOR_16x16) {
            Image image = loadImage("resources/ImagePanel.gif");
            return image;
        }
        return null;
    }
}

/* */
