/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel;

import java.awt.AWTException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.MouseEvent;
import java.beans.XMLDecoder;
import java.beans.XMLEncoder;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.logging.Level;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;

import vavi.awt.Selectable;
import vavi.awt.containereditor.ContainerEditor;
import vavi.awt.containereditor.basic.BasicContainerEditor;
import vavi.awt.event.ComponentSelectionEvent;
import vavi.awt.event.ComponentSelectionListener;
import vavi.awt.event.EventPlugSupport;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;
import vavi.util.RegexFileFilter;


/**
 * のメインモジュールです．
 *
 * @depends ${JDK_HOME}/lib/dt.jar
 * @depends ./EditablePanelResource${I18N}.properties
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 020507 nsano fix Model version <br>
 *          2.00 020510 nsano beans version <br>
 *          3.00 020605 nsano remove controller <br>
 */
public class EditablePanel extends JComponent {

    /** リソースバンドル */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.editablePanel.EditablePanelResource", Locale.getDefault());

    /** コンテナエディタ */
    private ContainerEditor containerEditor;

    /** コンテナ */
    private Container container;

    /** メニューフレーム */
    private MenuFrame menuFrame;

    /** ツリービュー */
    private ComponentTreeFrame componentTreeFrame;

    /** プロパティシート */
    private PropertyEditorFrame propertyEditorFrame;

    /**
     * 編集可能パネルを構築します．
     */
    public EditablePanel() {

        // default target object
        container = new JPanel();
        container.setLayout(null);
        container.setName(getNewName("JPanel"));
        container.setSize(new Dimension(500, 500));
// Debug.println(((JPanel) container).getPreferredSize());
        ((JPanel) container).setOpaque(true);
        container.setBackground(Color.white);

        // self
        container.setPreferredSize(container.getSize());
        this.setLayout(new BorderLayout());
        this.add(container, BorderLayout.CENTER);
        this.addEditorListener(el); // < sel,file,setEd

        // container editor
        containerEditor = new BasicContainerEditor(container);
        containerEditor.setMouseInputAction(mil); // < add
        containerEditor.addEditorListener(epel); // < cp,lo,loc,b,sel

        // menu
        menuFrame = new MenuFrame();
        menuFrame.setJMenuBar(createMenuBar());
        menuFrame.getContentPane().add(createToolBar(), BorderLayout.NORTH);
        EventPlugSupport eps = menuFrame.getEventPlugSupport();
        eps.setEventListener("csl of editor", mcsl); // < prepare
        eps.setInvoker("el for editor", this); // > setEd,cancel
        eps.setConnected(true);

        // property editor
        propertyEditorFrame = new PropertyEditorFrame();
        eps = propertyEditorFrame.getEventPlugSupport();
        eps.setInvoker("el for editor", this); // > setEd
        eps.setInvoker("el for container", this); // > loc,bounds,sel
        eps.setConnected("el for editor", true);

        // component tree
        componentTreeFrame = new ComponentTreeFrame();
        eps = componentTreeFrame.getEventPlugSupport();
        eps.setEventListener("el of editor", ctel);
        eps.setInvoker("el for editor", this); // > setEd
        eps.setInvoker("el for container", this); // > add,rm,sel
        eps.setConnected("el for editor", true);

        // popup menu
        // JEditorPopupMenu popup = new JEditorPopupMenu(containerEditor);
    }

    // -------------------------------------------------------------------------

    /** */
    private JDialog dialog = null;

    /** */
    protected JDialog createDialog(Component parent) throws HeadlessException {
        Frame frame = parent instanceof Frame ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        String title = rb.getString("version.title");

        JDialog dialog = new JDialog(frame, title, false);

        // 実行モードの際はフレームのサイズを変更できないようにする
        dialog.setResizable(containerEditor.isEditable());

        Container contentPane = dialog.getContentPane();
        contentPane.setLayout(new BorderLayout());
        contentPane.add(this, BorderLayout.CENTER);

        return dialog;
    }

    /**
     * ダイアログとして編集可能パネルを表示します．
     */
    public void showDialog(Component parent) throws HeadlessException {

        dialog = createDialog(parent);
        dialog.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);

        setEditable(false);
        fireEditorUpdated(new EditorEvent(this, "load", container));

        menuFrame.pack();
        menuFrame.setVisible(true);

        dialog.pack();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Dimension size = dialog.getSize();
        int y = (screenSize.height - size.height) / 2;
        int x = (screenSize.width - size.width) / 2;
        dialog.setLocation(new Point(x, y));
        dialog.setVisible(true);
    }

    // -------------------------------------------------------------------------

    /**
     * 閉じます．
     */
    private void close() {
        dialog.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        dialog.setVisible(false);
        dialog.dispose();
        dialog = null;
    }

    // -------------------------------------------------------------------------

    /** */
    private void setEditable(boolean isEditable) {
        // dialog
        dialog.setResizable(isEditable);
        // editable panel
        containerEditor.setEditable(isEditable);
        if (isEditable) {
            container.addContainerListener(cl); // < add,remove
        } else {
            container.removeContainerListener(cl); // < add,remove
        }
        // others
        fireEditorUpdated(new EditorEvent(this, "setEditable", new Boolean(isEditable)));
    }

    // -------------------------------------------------------------------------

    /** 用意された Component */
    private Component preparedComponent;

    /**
     * 準備された Componentを表示します．
     * 
     * @see #mil
     */
    private void finishToAdd(Point point) {
Debug.println(getClassName(preparedComponent));
        preparedComponent.setLocation(point.x, point.y);

        container.add(preparedComponent);
        container.validate();
        container.repaint();

        preparedComponent = null;
        // > JBeansTabbedPane
        fireEditorUpdated(new EditorEvent(this, "cancelToAdd"));
    }

    /**
     * Componentを表示する準備をします．
     * 
     * @see #cancelToAdd()
     */
    private synchronized void prepareToAdd(String className) {
Debug.println(className);
        try {
            @SuppressWarnings(value="unchecked")
            Class<Object> beanClass = (Class<Object>) Class.forName(className);
            Object bean = beanClass.newInstance();
            if (bean instanceof Component) {
                preparedComponent = (Component) bean;
            } else {
                preparedComponent = new BeanWrapper(bean);
            }
            // preparedComponent.setName(getNewName(getClassName(bean)));
            preparedComponent.setSize(new Dimension(40, 40));
        } catch (Exception e) {
            preparedComponent = null;
Debug.println(Level.SEVERE, e);
        }
    }

    /**
     * 準備された Component を破棄します．
     * 
     * @see #preparedComponent
     */
    @SuppressWarnings("unused")
    private synchronized void cancelToAdd() {
        if (preparedComponent != null) {
            preparedComponent = null;
        }
        // > JBeansTabbedPane
        fireEditorUpdated(new EditorEvent(this, "cancelToAdd"));
    }

    /**
     * ユニークな名前を返します．
     */
    private String getNewName(String baseName) {
        List<String> names = new ArrayList<>();
        for (int i = 0; i < container.getComponents().length; i++) {
            Component component = container.getComponent(i);
            names.add(component.getName());
// Debug.println(component.getName());
        }

        int count = 1;

        String newName = baseName + "_" + count;
        while (names.contains(newName)) {
            count++;
            newName = baseName + "_" + count;
// Debug.println(newName);
        }

        return newName;
    }

    /** Gets class name w/o package name. */
    private static String getClassName(Object object) {
        String className = object.getClass().getName();
        return className.substring(className.lastIndexOf(".") + 1);
    }

    // -------------------------------------------------------------------------

    /** < this */
    private EditorListener el = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
// Debug.println(name);
            if ("setEditable".equals(name)) {
                Boolean bool = (Boolean) ev.getArgument();
                boolean isEditable = bool.booleanValue();

                fileMenu.setEnabled(isEditable);
                editMenu.setEnabled(isEditable);
                alignMenu.setEnabled(isEditable);
                pasteAction.setEnabled(false);
                modeMenuItem.setState(isEditable);
// Debug.println(isEditable);
            } else if ("select".equals(name)) {
                @SuppressWarnings("unchecked")
                List<Selectable> selection = (List<Selectable>) ev.getArgument();
                setActionsState(selection);
            } else if ("file".equals(name)) {
                File file = (File) ev.getArgument();
                saveAction.setEnabled(file != null);
            }
        }
    };

    /** < containerEditor */
    private EditorListener epel = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("select".equals(name)) {
                fireEditorUpdated(ev);
            } else if ("copy".equals(name)) {
                pasteAction.setEnabled(true);
            } else if ("lostOwnership".equals(name)) {
                pasteAction.setEnabled(false);
            } else if ("location".equals(name)) {
                fireEditorUpdated(ev);
            } else if ("bounds".equals(name)) {
                fireEditorUpdated(ev);
            }
        }
    };

    /** */
    private void setActionsState(List<Selectable> selected) {
        int size = selected.size();
        if (size == 1 && selected.get(0) == container) {
            size = 0;
        }

        boolean flag = size > 0;

        cutAction.setEnabled(flag);
        copyAction.setEnabled(flag);
        deleteAction.setEnabled(flag);
        toFrontAction.setEnabled(flag);
        toBackAction.setEnabled(flag);

        flag = size > 1;

        alignLeftAction.setEnabled(flag);
        alignRightAction.setEnabled(flag);
        alignTopAction.setEnabled(flag);
        alignBottomAction.setEnabled(flag);
        alignJustifyHorizontalAction.setEnabled(flag);
        alignJustifyVerticalAction.setEnabled(flag);
        alignJustifyHorizontalGapAction.setEnabled(flag);
        alignJustifyVerticalGapAction.setEnabled(flag);
    }

    /** ComponentTreeFrame 用の EditorListener */
    private EditorListener ctel = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
// Debug.println(name);
            if ("select".equals(name)) {
                @SuppressWarnings("unchecked")
                List<Selectable> selection = (List<Selectable>) ev.getArgument();
                containerEditor.select(selection);
                setActionsState(selection);
            } else if ("show".equals(name)) {
                Component component = (Component) ev.getArgument();
                component.setVisible(true);
            } else if ("hide".equals(name)) {
                Component component = (Component) ev.getArgument();
                component.setVisible(false);
            } else if ("delete".equals(name)) {
                containerEditor.delete();
            }
        }
    };

    // -------------------------------------------------------------------------

    /** containerEditor 用の MouseInputListener */
    private MouseInputListener mil = new MouseInputAdapter() {
        private Cursor dropCursor;
        {
            try {
                dropCursor = Cursor.getSystemCustomCursor("CopyDrop.32x32");
            } catch (AWTException e) {
                System.err.println(e);
            }
        }

        /** */
        public void mouseMoved(MouseEvent ev) {
            if (preparedComponent != null) {
                ev.getComponent().setCursor(dropCursor);
            } else {
                ev.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        }

        /** Component 追加． */
        public void mouseReleased(MouseEvent ev) {
            if (preparedComponent != null) {
                finishToAdd(ev.getPoint());
                ev.getComponent().setCursor(Cursor.getDefaultCursor());
            }
        }
    };

    // -------------------------------------------------------------------------

    /**
     * < {add, remove}@containerEditor
     */
    private ContainerListener cl = new ContainerListener() {
        public void componentAdded(ContainerEvent ev) {
            Component component = ev.getChild();
            component.setName(getNewName(getClassName(component)));
Debug.println("-(+)------------: " + component.getName());
            for (int i = 0; i < container.getComponents().length; i++) {
                Component c = container.getComponent(i);
Debug.println(i + ": " + c.getName());
            }
Debug.println("----------------");
            fireEditorUpdated(new EditorEvent(this, "add", component));
        }

        public void componentRemoved(ContainerEvent ev) {
            Component component = ev.getChild();
Debug.println("-(-)------------: " + component.getName());
            for (int i = 0; i < container.getComponents().length; i++) {
                Component c = container.getComponent(i);
Debug.println(i + ": " + c.getName());
            }
Debug.println("----------------");
            fireEditorUpdated(new EditorEvent(this, "remove", component));
        }
    };

    /** < selection@palette */
    private ComponentSelectionListener mcsl = new ComponentSelectionListener() {
        public void valueChanged(ComponentSelectionEvent ev) {
            prepareToAdd(((JToggleButton) ev.getSelected()).getActionCommand());
        }
    };

    // -------------------------------------------------------------------------

    /** ファイルメニュー */
    private JMenu fileMenu;

    /** 編集メニュー */
    private JMenu editMenu;

    /** モードメニュー */
    private JMenu modeMenu;

    /** 配置メニュー */
    private JMenu alignMenu;

    /** ヘルプメニュー */
    private JMenu helpMenu;

    /** */
    private JCheckBoxMenuItem modeMenuItem;

    /**
     * メニューバーを作成します．
     */
    private JMenuBar createMenuBar() {

        JMenuBar menuBar = new JMenuBar();

        // ファイル

        fileMenu = new JMenu(rb.getString("menu.file"));
        fileMenu.setEnabled(false);

        fileMenu.add(openAction);
        fileMenu.add(saveAction);
        fileMenu.add(saveAsAction);
        fileMenu.addSeparator();
        fileMenu.add(exitAction);

        menuBar.add(fileMenu);

        // 編集

        editMenu = new JMenu(rb.getString("menu.edit"));
        editMenu.setEnabled(false);

        editMenu.add(cutAction);
        editMenu.add(copyAction);
        editMenu.add(pasteAction);
        editMenu.add(deleteAction);
        editMenu.addSeparator();
        editMenu.add(selectAllAction);

        menuBar.add(editMenu);

        // 配置

        alignMenu = new JMenu(rb.getString("menu.align"));
        alignMenu.setEnabled(false);

        alignMenu.add(alignLeftAction);
        alignMenu.add(alignRightAction);
        alignMenu.add(alignTopAction);
        alignMenu.add(alignBottomAction);
        alignMenu.addSeparator();
        alignMenu.add(alignJustifyHorizontalAction);
        alignMenu.add(alignJustifyVerticalAction);
        alignMenu.addSeparator();
        alignMenu.add(alignJustifyHorizontalGapAction);
        alignMenu.add(alignJustifyVerticalGapAction);
        alignMenu.addSeparator();
        alignMenu.add(toFrontAction);
        alignMenu.add(toBackAction);

        menuBar.add(alignMenu);

        // モード

        modeMenu = new JMenu(rb.getString("menu.mode"));

        modeMenuItem = new JCheckBoxMenuItem(changeModeAction);
        modeMenuItem.setState(false);
        modeMenu.add(modeMenuItem);

        menuBar.add(modeMenu);

        // ヘルプ

        helpMenu = new JMenu(rb.getString("menu.help"));

        helpMenu.add(showManualAction);
        helpMenu.addSeparator();
        helpMenu.add(showVersionAction);

        menuBar.add(helpMenu);

        return menuBar;
    }

    /**
     * ツールバーを作成します．
     */
    private JToolBar createToolBar() {

        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        // 編集

        JButton button = toolBar.add(cutAction);
        // Object[]o=((AbstractAction) cutAction).getKeys();
        // for(int i=0;i<o.length;i++)Debug.println(o[i]);
        button.setToolTipText((String) cutAction.getValue("Name"));
        button = toolBar.add(copyAction);
        button.setToolTipText((String) copyAction.getValue("Name"));
        button = toolBar.add(pasteAction);
        button.setToolTipText((String) pasteAction.getValue("Name"));
        button = toolBar.add(deleteAction);
        button.setToolTipText((String) deleteAction.getValue("Name"));

        return toolBar;
    }

    // -------------------------------------------------------------------------

    /** */
    private File file;

    /** */
    private static final RegexFileFilter fileFilter = new RegexFileFilter(".+\\.xml", "XML File");

    /** ファイルを開くメニュー */
    private Action openAction = new AbstractAction(rb.getString("action.open"), (ImageIcon) UIManager.get("editablePanel.openIcon")) {
        private JFileChooser fc = new JFileChooser();
        {
            File cwd = new File(System.getProperty("user.home"));
            fc.setCurrentDirectory(cwd);
            fc.setFileFilter(fileFilter);
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                if (fc.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = fc.getSelectedFile();
                fireEditorUpdated(new EditorEvent(this, "file", file));
                fc.setCurrentDirectory(file);
                load();
            } catch (Exception e) {
Debug.printStackTrace(e);
                JOptionPane.showMessageDialog(null, rb.getString("action.open.error"), rb.getString("dialog.title.error"), JOptionPane.ERROR_MESSAGE);
                file = null;
                fireEditorUpdated(new EditorEvent(this, "file", file));
            }
        }
    };

    /** 画面情報を保存するメニュー */
    private Action saveAction = new AbstractAction(rb.getString("action.save"), (ImageIcon) UIManager.get("editablePanel.saveIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                if (file == null) {
                    saveAsAction.actionPerformed(ev);
                    return;
                }
                save();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, rb.getString("action.save.error"), rb.getString("dialog.title.error"), JOptionPane.ERROR_MESSAGE);
                file = null;
                fireEditorUpdated(new EditorEvent(this, "file", file));
            }
        }
    };

    /** 名前をつけて保存するアクション */
    private Action saveAsAction = new AbstractAction(rb.getString("action.saveAs"), (ImageIcon) UIManager.get("editablePanel.saveAsIcon")) {
        private JFileChooser fc = new JFileChooser();
        {
            File cwd = new File(System.getProperty("user.home"));
            fc.setCurrentDirectory(cwd);
            fc.setFileFilter(fileFilter);
        }

        public void actionPerformed(ActionEvent ev) {
            try {
                if (fc.showSaveDialog(null) != JFileChooser.APPROVE_OPTION) {
                    return;
                }
                file = fc.getSelectedFile();
                fireEditorUpdated(new EditorEvent(this, "file", file));
                fc.setCurrentDirectory(file);
                save();
            } catch (Exception e) {
                JOptionPane.showMessageDialog(null, rb.getString("action.save.error"), rb.getString("dialog.title.error"), JOptionPane.ERROR_MESSAGE);
            }
        }
    };

    /** */
    private void load() throws IOException {

        InputStream is = new BufferedInputStream(new FileInputStream(file));
        Debug.println("open: " + file);
        //
        XMLDecoder xd = new XMLDecoder(is);
        container = (Container) xd.readObject();
        xd.close();

        containerEditor.setContainer(container);
        fireEditorUpdated(new EditorEvent(this, "load", container));

        this.validate();
        this.repaint();
    }

    /** */
    private void save() throws IOException {
        OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
Debug.println("save: " + file);

        XMLEncoder xe = new XMLEncoder(os);
        xe.writeObject(container);
        xe.close();
    }

    /** 終了するアクション */
    private Action exitAction = new AbstractAction(rb.getString("action.exit")) {
        public void actionPerformed(ActionEvent ev) {
            SwingUtilities.invokeLater(() -> {
                close();
            });
            System.exit(0);
        }
    };

    // -------------------------------------------------------------------------

    /** */
    private Action changeModeAction = new AbstractAction(rb.getString("menuItem.mode")) {
        /** モードの切り替えメニューの処理 */
        public void actionPerformed(ActionEvent ev) {
            boolean isEditable = modeMenuItem.getState();
            setEditable(isEditable);
        }
    };

    // -------------------------------------------------------------------------

    /** すべて選択するアクション */
    private Action selectAllAction = new AbstractAction(rb.getString("action.selectAll")) {
        public void actionPerformed(ActionEvent ev) {
            containerEditor.selectAll();
        }
    };

    // -------------------------------------------------------------------------

    /** カットするアクション */
    private Action cutAction = new AbstractAction(rb.getString("action.cut"), (ImageIcon) UIManager.get("editablePanel.cutIcon")) {
        public void actionPerformed(ActionEvent ev) {
            containerEditor.cut();
        }
    };

    /** コピーするアクション */
    private Action copyAction = new AbstractAction(rb.getString("action.copy"), (ImageIcon) UIManager.get("editablePanel.copyIcon")) {
        public void actionPerformed(ActionEvent ev) {
            containerEditor.copy();
        }
    };

    /** 貼り付けするアクション */
    private Action pasteAction = new AbstractAction(rb.getString("action.paste"), (ImageIcon) UIManager.get("editablePanel.pasteIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.paste();
        }
    };

    /** 削除するアクション */
    private Action deleteAction = new AbstractAction(rb.getString("action.delete"), (ImageIcon) UIManager.get("editablePanel.deleteIcon")) {
        public void actionPerformed(ActionEvent ev) {
            containerEditor.delete();
        }
    };

    /** 上にそろえるアクション */
    private Action alignTopAction = new AbstractAction(rb.getString("action.alignTop"), (ImageIcon) UIManager.get("editablePanel.alignTopIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignTop();
        }
    };

    /** 下にそろえるアクション */
    private Action alignBottomAction = new AbstractAction(rb.getString("action.alignBottom"), (ImageIcon) UIManager.get("editablePanel.alignBottomIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignBottom();
        }
    };

    /** 左にそろえるアクション */
    private Action alignLeftAction = new AbstractAction(rb.getString("action.alignLeft"), (ImageIcon) UIManager.get("editablePanel.alignLeftIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignLeft();
        }
    };

    /** 右にそろえるアクション */
    private Action alignRightAction = new AbstractAction(rb.getString("action.alignRight"), (ImageIcon) UIManager.get("editablePanel.alignRightIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignRight();
        }
    };

    /** 幅をそろえるアクション */
    private Action alignJustifyHorizontalAction = new AbstractAction(rb.getString("action.alignJustifyHorizontal"), (ImageIcon) UIManager.get("editablePanel.alignJustifyHorizontalIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignWidth();
        }
    };

    /** 高さをそろえるアクション */
    private Action alignJustifyVerticalAction = new AbstractAction(rb.getString("action.alignJustifyVertical"), (ImageIcon) UIManager.get("editablePanel.alignJustifyVerticalIcon")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignHeight();
        }
    };

    /** 水平間隔を均等にするアクション */
    private Action alignJustifyHorizontalGapAction = new AbstractAction(rb.getString("action.alignJustifyHorizontalGap")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignJustifyHorizontalGap();
        }
    };

    /** 垂直間隔を均等にするアクション */
    private Action alignJustifyVerticalGapAction = new AbstractAction(rb.getString("action.alignJustifyVerticalGap")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.alignJustifyVerticalGap();
        }
    };

    /** 最前面に配置するアクション */
    private Action toFrontAction = new AbstractAction(rb.getString("action.toFront")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.toFront();
        }
    };

    /** 最背面に配置するアクション */
    private Action toBackAction = new AbstractAction(rb.getString("action.toBack")) {
        {
            setEnabled(false);
        }

        public void actionPerformed(ActionEvent ev) {
            containerEditor.toBack();
        }
    };

    // -------------------------------------------------------------------------

    /** マニュアルを表示するアクション */
    private Action showManualAction = new AbstractAction(rb.getString("action.showManual"), (ImageIcon) UIManager.get("editablePanel.showManualIcon")) {
        public void actionPerformed(ActionEvent ev) {
            try {
                Runtime.getRuntime().exec(new String[] {props.getProperty("ep.path.browser"), props.getProperty("ep.url.manual")});
            } catch (Exception e) {
                Debug.println(Level.SEVERE, "Cannot show the manual: " + e);
            }
        }
    };

    /** バージョン情報を表示するアクション */
    private Action showVersionAction = new AbstractAction(rb.getString("action.showVersion"), (ImageIcon) UIManager.get("editablePanel.showVersionIcon")) {
        public void actionPerformed(ActionEvent ev) {
            JOptionPane.showMessageDialog(null, rb.getString("version.title") + "\n" + rb.getString("version.revision") + "\n" + rb.getString("version.build") + "\n" + rb.getString("version.copyright"), rb.getString("dialog.title.showVersion"), JOptionPane.INFORMATION_MESSAGE);
        }
    };

    // -------------------------------------------------------------------------

    /** The editor support */
    private EditorSupport editorSupport = new EditorSupport();

    /** Adds an editor listener. */
    public void addEditorListener(EditorListener l) {
        editorSupport.addEditorListener(l);
    }

    /** Removes an editor listener. */
    public void removeEditorListener(EditorListener l) {
        editorSupport.removeEditorListener(l);
    }

    /** */
    private void fireEditorUpdated(EditorEvent ev) {
        editorSupport.fireEditorUpdated(ev);
    }

    // -------------------------------------------------------------------------

    /** プロパティ */
    private static Properties props = new Properties();

    /**
     * 初期化します．
     */
    static {
        try {
            Toolkit t = Toolkit.getDefaultToolkit();
            Class<?> c = EditablePanel.class;

            String path = "EditablePanel.properties";
            props.load(c.getResourceAsStream(path));

            UIDefaults table = UIManager.getDefaults();

            int i = 0;
            while (true) {
                String key = "ep.action." + i + ".iconName";
                String val = props.getProperty(key);
                if (val == null) {
                    Debug.println("no property for: ep.action." + i + ".iconName");
                    break;
                }

                key = "ep.action." + i + ".icon";
                String icon = props.getProperty(key);

Debug.println(Level.FINE, "icon: " + icon);
                table.put(val, new ImageIcon(t.getImage(c.getResource(icon))));

                i++;
            }

            path = "/toolbarButtonGraphics/development/Bean24.gif";
            table.put("ep.beanWrapperIcon", t.getImage(c.getResource(path)));
        } catch (IOException e) {
            throw new IllegalStateException(e);
        }
    }

    // -------------------------------------------------------------------------

    /** */
    private class BeanWrapper extends JComponent {
        private Image image = (Image) UIManager.get("ep.beanWrapperIcon");

        private Object bean;

        BeanWrapper(Object bean) {
            this.bean = bean;
        }

        @SuppressWarnings("unused")
        public Object getBean() {
            return bean;
        }

        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(image, 0, 0, this);
        }
    }

    // -------------------------------------------------------------------------

    /**
     * Tests the Panel Designer
     */
    public static void main(String[] args) {

        // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

        EditablePanel ep = new EditablePanel();
        ep.showDialog(null);
    }
}

/* */
