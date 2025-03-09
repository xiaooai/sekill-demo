// package com.xxxx.seckill.config;

// import org.springframework.amqp.core.Binding;
// import org.springframework.amqp.core.BindingBuilder;
// import org.springframework.amqp.core.FanoutExchange;
// import org.springframework.amqp.core.Queue;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;



// // RabbitMQ配置类
// @Configuration
// public class RebbitMQFanoutConfig {


//     private static final String QUEUE01 = "queue_fanout_01";
//     private static final String QUEUE02 = "queue_fanout_02";
//     private static final String EXCHANGE = "fanoutExchange";
    

//     @Bean
//     public Queue queue() {
//         return new Queue("queue", true);
//     }   

//     @Bean
//     public Queue queue01_fanout() {
//         return new Queue(QUEUE01);
//     }

//     @Bean
//     public Queue queue02_fanout() {
//         return new Queue(QUEUE02);
//     }

//     @Bean
//     public FanoutExchange fanoutExchange() {
//         return new FanoutExchange(EXCHANGE);
//     }

//     @Bean
//     public Binding binding01_fanout() {
//         return BindingBuilder.bind(queue01_fanout()).to(fanoutExchange());
//     }

//     @Bean
//     public Binding binding02_fanout() {
//         return BindingBuilder.bind(queue02_fanout()).to(fanoutExchange());
//     }




    


// }