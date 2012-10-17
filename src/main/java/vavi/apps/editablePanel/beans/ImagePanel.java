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
 * イメージを表示するコンポーネントです．
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 010830 nsano independent of application <br>
 */
public class ImagePanel extends JComponent {

    /** ボーダーの幅 */
    private static final int BORDER = 5;

    /** イメージ */
    private Image image = null;

    /**
     * イメージのパスを設定します．
     * 
     * @param imagePath
     */
    public synchronized void setImagePath(String imagePath) {
        // イメージデータを読み込んで表示
        if ("".equals(imagePath)) {
            Debug.println("blank name");
            setPreferredSize(new Dimension(32 + BORDER, 32 + BORDER));
            return;
        }

        // パス名が "/" で始まっていない場合は相対パス
        if (!new File(imagePath).isAbsolute()) {
            imagePath = imagePath + File.separator + imagePath;
            Debug.println("relative path: " + imagePath);
        } else {
            Debug.println("absolute path: " + imagePath);
        }

        // イメージが確実にロードされるのを保証する
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

        // コンポーネントの幅をイメージに合わせる。
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
     * イメージを描画します．
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
