package com.avast.android.butterknifezelezny.form;

import com.avast.android.butterknifezelezny.iface.ICancelListener;
import com.avast.android.butterknifezelezny.iface.IConfirmListener;
import com.avast.android.butterknifezelezny.iface.OnCheckBoxStateChangedListener;
import com.avast.android.butterknifezelezny.model.Element;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class EntryList extends JPanel {

    protected Project mProject;
    protected Editor mEditor;
    protected ArrayList<Element> mElements = new ArrayList<Element>();
    protected ArrayList<String> mGeneratedIDs = new ArrayList<String>();
    protected ArrayList<Entry> mEntries = new ArrayList<Entry>();
    protected boolean mCreateHolder = false;
    protected boolean mNeedInit=true;
    protected boolean mRToR2=false;

    protected String mPrefix = null;
    protected IConfirmListener mConfirmListener;
    protected ICancelListener mCancelListener;
    protected JCheckBox mPrefixCheck;
    protected JTextField mPrefixValue;
    protected JLabel mPrefixLabel;

    protected JCheckBox mHolderCheck;
    protected JCheckBox msplitOnclickMethodsCheck;
    protected JCheckBox mNeedInitCheck;
    protected JCheckBox mRToR2Check;
    protected JLabel mHolderLabel;
    protected JButton mConfirm;
    protected JButton mCancel;
    protected EntryHeader mEntryHeader;

    private OnCheckBoxStateChangedListener allCheckListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            for (final Entry entry : mEntries) {
                entry.setListener(null);
                entry.getCheck().setSelected(checked);
                entry.setListener(singleCheckListener);
            }
        }
    };

    private OnCheckBoxStateChangedListener singleCheckListener = new OnCheckBoxStateChangedListener() {
        @Override
        public void changeState(boolean checked) {
            boolean result = true;
            for (Entry entry : mEntries) {
                result &= entry.getCheck().isSelected();
            }

            mEntryHeader.setAllListener(null);
            mEntryHeader.getAllCheck().setSelected(result);
            mEntryHeader.setAllListener(allCheckListener);
        }
    };

    public EntryList(Project project, Editor editor, ArrayList<Element> elements, ArrayList<String> ids, boolean createHolder, IConfirmListener confirmListener, ICancelListener cancelListener) {
        mProject = project;
        mEditor = editor;
        mCreateHolder = createHolder;
        mConfirmListener = confirmListener;
        mCancelListener = cancelListener;
        if (elements != null) {
            mElements.addAll(elements);
        }
        if (ids != null) {
            mGeneratedIDs.addAll(ids);
        }

        setPreferredSize(new Dimension(640, 360));
        setLayout(new BoxLayout(this, BoxLayout.PAGE_AXIS));

        addInjections();
        addButtons();
    }

    protected void addInjections() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.PAGE_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        mEntryHeader = new EntryHeader();
        contentPanel.add(mEntryHeader);
        contentPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPanel injectionsPanel = new JPanel();
        injectionsPanel.setLayout(new BoxLayout(injectionsPanel, BoxLayout.PAGE_AXIS));
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        int cnt = 0;
        boolean selectAllCheck = true;
        for (Element element : mElements) {
            Entry entry = new Entry(this, element, mGeneratedIDs);
            entry.setListener(singleCheckListener);

            if (cnt > 0) {
                injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));
            }
            injectionsPanel.add(entry);
            cnt++;

            mEntries.add(entry);

            selectAllCheck &= entry.getCheck().isSelected();
        }
        mEntryHeader.getAllCheck().setSelected(selectAllCheck);
        mEntryHeader.setAllListener(allCheckListener);
        injectionsPanel.add(Box.createVerticalGlue());
        injectionsPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JBScrollPane scrollPane = new JBScrollPane(injectionsPanel);
        contentPanel.add(scrollPane);

        add(contentPanel, BorderLayout.CENTER);
        refresh();
    }

    protected void addButtons() {
        /*
		mPrefixCheck = new JCheckBox();
		mPrefixCheck.setPreferredSize(new Dimension(32, 26));
		mPrefixCheck.addChangeListener(new CheckPrefixListener());

		mPrefixValue = new JTextField(Utils.getPrefix(), 10);
		mPrefixValue.setPreferredSize(new Dimension(40, 26));

		mPrefixLabel = new JLabel();
		mPrefixLabel.setText("Field name prefix");

		JPanel prefixPanel = new JPanel();
		prefixPanel.setLayout(new BoxLayout(prefixPanel, BoxLayout.LINE_AXIS));
		prefixPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
		prefixPanel.add(mPrefixCheck);
		prefixPanel.add(mPrefixValue);
		prefixPanel.add(mPrefixLabel);
		prefixPanel.add(Box.createHorizontalGlue());
		add(prefixPanel, BorderLayout.PAGE_END);
		*/

        mHolderCheck = new JCheckBox();
        mHolderCheck.setPreferredSize(new Dimension(32, 26));
        mHolderCheck.setSelected(mCreateHolder);
        mHolderCheck.addChangeListener(new CheckHolderListener());

        mHolderLabel = new JLabel();
        mHolderLabel.setText("Create ViewHolder");

        JPanel holderPanel = new JPanel();
        holderPanel.setLayout(new BoxLayout(holderPanel, BoxLayout.LINE_AXIS));
        holderPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        holderPanel.add(mHolderCheck);
        holderPanel.add(mHolderLabel);
        holderPanel.add(Box.createHorizontalGlue());
        add(holderPanel, BorderLayout.PAGE_END);

        msplitOnclickMethodsCheck = new JCheckBox();
        msplitOnclickMethodsCheck.setPreferredSize(new Dimension(32, 26));
        msplitOnclickMethodsCheck.setSelected(false);

        final JLabel independentOnclickMethodsLabel = new JLabel();
        independentOnclickMethodsLabel.setText("Split OnClick methods");

        final JPanel splitOnclickMethodsPanel = new JPanel();
        splitOnclickMethodsPanel.setLayout(new BoxLayout(splitOnclickMethodsPanel, BoxLayout.LINE_AXIS));
        splitOnclickMethodsPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        splitOnclickMethodsPanel.add(msplitOnclickMethodsCheck);
        splitOnclickMethodsPanel.add(independentOnclickMethodsLabel);
        splitOnclickMethodsPanel.add(Box.createHorizontalGlue());
        add(splitOnclickMethodsPanel, BorderLayout.PAGE_END);

        mNeedInitCheck=new JCheckBox();
        mNeedInitCheck.setPreferredSize(new Dimension(32,26));
        mNeedInitCheck.setSelected(mNeedInit);

        final JLabel needInitLabel = new JLabel();
        needInitLabel.setText("initialize");
        final JPanel initPanel = new JPanel();
        initPanel.setLayout(new BoxLayout(initPanel, BoxLayout.LINE_AXIS));
        initPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        initPanel.add(mNeedInitCheck);
        initPanel.add(needInitLabel);
        initPanel.add(Box.createHorizontalGlue());
        add(initPanel, BorderLayout.PAGE_END);

        mRToR2Check=new JCheckBox();
        mRToR2Check.setPreferredSize(new Dimension(32,26));
        mRToR2Check.setSelected(mRToR2);

        final JLabel rToR2=new JLabel();
        rToR2.setText("R->R2");
        final JPanel rToR2Panel = new JPanel();
        rToR2Panel.setLayout(new BoxLayout(rToR2Panel, BoxLayout.LINE_AXIS));
        rToR2Panel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        rToR2Panel.add(mRToR2Check);
        rToR2Panel.add(rToR2);
        rToR2Panel.add(Box.createHorizontalGlue());
        add(rToR2Panel, BorderLayout.PAGE_END);


        mCancel = new JButton();
        mCancel.setAction(new CancelAction());
        mCancel.setPreferredSize(new Dimension(120, 26));
        mCancel.setText("Cancel");
        mCancel.setVisible(true);

        mConfirm = new JButton();
        mConfirm.setAction(new ConfirmAction());
        mConfirm.setPreferredSize(new Dimension(120, 26));
        mConfirm.setText("Confirm");
        mConfirm.setVisible(true);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.LINE_AXIS));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(Box.createHorizontalGlue());
        buttonPanel.add(mCancel);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(mConfirm);

        add(buttonPanel, BorderLayout.PAGE_END);
        refresh();
    }

    protected void refresh() {
        revalidate();

        if (mConfirm != null) {
            mConfirm.setVisible(mElements.size() > 0);
        }
    }

    protected boolean checkValidity() {
        boolean valid = true;

        for (Element element : mElements) {
            if (!element.checkValidity()) {
                valid = false;
            }
        }

        return valid;
    }

    public JButton getConfirmButton() {
        return mConfirm;
    }
    // classes

    public class CheckHolderListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            mCreateHolder = mHolderCheck.isSelected();
        }
    }

    public class CheckPrefixListener implements ChangeListener {

        @Override
        public void stateChanged(ChangeEvent event) {
            mPrefixValue.setEnabled(mPrefixCheck.isSelected());

            if (mPrefixCheck.isSelected() && mPrefixValue.getText().length() > 0) {
                mPrefix = mPrefixValue.getText();
            } else {
                mPrefix = null;
            }
        }
    }

    protected class ConfirmAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            boolean valid = checkValidity();

            for (Entry entry : mEntries) {
                entry.syncElement();
            }

            if (valid) {
                if (mConfirmListener != null) {
                    mConfirmListener.onConfirm(mProject, mEditor, mElements, mPrefix, mCreateHolder, msplitOnclickMethodsCheck.isSelected(),mNeedInitCheck.isSelected(),mRToR2Check.isSelected());
                }
            }
        }
    }

    protected class CancelAction extends AbstractAction {

        public void actionPerformed(ActionEvent event) {
            if (mCancelListener != null) {
                mCancelListener.onCancel();
            }
        }
    }
}
