/*
 * Copyright (c) 2001 by Naohide Sano, All rights reserved.
 *
 * Programmed by Naohide Sano
 */

package vavi.apps.editablePanel;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Vector;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import vavi.awt.event.EventPlug;
import vavi.awt.event.EventPlugSupport;
import vavi.swing.containertree.ComponentTreeNode;
import vavi.swing.containertree.JContainerTree;
import vavi.swing.event.EditorEvent;
import vavi.swing.event.EditorListener;
import vavi.swing.event.EditorSupport;
import vavi.util.Debug;


/**
 * ���f���̃c���[�r���[�ƕ��i�p���b�g�̃t���[���ł��D
 * 
 * @depends ./EditablePanelResource${I18N}.properties
 * 
 * @author <a href="mailto:vavivavi@yahoo.co.jp">Naohide Sano</a> (nsano)
 * @version 0.00 010823 nsano initial version <br>
 *          1.00 020510 nsano rewrite almost <br>
 */
public class ComponentTreeFrame extends JFrame {

    /** ���\�[�X�o���h�� */
    private static final ResourceBundle rb = ResourceBundle.getBundle("vavi.apps.editablePanel.EditablePanelResource", Locale.getDefault());

    /** tool buttons */
    private JButton showButton;

    private JButton hideButton;

    private JButton deleteButton;

    /** component tree */
    private JTree tree;

    /** */
    private ComponentTreeNode root;

    /** TODO wanna be deprecated */
    private DefaultTreeModel treeModel;

    /** */
    private EventPlugSupport eps = new EventPlugSupport();

    /** */
    private Container container;

    /**
     * �c���[�r���[�̃t���[�����\�z���܂��D
     * 
     * container ���[�g�ƂȂ�R���e�i�̃R���g���[��
     */
    public ComponentTreeFrame() {

        this.setTitle(rb.getString("ctFrame.title"));
        // this.setResizable(false);
        this.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        this.getContentPane().setLayout(new BorderLayout());

        // tree
        tree = new JContainerTree();

        tree.setAutoscrolls(true);
        tree.addTreeSelectionListener(tsl);

        JScrollPane sp = new JScrollPane(tree);

        // tool
        Box p = new Box(BoxLayout.Y_AXIS);
        // JPanel p = new JPanel(new GridLayout(0, 1));

        showButton = new JButton(showAction);
        showButton.setToolTipText(rb.getString("ctFrame.text.show"));

        hideButton = new JButton(hideAction);
        hideButton.setToolTipText(rb.getString("ctFrame.text.hide"));

        deleteButton = new JButton(deleteAction);
        deleteButton.setToolTipText(rb.getString("ctFrame.text.delete"));

        int h = showButton.getPreferredSize().height;
        int w1 = showButton.getPreferredSize().width;
        int w2 = hideButton.getPreferredSize().width;
        int w3 = deleteButton.getPreferredSize().width;
        int w = Math.max(Math.max(w1, w2), w3);
// Debug.println(w + ", " + h + "(" + w1 + ", " + w2 + ", " + w3 + ")");
        Dimension d = new Dimension(w, h);
        showButton.setMaximumSize(d);
        hideButton.setMaximumSize(d);
        deleteButton.setMaximumSize(d);
        p.add(showButton);
        p.add(hideButton);
        p.add(deleteButton);

        // tool + tree
        JPanel panel = new JPanel(new BorderLayout());
        panel.setPreferredSize(new Dimension(320, 400));
        panel.add(p, BorderLayout.WEST);
        panel.add(sp, BorderLayout.CENTER);

        this.getContentPane().add(panel, BorderLayout.CENTER);

        //
        eps.addEventPlug(new EventPlug("el of editor", this, null));
        eps.addEventPlug(new EventPlug("el for editor", null, el1));
        eps.addEventPlug(new EventPlug("el for container", null, el2));
    }

    /** */
    public EventPlugSupport getEventPlugSupport() {
        return eps;
    }

    /** */
    private void plug() {
        eps.setConnected("el of editor", true);
        eps.setConnected("el for container", true);
    }

    /** */
    private void unplug() {
        eps.setConnected("el of editor", false);
        eps.setConnected("el for container", false);

        setActionState();
    }

    // internal actions -------------------------------------------------------

    /** �폜�{�^���̏��� */
    private Action deleteAction = new AbstractAction(rb.getString("ctFrame.button.delete")) {
        public void actionPerformed(ActionEvent ev) {
            fireEditorUpdated(new EditorEvent(this, "delete"));
            setActionState();
        }
    };

    /** �\���{�^���̏��� */
    private Action showAction = new AbstractAction(rb.getString("ctFrame.button.show")) {
        /** */
        public void actionPerformed(ActionEvent ev) {
            TreePath[] selected = tree.getSelectionPaths();
            if (selected == null)
                return;

            for (int i = 0; i < selected.length; i++) {
                ComponentTreeNode node = (ComponentTreeNode) selected[i].getLastPathComponent();
                Component c = (Component) node.getUserObject();
                fireEditorUpdated(new EditorEvent(this, "show", c));
            }

            setActionState();
        }
    };

    /** ��\���{�^���̏��� */
    private Action hideAction = new AbstractAction(rb.getString("ctFrame.button.hide")) {
        public void actionPerformed(ActionEvent ev) {
            TreePath[] selected = tree.getSelectionPaths();
            if (selected == null) {
                return;
            }

            for (int i = 0; i < selected.length; i++) {
                ComponentTreeNode node = (ComponentTreeNode) selected[i].getLastPathComponent();
                Component c = (Component) node.getUserObject();
                fireEditorUpdated(new EditorEvent(this, "hide", c));
            }

            setActionState();
        }
    };

    /** */
    private void setActionState() {
        int visibleCount = 0;
        int invisibleCount = 0;
        boolean includeContainer = false;

        TreePath[] selected = tree.getSelectionPaths();

        if (selected != null) {
            for (int i = 0; i < selected.length; i++) {
                ComponentTreeNode node = (ComponentTreeNode) selected[i].getLastPathComponent();
                Component component = (Component) node.getUserObject();
                if (component != container) {
                    if (component.isVisible()) {
                        visibleCount++;
                    } else {
                        invisibleCount++;
                    }
                } else {
                    includeContainer = true;
                }
            }
        }

        showButton.setEnabled(invisibleCount > 0);
        hideButton.setEnabled(visibleCount > 0);

        deleteButton.setEnabled(!includeContainer);
    }

    // internal listener ------------------------------------------------------

    /**
     * �c���[�̑I�����ύX���ꂽ�ꍇ�ɌĂ΂�܂��D
     */
    private TreeSelectionListener tsl = new TreeSelectionListener() {
        public void valueChanged(TreeSelectionEvent ev) {

            TreePath[] selected = tree.getSelectionPaths();
            if (selected == null) {
                // Debug.printStackTrace(new Exception());
                return;
            }

            // Debug.println("here");
            Vector<Component> selection = new Vector<Component>();

            for (int i = 0; i < selected.length; i++) {

                ComponentTreeNode node = (ComponentTreeNode) selected[i].getLastPathComponent();
                Component component = (Component) node.getUserObject();
                selection.addElement(component);
                // Debug.println(component.getName());
            }

            fireEditorUpdated(new EditorEvent(this, "select", selection));
        }
    };

    // functions --------------------------------------------------------------

    /**
     * �c���[��̑S�p�X�����f���̖��O���L�[�ɕԂ��܂��D
     */
    private Map<String, TreePath> getAllTreePath() {
        Map<String, TreePath> allTreePath = new HashMap<String, TreePath>();
        for (int i = 0; i < treeModel.getChildCount(root) + 1; i++) {
            TreePath tp = tree.getPathForRow(i);
            allTreePath.put(tp.getLastPathComponent().toString(), tp);
        }
        return allTreePath;
    }

    /**
     * �V�����c���[�r���[�̃m�[�h���쐬���܂��D
     * 
     * @see java.awt.event.ContainerListener#componentAdded(java.awt.event.ContainerEvent)
     */
    private void addView(Component component) {

        // Debug.println(Debug.getCallerMethod(2));
        Map<String, TreePath> allTreePath = getAllTreePath();
        String name = component.getName();
        if (allTreePath.containsKey(name)) {
            Debug.println("already exists: " + component.getName());
            return;
        }

        // �m�[�h�쐬
        root.add(new ComponentTreeNode(component));
        treeModel.setRoot(root);

        setActionState();
    }

    /**
     * �c���[�r���[�̃m�[�h���폜���܂��D
     * 
     * @see java.awt.event.ContainerListener#componentRemoved(java.awt.event.ContainerEvent)
     */
    private void deleteView(Component component) {

        Map<String, TreePath> allTreePath = getAllTreePath();
        String name = component.getName();
        if (!allTreePath.containsKey(name)) {
            Debug.println("no such node: " + component.getName());
            return;
        }

        ComponentTreeNode node = (ComponentTreeNode) allTreePath.get(name).getLastPathComponent();

        // �c���[����m�[�h������
        root.remove(node);
        treeModel.setRoot(root);

        setActionState();
    }

    /**
     * �c���[�r���[�̃m�[�h��I�����܂��D
     * 
     * @see EditorListener#editorUpdated(EditorEvent)
     */
    public synchronized void selectView(List<Component> selected) {
        tree.removeTreeSelectionListener(tsl); // �ċA���Ȃ��悤��

        tree.getSelectionModel().clearSelection();

        if (selected.size() > 0) {
            Map<String, TreePath> allTreePath = getAllTreePath();

            for (int i = 0; i < selected.size(); i++) {
                String name = selected.get(i).getName();
                TreePath tp = allTreePath.get(name);
                tree.getSelectionModel().addSelectionPath(tp);
            }
        }

        setActionState();

        tree.addTreeSelectionListener(tsl); // ���A
    }

    // listeners for external -------------------------------------------------

    /** < {setEditable,close}@editor */
    private EditorListener el1 = new EditorListener() {
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
//Debug.println(Debug.SPECIAL, name + ": " + Debug.getTopCallerMethod("vavi"));
            if ("setEditable".equals(name)) {
                if (((Boolean) ev.getArgument()).booleanValue()) {
                    plug();
                    pack();
                    setVisible(true);
                } else {
                    unplug();
                    setVisible(false);
                }
            } else if ("close".equals(name)) {
                setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                setVisible(false);
                dispose();
            } else if ("load".equals(name)) {
                Container container = (Container) ev.getArgument();
                ComponentTreeFrame.this.container = container;

                root = new ComponentTreeNode(container);
                treeModel = new DefaultTreeModel(root);
                tree.setModel(treeModel);

                for (int i = 0; i < container.getComponentCount(); i++) {
                    Component component = container.getComponent(i);
                    addView(component);
                }
            }
        }
    };

    /**
     * < {select,add,remove}@editor
     */
    private EditorListener el2 = new EditorListener() {
        @SuppressWarnings("unchecked")
        public void editorUpdated(EditorEvent ev) {
            String name = ev.getName();
            if ("select".equals(name)) {
                List<Component> selected = (List) ev.getArgument();
                selectView(selected);
            } else if ("add".equals(name)) {
                Component component = (Component) ev.getArgument();
                addView(component);
            } else if ("remove".equals(name)) {
                Component component = (Component) ev.getArgument();
                deleteView(component);
            }
        }
    };

    // -------------------------------------------------------------------------

    /** EditorEvent �@�\�̃��[�e�B���e�B */
    private EditorSupport editorSupport = new EditorSupport();

    /** Editor ���X�i�[���A�^�b�`���܂��D */
    public void addEditorListener(EditorListener l) {
        editorSupport.addEditorListener(l);
    }

    /** Editor ���X�i�[�������[�u���܂��D */
    public void removeEditorListener(EditorListener l) {
        editorSupport.removeEditorListener(l);
    }

    /** */
    private void fireEditorUpdated(EditorEvent ev) {
        editorSupport.fireEditorUpdated(ev);
    }

    // -------------------------------------------------------------------------

    public static void main(String[] args) {
        JFrame frame = new ComponentTreeFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

/* */
