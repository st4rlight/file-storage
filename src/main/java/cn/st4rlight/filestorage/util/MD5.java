package cn.st4rlight.filestorage.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

@Slf4j
public class MD5 {

    public static String getMD5(InputStream inputStream) throws RuntimeException, IOException {
        BigInteger bi = null;
        try {
            byte[] buffer = new byte[1024];
            MessageDigest md5 = MessageDigest.getInstance("MD5");

            int len = 0;
            while((len = inputStream.read(buffer)) != -1)
                md5.update(buffer, 0, len);

            byte[] bytes = md5.digest();
            bi = new BigInteger(1, bytes);

            // 使用BigInteger做转换得话会导致丢失前置得0，因此要补上
            String result = bi.toString(16);
            while(result.length() < 32)
                result = "0" + result;

            return result;
        } catch (NoSuchAlgorithmException ex) {
            log.error("获取MD5转换工具出错", ex);
            throw new RuntimeException(ex);
        } catch (IOException ex) {
            log.error("文件流读取出错", ex);
            throw new RuntimeException(ex);
        }finally {
            inputStream.close();
        }
    }

    public static String encryptPassword(String password){
        byte[] secrectBytes = null;
        try {
            secrectBytes = MessageDigest.getInstance("MD5").digest(password.getBytes());
            String str = new BigInteger(1, secrectBytes).toString(16);
            StringBuilder sb = new StringBuilder(str);
            for(int i = 0; i < 32 - str.length(); i++)
                sb.insert(0, "0");

            return sb.toString();
        } catch (NoSuchAlgorithmException ex) {
            log.error("密码加密出错", ex);
            throw new RuntimeException(ex);
        }
    }
}
