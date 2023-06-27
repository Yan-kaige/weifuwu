package cn.itcast.mq.listener;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SpringRabbitListener {

    @RabbitListener(queues = "kai.queue")
    public void listenSimpleQueue(String msg) {
        System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");
        System.out.println(1/0);
        System.out.println("消息处理成功");
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("dl.queue"),
            exchange = @Exchange("dl.direct"),
            key = "dl"
    ))
    public void dlListen(String msg){
            log.info("接收到私信消息"+msg);
    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue("delay.queue"),
            exchange = @Exchange(value = "delay.direct",delayed = "true"),
            key = "delay"
    ))
    public void delayListen(String msg){
        log.info("接收到延迟消息"+msg);
    }


    @RabbitListener(queues = "lazyQ")
    public void lq(String msg) {
        System.out.println("消费者接收到simple.queue的消息：【" + msg + "】");

    }

}
