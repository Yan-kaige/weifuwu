package cn.itcast.mq.config;

import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class LazyConfig {

    @Bean
    public Queue lazy(){
        return QueueBuilder.durable("lazyQ").lazy().build();
    }

    @Bean
    public Queue normal(){
        return QueueBuilder.durable("normalQ").build();
    }
}
