package com.xxxx.seckill; // 规定所有的代码都放在这个包下

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// 标记这是一个完整独立的SpringBoot应用，核心标志不能删除
// 会自动开启组件扫描，寻找@Controller、@Service等组件
@SpringBootApplication 
@MapperScan("com.xxxx.seckill.mapper")
public class SeckillDemoApplication { // 命名规则，项目名+Application

    public static void main(String[] args) { // 会调用SpringApplication.run()方法，启动应用
        SpringApplication.run(SeckillDemoApplication.class, args);
    }

}
