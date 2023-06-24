package cn.itcast.feign.fallback;

import cn.itcast.feign.client.RemoteUserService;
import cn.itcast.feign.pojo.User;
import feign.hystrix.FallbackFactory;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserClientFallbackFactory implements FallbackFactory<RemoteUserService> {


    @Override
    public RemoteUserService create(Throwable throwable) {
        return new RemoteUserService() {
            @Override
            public User queryById(Long id) {
                log.error("用户异常");
                return new User();
            }
        };
    }
}
