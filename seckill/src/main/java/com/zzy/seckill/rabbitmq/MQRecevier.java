package com.zzy.seckill.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MQRecevier {

    @RabbitListener(queues = "queue")
    public void recevie(Object msg){
        log.info("接受" + msg);
    }
    @RabbitListener(queues = "queue_fanout01")
    public void recevie01(Object msg){
        log.info("QUEUE01接受" + msg);
    }
    @RabbitListener(queues = "queue_fanout02")
    public void recevie02(Object msg){
        log.info("QUEUE02接受" + msg);
    }
    @RabbitListener(queues = "queue_direct01")
    public void recevie03(Object msg){
        log.info("QUEUE03接受"+msg);
    }
    @RabbitListener(queues = "queue_direct02")
    public void recevie04(Object msg){
        log.info("QUEUE04接受"+msg);
    }

    @RabbitListener(queues = "queue_topic01")
    public void recevie05(Object msg){
        log.info("QUEUE05接受"+msg);
    }
    @RabbitListener(queues = "queue_topic02")
    public void recevie06(Object msg){
        log.info("QUEUE06接受"+msg);
    }

    @RabbitListener(queues = "queue_header01")
    public void recevie07(Message msg){
        log.info("QUEUE07接受"+msg);
        log.info("QUEUE07消息"+new String(msg.getBody()));
    }
    @RabbitListener(queues = "queue_header02")
    public void recevie08(Message msg){
        log.info("QUEUE08接受"+msg);
        log.info("QUEUE08消息"+new String(msg.getBody()));
    }

}
