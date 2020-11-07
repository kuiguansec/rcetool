package com.test.tools.service.impl;

import com.test.tools.service.RceToolsModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public class RceToolsModelImpl implements RceToolsModel {
    public List<String> getAllFileByNote(DefaultMutableTreeNode note) {
        List<String> allFile = new ArrayList<>();
        File file = new File("plugin/" + note.toString() + "/");
        if (!file.exists()) {
            return null;
        }
        File[] files = file.listFiles();
        if (files == null) return null;
        for (File file1 : files) {
            if (!file1.isDirectory()) {
                String[] split = file1.getName().split("\\.");
                String s = split[split.length - 1];
                if (s.equals("jar"))
                    allFile.add(file1.getName());
            }
        }
        return allFile;
    }


    public List<String> getAllDirByPlugin() {
        List<String> allDir = new ArrayList<>();
        File file = new File("plugin/");
        if (!file.exists()) return null;
        File[] files = file.listFiles();
        if (files == null) return null;
        for (File file1 : files) {
            if (file1.isDirectory()) {
                allDir.add(file1.getName());
            }
        }
        return allDir;
    }
}
