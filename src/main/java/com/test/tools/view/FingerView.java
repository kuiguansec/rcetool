package com.test.tools.view;

import com.test.tools.model.Cms;
import com.test.tools.service.CmsService;
import com.test.tools.service.impl.CmsServiceImpl;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.sql.SQLException;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

public class FingerView extends JFrame {

    private JFrame jf;
    private JPanel jPanel;
    private JTable jTable;
    private JButton jButton;
    private JTextField searchText;
    private JComboBox<String> jcb;
    private CmsService cmsService;

    public void init() {
        jf = new JFrame("指纹管理");

        jPanel = new JPanel();
        searchText = new JTextField();
        jButton = new JButton(new AbstractAction("搜索") {
            public void actionPerformed(ActionEvent e) {
                if (searchText.getText().trim().equals("") || searchText.getText().trim().equals("请输入搜索内容")) {
                    TableModel tableModel = getAllData();
                    renderTable(tableModel);
                } else {
                    searchData();
                }
            }
        });
        jcb = new JComboBox<>();
        jcb.addItem("名称");
        jcb.addItem("路径");
        jcb.addItem("指纹");
        jcb.setPreferredSize(new Dimension(100, 30));
        jPanel.add(jcb);
        searchText.setPreferredSize(new Dimension(400, 30));
        searchText.setText("请输入搜索内容");
        searchText.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                jButton.doClick();
            }
        });
        searchText.addFocusListener(new FocusListener() {
            public void focusGained(FocusEvent e) {
                if (searchText.getText().equals("请输入搜索内容")) {
                    searchText.setText("");
                }
            }


            public void focusLost(FocusEvent e) {
                if (searchText.getText().trim().equals("")) {
                    searchText.setText("请输入搜索内容");
                }
            }
        });
        jPanel.add(searchText);
        jButton.setPreferredSize(new Dimension(100, 30));
        jPanel.add(jButton);
        jf.add(jPanel, "South");

        JButton delBtn = new JButton(new AbstractAction("删除选中行") {
            public void actionPerformed(ActionEvent e) {
                boolean b = false;
                int[] selectedRows = jTable.getSelectedRows();
                for (int selectedRow : selectedRows) {
                    TableModel model = jTable.getModel();
                    String cmsId = String.valueOf(model.getValueAt(selectedRow, 0));
                    b = cmsService.delCmsById(cmsId);
                }
                if (b) {
                    JOptionPane.showMessageDialog(jf, "删除成功！");
                } else {
                    JOptionPane.showMessageDialog(jf, "删除失败！");
                }
                TableModel tableModel = getAllData();
                renderTable(tableModel);
            }
        });
        delBtn.setPreferredSize(new Dimension(100, 30));
        jPanel.add(delBtn);
        addTable();
        JScrollPane scrollPane = new JScrollPane(jTable);
        jf.add(scrollPane);
        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(2);
        jf.setVisible(true);
    }

    public void searchData() {
        String[] columnNames = {"id", "cms名", "路径", "指纹", "方式", "状态码"};
        String options = (String) jcb.getSelectedItem();
        String keyword = searchText.getText().trim();
        List<Cms> allCms = cmsService.getCmsByKeyword(options, keyword);
        Object[][] rowData = new Object[allCms.size()][6];
        for (int i = 0; i < allCms.size(); i++) {
            Cms cms = allCms.get(i);
            rowData[i][0] = Integer.valueOf(cms.getFingerId());
            rowData[i][1] = cms.getCmsName();
            rowData[i][2] = cms.getPath();
            rowData[i][3] = cms.getMatchPattern();
            rowData[i][4] = cms.getOptions();
            rowData[i][5] = Integer.valueOf(cms.getStatus());
        }
        TableModel tableModel = new DefaultTableModel(rowData, (Object[]) columnNames);
        renderTable(tableModel);
    }


    public TableModel getAllData() {
        String[] columnNames = {"id", "cms名", "路径", "指纹", "方式", "状态码"};
        cmsService = (CmsService) new CmsServiceImpl();
        List<Cms> allCms = cmsService.getAllCms();
        Object[][] rowData = new Object[allCms.size()][6];
        for (int i = 0; i < allCms.size(); i++) {
            Cms cms = allCms.get(i);
            rowData[i][0] = Integer.valueOf(cms.getFingerId());
            rowData[i][1] = cms.getCmsName();
            rowData[i][2] = cms.getPath();
            rowData[i][3] = cms.getMatchPattern();
            rowData[i][4] = cms.getOptions();
            rowData[i][5] = Integer.valueOf(cms.getStatus());
        }
        return new DefaultTableModel(rowData, (Object[]) columnNames);
    }

    public void addTable() {
        jTable = new JTable();
        TableModel tableModel = getAllData();
        renderTable(tableModel);
    }


    public void renderTable(TableModel tableModel) {
        jTable.setModel(tableModel);
        jTable.setShowGrid(true);
        jTable.setPreferredScrollableViewportSize(new Dimension(700, 500));
        jTable.setRowHeight(30);
        jTable.getTableHeader().setFont(new Font(null, 1, 14));
        jTable.getTableHeader().setForeground(Color.RED);
        jTable.getTableHeader().setResizingAllowed(true);
        jTable.getTableHeader().setReorderingAllowed(true);

        tableModel.addTableModelListener(new TableModelListener() {
            public void tableChanged(TableModelEvent e) {
                int i = JOptionPane.showConfirmDialog(jf, "是否确定修改数据?");
                if (i != 0) {
                    return;
                }
                int firstRow = e.getFirstRow();
                Cms cms = new Cms();
                cms.setFingerId(Integer.valueOf(String.valueOf(tableModel.getValueAt(firstRow, 0)).trim()).intValue());
                cms.setStatus(Integer.valueOf(String.valueOf(tableModel.getValueAt(firstRow, 5)).trim()).intValue());
                cms.setPath(String.valueOf(tableModel.getValueAt(firstRow, 2)).trim());
                cms.setCmsName(String.valueOf(tableModel.getValueAt(firstRow, 1)).trim());
                cms.setMatchPattern(String.valueOf(tableModel.getValueAt(firstRow, 3)).trim());
                cms.setOptions(String.valueOf(tableModel.getValueAt(firstRow, 4)).trim());
                try {
                    if (cmsService.updateCmsByCms(cms)) {
                        JOptionPane.showMessageDialog(jf, "修改成功");
                    } else {
                        JOptionPane.showMessageDialog(jf, "修改失败");
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        });
    }
}

