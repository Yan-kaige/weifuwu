package cn.itcast.feign.client;

import cn.itcast.feign.fallback.UserClientFallbackFactory;
import cn.itcast.feign.pojo.User;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "userservice",fallbackFactory = UserClientFallbackFactory.class)
public interface RemoteUserService {

    @GetMapping("/user/{id}")
    User queryById(@PathVariable("id") Long id) ;
}
