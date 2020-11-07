package com.test.tools.service;

import com.test.tools.model.Cms;

import java.sql.SQLException;
import java.util.List;

public interface CmsService {
  List<Cms> getAllCms();
  
  List<Cms> getCmsByKeyword(String paramString1, String paramString2);
  
  boolean delCmsById(String paramString);
  
  boolean updateCmsByCms(Cms paramCms) throws SQLException;
  
  boolean addCms(Cms paramCms);
}

