package feign.feign;

import com.huihuang.service.MemberService;
import feign.codec.Encoder;
import feign.form.spring.SpringFormEncoder;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;

@FeignClient(value = "app-huihuang-member", configuration = MemberServiceFeign.MultipartSupportConfig.class)
public interface MemberServiceFeign extends MemberService {

    class MultipartSupportConfig {
        @Bean
        public Encoder feignFormEncoder() {
            return new SpringFormEncoder();
        }
    }
}
