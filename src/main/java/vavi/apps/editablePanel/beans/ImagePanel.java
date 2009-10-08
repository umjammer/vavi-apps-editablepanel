/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel.beans;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.MediaTracker;
import java.awt.Toolkit;
import java.awt.image.ImageObserver;
import java.io.File;
import java.util.logging.Level;

import javax.swing.JComponent;

import vavi.util.Debug;


/**
 * �C���[�W��\������R���|�[�l���g�ł��D
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 */
public class ImagePanel extends JComponent {

    /** �{�[�_�[�̕� */
    private static final int BORDER = 5;

    /** �C���[�W */
    private Image image = null;

    /**
     * �C���[�W�̃p�X��ݒ肵�܂��D
     * 
     * @param imagePath
     */
    public synchronized void setImagePath(String imagePath) {
        // �C���[�W�f�[�^��ǂݍ���ŕ\��
        if ("".equals(imagePath)) {
            Debug.println("blank name");
            setPreferredSize(new Dimension(32 + BORDER, 32 + BORDER));
            return;
        }

        // �p�X���� "/" �Ŏn�܂��Ă��Ȃ��ꍇ�͑��΃p�X
        if (!new File(imagePath).isAbsolute()) {
            imagePath = imagePath + File.separator + imagePath;
            Debug.println("relative path: " + imagePath);
        } else {
            Debug.println("absolute path: " + imagePath);
        }

        // �C���[�W���m���Ƀ��[�h�����̂�ۏ؂���
        try {
            MediaTracker tracker = new MediaTracker(this);
            image = Toolkit.getDefaultToolkit().getImage(imagePath);
            tracker.addImage(image, 0);
            tracker.waitForAll();
        } catch (InterruptedException e) {
            Debug.println(Level.SEVERE, e);
            image = null;
        }

        // Dimension d = getSize();
        // Debug.println(d.width + ", " + d.height);

        // �R���|�[�l���g�̕����C���[�W�ɍ��킹��B
        // int w = Math.max(d.width, image.getWidth(this));
        // int h = Math.max(d.height, image.getHeight(this));
        int w = image.getWidth(this);
        int h = image.getHeight(this);

        // Dimension d = getPreferredSize();
        // Debug.println(w + ", " + h);
        // Debug.println(d.width + ", " + d.height);

        setPreferredSize(new Dimension(w + BORDER, h + BORDER));

        revalidate();
        repaint();
    }

    /** TODO */
    public String getImagePath() {
        return null;
    }

    /**
     * �C���[�W��`�悵�܂��D
     */
    public void paint(Graphics g) {
        super.paint(g);

        if (image == null || !g.drawImage(image, 0, 0, this))
            g.drawString("image N/A", 10, 10);
    }

    /** */
    public boolean imageUpdate(Image img, int infoflags, int x, int y, int width, int height) {
        if ((infoflags & ImageObserver.ALLBITS) == ImageObserver.ALLBITS) {
            repaint();
            return false;
        } else {
            return true;
        }
    }
}

/* */
