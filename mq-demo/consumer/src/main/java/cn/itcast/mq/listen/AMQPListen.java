package cn.itcast.mq.listen;

import org.springframework.amqp.core.ExchangeTypes;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.time.LocalTime;
import java.util.Map;

@Component
public class AMQPListen {

//    @RabbitListener(queues = "kai66")
//    public void listenSimpleQueueMessage(Map<String,Object> msg) throws InterruptedException {
//        System.out.println("spring 消费者接收到消息：【" + msg + "】");
//    }

//    @RabbitListener(queues = "simple.queue")
//    public void listenSimpleQueueMessage1(String msg) throws InterruptedException {
//        System.out.println("spring 1消费者接收到消息：【" + msg + "】"+LocalTime.now());
//        Thread.sleep(20);
//    }
//
//    @RabbitListener(queues = "simple.queue")
//    public void listenSimpleQueueMessage2(String msg) throws InterruptedException {
//        System.err.println("spring 2消费者接收到消息：【" + msg + "】"+ LocalTime.now());
//        Thread.sleep(200);
//    }


//    @RabbitListener(queues = "kai.queue1")
//    public void listenSimpleQueueMessage1(String msg) throws InterruptedException {
//        System.out.println("spring 1消费者接收到消息：【" + msg + "】"+LocalTime.now());
//    }
//
//    @RabbitListener(queues = "kai.queue2")
//    public void listenSimpleQueueMessage2(String msg) throws InterruptedException {
//        System.err.println("spring 2消费者接收到消息：【" + msg + "】"+ LocalTime.now());
//    }


//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "kai.directQueue1"),
//            exchange =@Exchange(name = "kai.directEx"),
//            key = {"red","blue"}
//            ))
//    public void listenSimpleQueueMessage1(String msg) throws InterruptedException {
//        System.out.println("spring 1消费者接收到direct消息：【" + msg + "】"+LocalTime.now());
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "kai.directQueue2"),
//            exchange =@Exchange(name = "kai.directEx"),
//            key = {"yellow","blue"}
//    ))
//    public void listenSimpleQueueMessage2(String msg) throws InterruptedException {
//        System.err.println("spring 2消费者接收到direct消息：【" + msg + "】"+ LocalTime.now());
//    }

//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "kai.topicQueue15"),
//            exchange =@Exchange(name = "kai.topicEx",type = ExchangeTypes.TOPIC),
//            key = {"china.#"}
//    ))
//    public void listenSimpleQueueMessage1(Map<String,Object> msg) throws InterruptedException {
//        System.out.println("spring 1消费者接收到topic消息：【" + msg + "】"+LocalTime.now());
//    }
//
//    @RabbitListener(bindings = @QueueBinding(
//            value = @Queue(name = "kai.topicQueue2"),
//            exchange =@Exchange(name = "kai.topicEx",type = ExchangeTypes.TOPIC),
//            key = {"#.news"}
//    ))
//    public void listenSimpleQueueMessage2(String msg) throws InterruptedException {
//        System.err.println("spring 2消费者接收到topic消息：【" + msg + "】"+ LocalTime.now());
//    }

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(name = "kai.topicQueue8848"),
            exchange =@Exchange(name = "kai.topicEx",type = ExchangeTypes.TOPIC),
            key = {"#.news"}
    ))
    public void listenSimpleQueueMessage2(Map<String,Object> msg) throws InterruptedException {
        System.err.println("spring 2消费者接收到topic消息：【" + msg + "】"+ LocalTime.now());
    }

}
