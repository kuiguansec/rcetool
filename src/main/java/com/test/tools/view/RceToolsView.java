package com.test.tools.view;

import com.test.tools.model.Cms;
import com.test.tools.service.CmsService;
import com.test.tools.service.RceToolsModel;
import com.test.tools.service.impl.CmsServiceImpl;
import com.test.tools.service.impl.RceToolsModelImpl;
import com.test.tools.tools.GetHtml;
import com.test.tools.tools.LoaderJar;
import org.apache.commons.codec.digest.DigestUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;

public class RceToolsView extends JFrame {
    private JFrame jf;
    private JSplitPane topPane;
    private JSplitPane bottomPane;
    private JLabel labelTarget;
    private RceToolsModel rceToolsModel;
    private JLabel labelCookies;
    private JLabel labelCommand;
    private JTextField textTarget;
    private JTextField textCookies;
    private JTextField textCommand;
    private DefaultMutableTreeNode root;
    private JTree vulNameTree;
    private JButton executeButton;
    private JButton fileUploadButton;
    private JButton scanCmsBtn;
    private JTextArea resultTextArea;
    private JMenuBar jMenuBar;
    private CmsService cmsService;
    private ButtonGroup systemGroup;
    private JRadioButton windowsRadio;
    private JRadioButton linuxRadio;
    private JLabel sysLabel;

    public void init() {
        jf = new JFrame("远程代码执行工具");
        jMenuBar = new JMenuBar();
        topPane = new JSplitPane(0);
        bottomPane = new JSplitPane(1);
        labelTarget = new JLabel("目标地址:", 2);
        labelCookies = new JLabel("Cookie:", 2);
        labelCommand = new JLabel("Command:", 2);
        textTarget = new JTextField(70);
        textCookies = new JTextField(70);
        textCommand = new JTextField(35);
        root = new DefaultMutableTreeNode("---请选择漏洞---");
        vulNameTree = new JTree(root);
        rceToolsModel = new RceToolsModelImpl();

        addMenu();
        scanCmsBtn = new JButton(new AbstractAction("指纹识别") {
            public void actionPerformed(ActionEvent e) {
                resultTextArea.setText("正在识别指纹\n");
                if (textTarget.getText().trim().equals("")) {
                    return;
                }
                (new Thread(() -> {
                    try {
                        List<String> results = new ArrayList<>();

                        String s = "";
                        String goPluginPath = System.getProperty("user.dir") + "\\tools\\main.exe ";
                        Process exec = Runtime.getRuntime().exec(goPluginPath + textTarget.getText().trim() + " 50");
                        InputStreamReader inputStreamReader = new InputStreamReader(exec.getInputStream());
                        BufferedReader br = new BufferedReader(inputStreamReader);
                        while ((s = br.readLine()) != null) {
                            results.add(s + "\n");
                        }
                        if (results.size() > 0) {
                            resultTextArea.setText("");
                            for (String data : results) {
                                resultTextArea.append(data);
                            }
                        } else {
                            resultTextArea.setText("无法识别指纹信息");
                        }
                        inputStreamReader.close();
                        br.close();
                    } catch (IOException ioException) {
                        ioException.printStackTrace();
                    }
                })).start();
            }
        });

        executeButton = new JButton(new AbstractAction("执行命令") {
            public void actionPerformed(ActionEvent e) {

                if (textTarget.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(jf, "url不能为空");
                    return;
                }
                if (vulNameTree.getSelectionPath() == null) {
                    JOptionPane.showMessageDialog(jf, "请选择exp");
                    return;
                }
                if (textCommand.getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(jf, "请输入执行命令");
                    return;
                }
                new Thread(()->{
                    try {
                        executeCommand(e);
                    } catch (MalformedURLException malformedURLException) {
                        malformedURLException.printStackTrace();
                    } catch (ClassNotFoundException classNotFoundException) {
                        classNotFoundException.printStackTrace();
                    } catch (IllegalAccessException illegalAccessException) {
                        illegalAccessException.printStackTrace();
                    } catch (InstantiationException instantiationException) {
                        instantiationException.printStackTrace();
                    } catch (NoSuchMethodException noSuchMethodException) {
                        noSuchMethodException.printStackTrace();
                    } catch (InvocationTargetException invocationTargetException) {
                        invocationTargetException.printStackTrace();
                    }
                }).start();

            }
        });
        fileUploadButton = new JButton(new AbstractAction("文件上传") {
            public void actionPerformed(ActionEvent e) {
                String s = uploadFile(e);

                if (s.equals("") || s == null) {
                    JOptionPane.showMessageDialog(jf, "文件上传失败！");

                    return;
                }
                TreePath selectionPath = vulNameTree.getSelectionPath();
                String selectedPath = getSelectedPath(selectionPath);
                try {
                    Class aClass = LoaderJar.loadJarMain(selectedPath);
                    Object o = aClass.newInstance();
                    Method uploadFile = aClass.getMethod("uploadFile", new Class[]{String.class, String.class});
                    String url = textTarget.getText();
                    boolean b = ((Boolean) uploadFile.invoke(o, url, s));
                    if (b) {
                        JOptionPane.showMessageDialog(jf, "上传成功");
                    } else {
                        JOptionPane.showMessageDialog(jf, "上传失败");
                    }
                } catch (MalformedURLException malformedURLException) {
                    malformedURLException.printStackTrace();
                } catch (ClassNotFoundException classNotFoundException) {
                    classNotFoundException.printStackTrace();
                } catch (IllegalAccessException illegalAccessException) {
                    illegalAccessException.printStackTrace();
                } catch (InstantiationException instantiationException) {
                    instantiationException.printStackTrace();
                } catch (NoSuchMethodException noSuchMethodException) {
                    noSuchMethodException.printStackTrace();
                } catch (InvocationTargetException invocationTargetException) {
                    invocationTargetException.printStackTrace();
                }
            }
        });

        resultTextArea = new JTextArea(30, 60);


        addBody();

        vulNameTree.addTreeSelectionListener(new TreeSelectionListener() {
            public void valueChanged(TreeSelectionEvent e) {
                selectedTreeNode(e);
            }
        });

        jf.pack();
        jf.setLocationRelativeTo(null);
        jf.setDefaultCloseOperation(3);
        jf.setVisible(true);
    }


    private void addMenu() {
        JMenu fingerprintMenu = new JMenu("指纹");

        JMenuItem fmpAdd = new JMenuItem(new AbstractAction("指纹管理") {
            public void actionPerformed(ActionEvent e) {
                FingerView fingerView = new FingerView();
                fingerView.init();
            }
        });

        JMenuItem fmpDel = new JMenuItem(new AbstractAction("添加指纹") {
            public void actionPerformed(ActionEvent e) {
                addFinger();
            }
        });

        JMenuItem fmpAbout = new JMenuItem(new AbstractAction("计算md5") {
            public void actionPerformed(ActionEvent e) {
                calcMd5();
            }
        });

        fingerprintMenu.add(fmpAdd);
        fingerprintMenu.add(fmpDel);
        fingerprintMenu.addSeparator();
        fingerprintMenu.add(fmpAbout);
        jMenuBar.add(fingerprintMenu);
        jf.setJMenuBar(jMenuBar);
    }


    private boolean checkInput() {
        TreePath selectionPath = vulNameTree.getSelectionPath();
        String selectedPath = getSelectedPath(selectionPath);
        if (selectionPath == null || selectedPath.equals("")) {

            JOptionPane.showMessageDialog(jf, "请选择exp！");
            return false;
        }
        if (textTarget.getText() == null || textTarget.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(jf, "url不能为空！");
            return false;
        }

        return true;
    }


    private void executeCommand(ActionEvent e) throws MalformedURLException, ClassNotFoundException, IllegalAccessException, InstantiationException, NoSuchMethodException, InvocationTargetException {
        TreePath selectionPath = vulNameTree.getSelectionPath();
        String selectedPath = getSelectedPath(selectionPath);
        if (!checkInput())
            return;
        Class aClass = LoaderJar.loadJarMain(selectedPath);
        System.out.println(windowsRadio.isSelected());
        Object o = aClass.newInstance();
        Method attack = aClass.getMethod("attack", new Class[]{String.class, String.class, String.class, boolean.class});
        String attackRes = (String) attack.invoke(o, new Object[]{textTarget.getText(), textCommand.getText(), textCommand.getText(), windowsRadio.isSelected()});
        resultTextArea.setText(attackRes);
    }


    private String getSelectedPath(TreePath selectionPath) {
        String[] split = selectionPath.toString().split(",");
        String vulPath = "plugin/" + split[1].trim() + "/" + split[2].split("]")[0].trim();
        File file = new File(vulPath);
        if (!file.exists()) return "";
        return vulPath;
    }


    private void selectedTreeNode(TreeSelectionEvent e) {
        resultTextArea.setText("");
        try {
            String[] split = e.getPath().toString().split(",");
            String vulPath = "plugin/" + split[1].trim() + "/" + split[2].split("]")[0].trim();
            File file = new File(vulPath);
            if (!file.exists())
                return;
            InputStreamReader inputStreamReader = new InputStreamReader(new FileInputStream(file));
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            Class aClass = LoaderJar.loadJarMain(vulPath);
            Object o = aClass.newInstance();
            Method readme = aClass.getMethod("readme", new Class[0]);
            String result = (String) readme.invoke(o, new Object[0]);
            resultTextArea.setText(result);
        } catch (ArrayIndexOutOfBoundsException | FileNotFoundException arrayIndexOutOfBoundsException) {
            if (e.getPath().toString().equals("[---请选择漏洞---]")) {
                resultTextArea.setText(e.getPath().toString());
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } catch (ClassNotFoundException classNotFoundException) {

        } catch (NoSuchMethodException noSuchMethodException) {

        } catch (IllegalAccessException illegalAccessException) {

        } catch (InstantiationException instantiationException) {
            instantiationException.printStackTrace();
        } catch (InvocationTargetException invocationTargetException) {
            invocationTargetException.printStackTrace();
        }
    }


    private Box addTopText() {
        Box verticalBox = Box.createVerticalBox();

        Box topBox_1 = Box.createHorizontalBox();
        Box topBox_2 = Box.createHorizontalBox();
        Box topBox_3 = Box.createHorizontalBox();

        topBox_1.setBorder(new EmptyBorder(10, 15, 10, 10));
        topBox_2.setBorder(new EmptyBorder(0, 15, 10, 10));
        topBox_3.setBorder(new EmptyBorder(0, 15, 10, 10));

        textTarget.setFont(new Font("新宋体", 0, 18));
        textCookies.setFont(new Font("新宋体", 0, 18));
        textCommand.setFont(new Font("新宋体", 0, 18));

        textCommand.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                executeButton.doClick();
            }
        });


        topBox_1.add(labelTarget);
        topBox_1.add(Box.createHorizontalStrut(20));
        topBox_1.add(textTarget);
        topBox_1.add(Box.createHorizontalStrut(20));
        topBox_1.add(executeButton);
        verticalBox.add(topBox_1);

        topBox_2.add(labelCookies);
        topBox_2.add(Box.createHorizontalStrut(30));
        topBox_2.add(textCookies);
        topBox_2.add(Box.createHorizontalStrut(20));
        topBox_2.add(scanCmsBtn);
        verticalBox.add(topBox_2);

        topBox_3.add(labelCommand);
        topBox_3.add(Box.createHorizontalStrut(14));
        topBox_3.add(textCommand);
        sysLabel = new JLabel("目标系统:");
        topBox_3.add(sysLabel);
        systemGroup = new ButtonGroup();
        windowsRadio = new JRadioButton("windows");
        windowsRadio.setSelected(true);
        linuxRadio = new JRadioButton("linux");
        systemGroup.add(windowsRadio);
        systemGroup.add(linuxRadio);
        topBox_3.add(windowsRadio);
        topBox_3.add(linuxRadio);
        topBox_3.add(Box.createHorizontalStrut(20));
        topBox_3.add(fileUploadButton);
        verticalBox.add(topBox_3);
        return verticalBox;
    }


    private void addBody() {
        Box box = addTopText();

        Border border = BorderFactory.createLineBorder(Color.BLACK);


        readerNode();

        resultTextArea.setBorder(border);
        resultTextArea.setEditable(false);

        JScrollPane jScrollPane = new JScrollPane(resultTextArea);
        jScrollPane.createVerticalScrollBar();

        bottomPane.add(jScrollPane);
        topPane.add(box);
        topPane.add(bottomPane);
        jf.add(topPane);
    }


    private void readerNode() {
        root.removeAllChildren();
        resultTextArea.removeAll();
        Border border = BorderFactory.createLineBorder(Color.BLACK);

        vulNameTree.setBorder(border);


        List<String> allDir = rceToolsModel.getAllDirByPlugin();

        for (String s : allDir) {
            root.add(new DefaultMutableTreeNode(s));
        }


        if (root.getChildCount() > 0) {
            DefaultMutableTreeNode nextNode = root.getNextNode();
            List<String> allFile_1 = rceToolsModel.getAllFileByNote(nextNode);
            for (String s : allFile_1) {
                nextNode.add(new DefaultMutableTreeNode(s));
            }
            while (true) {
                nextNode = nextNode.getNextNode();
                if (nextNode == null)
                    break;
                List<String> allFile_2 = rceToolsModel.getAllFileByNote(nextNode);
                if (allFile_2 == null) {
                    continue;
                }
                for (String s : allFile_2) {
                    nextNode.add(new DefaultMutableTreeNode(s));
                }
            }
        }

        JScrollPane jScrollPane = new JScrollPane(vulNameTree);
        jScrollPane.createVerticalScrollBar();
        bottomPane.add(jScrollPane);
    }

    private String uploadFile(ActionEvent e) {
        JFileChooser chooser = new JFileChooser();
        chooser.setMultiSelectionEnabled(true);
        StringBuffer sb = new StringBuffer();

        if (!checkInput()) {
            JOptionPane.showMessageDialog(jf, "文件上传失败!");
            return null;
        }


        if (chooser.showOpenDialog(jf) == 0) {

            File selectedFile = chooser.getSelectedFile();
            if (!selectedFile.exists()) return null;

            InputStreamReader reader = null;
            try {
                reader = new InputStreamReader(new FileInputStream(selectedFile), "UTF-8");
                BufferedReader bfreader = new BufferedReader(reader);
                String line;
                while ((line = bfreader.readLine()) != null) {
                    sb.append(line);
                }
            } catch (UnsupportedEncodingException unsupportedEncodingException) {
                unsupportedEncodingException.printStackTrace();
            } catch (FileNotFoundException fileNotFoundException) {
                fileNotFoundException.printStackTrace();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }

        return sb.toString();
    }


    public void calcMd5() {
        final JDialog jd = new JDialog();
        jd.setTitle("计算md5");

        Box box = Box.createVerticalBox();
        JPanel jp_1 = new JPanel();
        final JTextField urlText = new JTextField(20);
        urlText.setPreferredSize(new Dimension(100, 30));


        JLabel cmsNameLabel = new JLabel("url:");
        jp_1.setBorder(new EmptyBorder(0, 10, 0, 10));

        jp_1.add(cmsNameLabel);
        jp_1.add(urlText);
        box.add(Box.createHorizontalStrut(85));
        box.add(Box.createVerticalStrut(20));
        box.add(jp_1);
        box.add(Box.createVerticalStrut(10));

        final JButton btn = new JButton(new AbstractAction("计算") {
            public void actionPerformed(ActionEvent e) {
                String url = urlText.getText().trim();
                if (url.equals("")) {
                    JOptionPane.showMessageDialog(jd, "url不能为空");
                    return;
                }
                if (!url.contains("http")) {
                    JOptionPane.showMessageDialog(jd, "url格式不正确");
                    return;
                }
                byte[] byteByUrl = GetHtml.getByteByUrl(url);
                String s = DigestUtils.md5Hex(byteByUrl);
                urlText.setText(s);
            }
        });

        box.add(btn);
        box.add(Box.createVerticalStrut(10));
        urlText.setAction(new AbstractAction() {
            public void actionPerformed(ActionEvent e) {
                btn.doClick();
            }
        });

        jd.add(box);
        jd.setAlwaysOnTop(true);

        jd.setSize(new Dimension(500, 300));
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setDefaultCloseOperation(2);
        jd.setVisible(true);
    }


    public void addFinger() {
        final JDialog jd = new JDialog();
        jd.setTitle("添加指纹");

        Box box = Box.createVerticalBox();
        JPanel jp_1 = new JPanel();

        jp_1.setPreferredSize(new Dimension(50, 30));
        JPanel jp_2 = new JPanel();
        JPanel jp_3 = new JPanel();
        JPanel jp_4 = new JPanel();
        JPanel jp_5 = new JPanel();
        final JTextField cmsName = new JTextField(20);
        cmsName.setPreferredSize(new Dimension(200, 30));
        final JTextField path = new JTextField(20);
        path.setPreferredSize(new Dimension(200, 30));
        final JTextField matchPattern = new JTextField(20);
        matchPattern.setPreferredSize(new Dimension(200, 30));
        final JTextField options = new JTextField(20);
        options.setPreferredSize(new Dimension(200, 30));
        final JTextField status = new JTextField(20);
        status.setPreferredSize(new Dimension(200, 30));
        JLabel cmsNameLabel = new JLabel("名称:");
        JLabel pathLabel = new JLabel("路径:");
        JLabel mpLabel = new JLabel("指纹:");
        JLabel optionsLabel = new JLabel("方式:");
        JLabel statusLabel = new JLabel("状态:");

        jp_1.setBorder(new EmptyBorder(0, 10, 0, 10));
        jp_2.setBorder(new EmptyBorder(0, 10, 0, 10));
        jp_3.setBorder(new EmptyBorder(0, 10, 0, 10));
        jp_4.setBorder(new EmptyBorder(0, 10, 0, 10));
        jp_5.setBorder(new EmptyBorder(0, 10, 0, 10));

        jp_1.add(cmsNameLabel);
        jp_1.add(cmsName);

        jp_2.add(pathLabel);
        jp_2.add(path);

        jp_3.add(mpLabel);
        jp_3.add(matchPattern);

        jp_4.add(optionsLabel);
        jp_4.add(options);

        jp_5.add(statusLabel);
        jp_5.add(status);

        box.add(Box.createVerticalStrut(10));
        box.add(Box.createHorizontalStrut(85));
        box.add(jp_1);
        box.add(Box.createVerticalStrut(10));
        box.add(jp_2);
        box.add(Box.createVerticalStrut(10));
        box.add(jp_3);
        box.add(Box.createVerticalStrut(10));
        box.add(jp_4);
        box.add(Box.createVerticalStrut(10));
        box.add(jp_5);
        box.add(Box.createVerticalStrut(10));


        JButton btn = new JButton(new AbstractAction("提交") {
            public void actionPerformed(ActionEvent e) {
                if (cmsName.getText().trim().equals("") || path
                        .getText().trim().equals("") || matchPattern
                        .getText().trim().equals("") || options
                        .getText().trim().equals("") || status
                        .getText().trim().equals("")) {
                    JOptionPane.showMessageDialog(jd, "必填字段不能为空");
                    return;
                }
                Cms cms = new Cms();
                cms.setCmsName(cmsName.getText().trim());
                cms.setPath(path.getText().trim());
                cms.setMatchPattern(matchPattern.getText().trim());
                cms.setOptions(options.getText().trim());
                cms.setStatus(Integer.valueOf(status.getText().trim()).intValue());
                cmsService = (CmsService) new CmsServiceImpl();
                boolean b = cmsService.addCms(cms);
                if (b) {
                    JOptionPane.showMessageDialog(jd, "添加成功");
                } else {
                    JOptionPane.showMessageDialog(jd, "添加失败");
                }
            }
        });

        box.add(btn);
        box.add(Box.createVerticalStrut(10));

        jd.add(box);
        jd.setAlwaysOnTop(true);
        jd.pack();
        jd.setLocationRelativeTo(null);
        jd.setDefaultCloseOperation(2);
        jd.setVisible(true);
    }
}
