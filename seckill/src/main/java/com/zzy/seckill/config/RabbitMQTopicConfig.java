package com.zzy.seckill.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQTopicConfig {
    private  static  final  String QUEUE = "seckillQueue";
    private  static  final  String EXCHANGE = "seckillExchange";
    private  static  final  String ROUTINGKEY = "seckill.#";
    @Bean
    public Queue queue(){//队列名，是否持久化
        return  new Queue(QUEUE);
    }
    @Bean
    public TopicExchange topicExchange(){//交换机
        return  new TopicExchange(EXCHANGE);
    }
    @Bean
    public Binding binding(){//队列绑定交换机 使用路由键
        return BindingBuilder.bind(queue()).to(topicExchange()).with(ROUTINGKEY);
    }

}
