package com.xxxx.seckill.utils;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.xxxx.seckill.pojo.User;
import com.xxxx.seckill.vo.RespBean;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class userUtil {
    public static void main(String[] args) throws Exception {
        createUser(5000);
    }

    private static void createUser(int count) throws Exception {
        List<User> users = new ArrayList<>(count);
        // 生成用户
        for(int i = 0; i < count; ++i) {
            User user = new User();
            user.setId(13000000000L + i);
            user.setNickname("user" + i);
            user.setPassword("5fddd87880135d83261af2abfbbb825a");
            user.setSalt("1a2b3c4d");
            user.setLoginCount(0);
            user.setRegisterDate(new Date());
            users.add(user);
        }
        System.out.println("创建user");
        Connection connection = getConnect();
       System.out.println("创建数据库连接");
       String sql = "insert into t_user(login_count,nickname,register_date,salt,password,id) values(?,?,?,?,?,?)";
       PreparedStatement preparedStatement = connection.prepareStatement(sql);
       for(int i = 0; i < count; ++i) {
           User user = users.get(i);
           preparedStatement.setInt(1, user.getLoginCount());
           preparedStatement.setString(2, user.getNickname());
           preparedStatement.setTimestamp(3, new Timestamp(user.getRegisterDate().getTime()));
           preparedStatement.setString(4, user.getSalt());
           preparedStatement.setString(5, user.getPassword());
           preparedStatement.setLong(6,user.getId());
           preparedStatement.addBatch();
       }
       preparedStatement.executeBatch();
       preparedStatement.close();
       System.out.println("插入成功");
       connection.close();
       System.out.println("放入数据库");
        // 登录，生成token
        String urlString = "http://localhost:8080/login/doLogin";
        File file = new File("C:\\Users\\Administrator\\Desktop\\项目1\\config.txt");
        
        if (file.exists()) {
            file.delete();
        }
        RandomAccessFile raf = new RandomAccessFile(file, "rw");
        file.createNewFile();
        raf.seek(0);
        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            URL url = new URL(urlString);
            HttpURLConnection co = (HttpURLConnection) url.openConnection();
            co.setRequestMethod("POST");
            co.setDoOutput(true);
            OutputStream out = co.getOutputStream();
            String params = "mobile=" + user.getId() + "&password=" + MD5Util.inputPassToFromPass("202122");
            out.write(params.getBytes());
            out.flush();
            InputStream inputStream = co.getInputStream();
            ByteArrayOutputStream bout = new ByteArrayOutputStream();
            byte buff[] = new byte[1024];
            int len = 0;
            while ((len = inputStream.read(buff)) >= 0) {
                bout.write(buff, 0, len);
            }
            inputStream.close();
            bout.close();
            String response = new String(bout.toByteArray());
            ObjectMapper mapper = new ObjectMapper();
            RespBean respBean = mapper.readValue(response, RespBean.class);
            String userTicket = ((String) respBean.getObj());
            System.out.println("create userTicket : " + user.getId());

            String row = user.getId() + "," + userTicket;
            raf.seek(raf.length());
            raf.write(row.getBytes());
            raf.write("\r\n".getBytes());
            System.out.println("write to file : " + user.getId());
        }
        raf.close();

        System.out.println("over");

    }
    public static Connection getConnect() throws Exception {
        String url = "jdbc:mysql://localhost:3336/seckill?useUnicode=true&characterEncoding=UTF-8&serverTimezone=Asia/Shanghai";
        String username = "root";
        String password = "1234";
        String driver = "com.mysql.cj.jdbc.Driver";
        Class.forName(driver);
        return DriverManager.getConnection(url, username, password);
    }


}
