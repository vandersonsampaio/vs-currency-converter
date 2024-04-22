package br.com.vs.currency.converter.config;

import br.com.vs.currency.converter.client.CustomErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class FeignClientConfig {

    @Bean
    public CustomErrorDecoder myErrorDecoder() {
        return new CustomErrorDecoder();
    }
}
