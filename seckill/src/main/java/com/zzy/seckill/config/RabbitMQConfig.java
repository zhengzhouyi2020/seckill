package com.zzy.seckill.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;


@Configuration
public class RabbitMQConfig {
    private  static  final  String QUEUE01 = "queue_fanout01";
    private  static  final  String QUEUE02 = "queue_fanout02";
    private  static  final  String EXCHANGE_FANOUT = "fanoutExchange";

    @Bean
    public Queue queue00(){//队列名，是否持久化
        return  new Queue("queue",true);
    }

    @Bean
    public Queue queue01(){//队列名
        return  new Queue(QUEUE01);
    }

    @Bean
    public Queue queue02(){//队列名
        return  new Queue(QUEUE02);
    }
    @Bean
    public FanoutExchange fanoutExchange(){//交换机
        return  new FanoutExchange(EXCHANGE_FANOUT);
    }
    @Bean
    public Binding binding01(){//队列绑定交换机
        return BindingBuilder.bind(queue01()).to(fanoutExchange());
    }
    @Bean
    public Binding binding02(){//队列绑定交换机
        return BindingBuilder.bind(queue02()).to(fanoutExchange());
    }

    private  static  final  String QUEUE03 = "queue_direct01";
    private  static  final  String QUEUE04 = "queue_direct02";
    private  static  final  String EXCHANGE_DIRECT = "directExchange";
    private  static  final  String ROUTINGKEY01 = "queue.red";
    private  static  final  String ROUTINGKEY02 = "queue.green";
    @Bean
    public Queue queue03(){//队列名，是否持久化
        return  new Queue(QUEUE03);
    }
    @Bean
    public Queue queue04(){//队列名，是否持久化
        return  new Queue(QUEUE04);
    }
    @Bean
    public DirectExchange directExchange(){//交换机
        return  new DirectExchange(EXCHANGE_DIRECT);
    }
    @Bean
    public Binding binding03(){//队列绑定交换机 使用路由键
        return BindingBuilder.bind(queue03()).to(directExchange()).with(ROUTINGKEY01);
    }
    @Bean
    public Binding binding04(){//队列绑定交换机 使用路由键
        return BindingBuilder.bind(queue04()).to(directExchange()).with(ROUTINGKEY02);
    }

    private  static  final  String QUEUE05 = "queue_topic01";
    private  static  final  String QUEUE06 = "queue_topic02";
    private  static  final  String EXCHANGE_TOPIC = "topicExchange";
    private  static  final  String ROUTINGKEY03 = "#.queue.#";
    private  static  final  String ROUTINGKEY04 = "*.queue.#";

    @Bean
    public Queue queue05(){//队列名，是否持久化
        return  new Queue(QUEUE05);
    }
    @Bean
    public Queue queue06(){//队列名，是否持久化
        return  new Queue(QUEUE06);
    }
    @Bean
    public TopicExchange topicExchange00(){//交换机
        return  new TopicExchange(EXCHANGE_TOPIC);
    }
    @Bean
    public Binding binding05(){//队列绑定交换机 使用路由键
        return BindingBuilder.bind(queue05()).to(topicExchange00()).with(ROUTINGKEY03);
    }
    @Bean
    public Binding binding06(){//队列绑定交换机 使用路由键
        return BindingBuilder.bind(queue06()).to(topicExchange00()).with(ROUTINGKEY04);
    }


    private  static  final  String QUEUE07 = "queue_header01";
    private  static  final  String QUEUE08 = "queue_header02";
    private  static  final  String EXCHANGE_HEADER = "headersExchange";
    @Bean
    public Queue queue07(){//队列名，是否持久化
        return  new Queue(QUEUE07);
    }
    @Bean
    public Queue queue08(){//队列名，是否持久化
        return  new Queue(QUEUE08);
    }
    @Bean
    public HeadersExchange headersExchange(){//交换机
        return  new HeadersExchange(EXCHANGE_HEADER);
    }
    @Bean
    public Binding binding07(){//队列绑定交换机 使用路由键
        Map<String,Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","low");
        //任意一个
        return BindingBuilder.bind(queue07()).to(headersExchange()).whereAny(map).match();
    }
    @Bean
    public Binding binding08(){//队列绑定交换机 使用路由键
        Map<String,Object> map = new HashMap<>();
        map.put("color","red");
        map.put("speed","fast");
        //同时匹配color和speed
        return BindingBuilder.bind(queue08()).to(headersExchange()).whereAll(map).match();

    }
}
