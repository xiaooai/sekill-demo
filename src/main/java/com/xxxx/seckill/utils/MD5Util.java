package com.xxxx.seckill.utils; 

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

@Component
public class MD5Util {

    // 加密
    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }

    private static final String salt="1a2b3c4d";

    public static String inputPassToFromPass(String inputPass){
        String str ="" + salt.charAt(0) + salt.charAt(2) + inputPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);
    }

    public static String fromPassToDBPass(String formPass, String salt){
        String str ="" + salt.charAt(0) + salt.charAt(2) + formPass + salt.charAt(5) + salt.charAt(4);
        return md5(str);

    }

    public static String inputPassToDBPass(String inputPass, String salt){
        String fromPass = inputPassToFromPass(inputPass);
        String dbPass = fromPassToDBPass(fromPass, salt);
        return dbPass;
    }

    
    // public static void main(String[] args){
    //     // ecb18c9b21921ffef2b2ee83884cfd13
    //     System.out.println(inputPassToFromPass("202122"));
    //     System.out.println(fromPassToDBPass("ecb18c9b21921ffef2b2ee83884cfd13", "1a2b3c4d"));
    //     System.out.println(inputPassToDBPass("202122", "1a2b3c4d"));
    // }

}
