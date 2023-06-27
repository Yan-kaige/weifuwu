package cn.itcast.mq.config;


import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class TTLMessageConfig
{

    @Bean
    public DirectExchange directExchange(){
        return  new DirectExchange("ttlEx");
    }


    @Bean
    public Queue ttlQueue(){
        return QueueBuilder
                .durable("ttlQueue")
                .ttl(10000)
                .deadLetterExchange("dl.direct")
                .deadLetterRoutingKey("dl")
                .build();
    }


    @Bean
    public Binding ttlBind(){
        return BindingBuilder.bind(ttlQueue()).to(directExchange()).with("ttl");
    }


}
