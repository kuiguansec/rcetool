package com.test.tools.service.impl;

import com.test.tools.model.Cms;
import com.test.tools.service.CmsService;
import com.test.tools.tools.DatabaseContent;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class CmsServiceImpl implements CmsService {
    public List<Cms> getAllCms() {
        Statement statement = null;
        List<Cms> cmsList = new ArrayList<>();
        Connection connect = DatabaseContent.getConnect();
        if (connect == null) return null;
        try {
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM cms");
            while (resultSet.next()) {
                Cms cms = new Cms();
                cms.setFingerId(resultSet.getInt("finger_id"));
                cms.setCmsName(resultSet.getString("cms_name"));
                cms.setPath(resultSet.getString("path"));
                cms.setMatchPattern(resultSet.getString("match_pattern"));
                cms.setOptions(resultSet.getString("options"));
                cms.setHit(resultSet.getInt("hit"));
                cms.setStatus(resultSet.getInt("status"));
                cmsList.add(cms);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cmsList;
    }


    public List<Cms> getCmsByKeyword(String options, String keyword) {
        StringBuffer sqlBuff = new StringBuffer("SELECT * from cms WHERE ");
        if (options.equals("名称")) {

            sqlBuff.append("cms_name LIKE '%");
            sqlBuff.append(keyword.trim());
            sqlBuff.append("%'");
        } else if (options.equals("路径")) {
            sqlBuff.append("path LIKE '%");
            sqlBuff.append(keyword.trim());
            sqlBuff.append("%'");
        } else if (options.equals("指纹")) {
            sqlBuff.append("match_pattern LIKE '%");
            sqlBuff.append(keyword.trim());
            sqlBuff.append("%'");
        }

        String sql = sqlBuff.toString();

        Statement statement = null;
        List<Cms> cmsList = new ArrayList<>();
        Connection connect = DatabaseContent.getConnect();
        if (connect == null) return null;
        try {
            statement = connect.createStatement();
            ResultSet resultSet = statement.executeQuery(sql.toString());
            while (resultSet.next()) {
                Cms cms = new Cms();
                cms.setFingerId(resultSet.getInt("finger_id"));
                cms.setCmsName(resultSet.getString("cms_name"));
                cms.setPath(resultSet.getString("path"));
                cms.setMatchPattern(resultSet.getString("match_pattern"));
                cms.setOptions(resultSet.getString("options"));
                cms.setHit(resultSet.getInt("hit"));
                cms.setStatus(resultSet.getInt("status"));
                cmsList.add(cms);
            }

        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return cmsList;
    }


    public boolean delCmsById(String id) {
        String sql = "DELETE FROM cms WHERE finger_id = " + id;
        Statement statement = null;
        Connection connect = DatabaseContent.getConnect();
        if (connect == null) return false;
        try {
            statement = connect.createStatement();
            int i = statement.executeUpdate(sql);
            return (i == 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }


    public boolean updateCmsByCms(Cms cms) throws SQLException {
        String sql = "UPDATE cms SET cms_name=?, path=?, match_pattern=?, options=?, status=? WHERE finger_id = ?";
        Connection connect = DatabaseContent.getConnect();
        PreparedStatement ps = connect.prepareStatement(sql);
        ps.setString(1, cms.getCmsName());
        ps.setString(2, cms.getPath());
        ps.setString(3, cms.getMatchPattern());
        ps.setString(4, cms.getOptions());
        ps.setInt(5, cms.getStatus());
        ps.setInt(6, cms.getFingerId());
        return (ps.executeUpdate() == 1);
    }


    public boolean addCms(Cms cms) {
        String sql = "INSERT INTO  cms(cms_name, path, match_pattern, options, status, hit) VALUES(?, ?, ?, ?, ?, ?) ";
        Connection connect = DatabaseContent.getConnect();
        PreparedStatement ps = null;
        try {
            ps = connect.prepareStatement(sql);
            ps.setString(1, cms.getCmsName());
            ps.setString(2, cms.getPath());
            ps.setString(3, cms.getMatchPattern());
            ps.setString(4, cms.getOptions());
            ps.setInt(5, cms.getStatus());
            ps.setInt(6, cms.getHit());
            return (ps.executeUpdate() == 1);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            return false;
        }
    }
}

