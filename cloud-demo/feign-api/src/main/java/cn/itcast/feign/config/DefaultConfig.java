package cn.itcast.feign.config;

import cn.itcast.feign.fallback.UserClientFallbackFactory;
import feign.Logger;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Component
public class DefaultConfig {

    @Bean
    public Logger.Level logLevel(){
        return  Logger.Level.BASIC;
    }


    @Bean
    public UserClientFallbackFactory userClientFallbackFactory(){
        return  new UserClientFallbackFactory();
    }
}
