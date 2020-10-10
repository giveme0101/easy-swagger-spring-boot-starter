package com.github.swagger;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.context.config.annotation.RefreshScope;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * @Author kevin xiajun94@FoxMail.com
 * @Description
 * @name SwaggerAutoConfiguration
 * @Date 2019/09/18 15:50
 */
@RefreshScope
@Configuration
@Import({
        Swagger2.class,
        WebMvcConfig.class
})
@EnableConfigurationProperties(SwaggerConfigProperties.class)
public class SwaggerAutoConfiguration {

}