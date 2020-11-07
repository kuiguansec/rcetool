package com.test.tools.service;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;

public interface RceToolsModel {
  List<String> getAllFileByNote(DefaultMutableTreeNode paramDefaultMutableTreeNode);
  
  List<String> getAllDirByPlugin();
}

