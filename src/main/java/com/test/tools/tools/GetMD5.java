package com.test.tools.tools;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;

import org.apache.commons.codec.binary.Hex;
import org.apache.commons.codec.digest.DigestUtils;


public class GetMD5 {
    public static String getMD5ByFile(FileInputStream fileInputStream) {
        try {
            MessageDigest MD5 = MessageDigest.getInstance("MD5");
            byte[] buffer = new byte[8192];
            int length;
            while ((length = fileInputStream.read(buffer)) != -1) {
                MD5.update(buffer, 0, length);
            }
            return new String(Hex.encodeHex(MD5.digest()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (fileInputStream != null) {
                    fileInputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static String getMd5ByString(String target) {
        return DigestUtils.md5Hex(target);
    }
}


/* Location:              D:\code\java\test\RceTools\RceTools-1.0-SNAPSHOT.jar!\com\sn\tools\tools\GetMD5.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.1.3
 */