package org.springframework.cloud.openfeign.ribbon;

import com.huihuang.feign.properties.ReinforceFeignProperties;
import com.huihuang.feign.ribbon.ReinforceFeignOptionsClientConfig;
import com.netflix.client.config.IClientConfig;
import feign.Client;
import feign.Request;
import org.springframework.cloud.netflix.ribbon.SpringClientFactory;

/**
 * @创建人:Raiden
 * @Descriotion:
 * @Date:Created in 23:28 2021/11/8
 * @Modified By:
 */
public class ReinforceLoabBalancerFeignClient extends LoadBalancerFeignClient {

    private ReinforceFeignProperties properties;

    public ReinforceLoabBalancerFeignClient(Client delegate, CachingSpringLoadBalancerFactory lbClientFactory, SpringClientFactory clientFactory) {
        super(delegate, lbClientFactory, clientFactory);
    }

    @Override
    protected IClientConfig getClientConfig(Request.Options options, String clientName) {
        if (options == null){
            throw new NullPointerException("The options parameter cannot be Null!");
        }
        return new ReinforceFeignOptionsClientConfig(options);
    }
}
