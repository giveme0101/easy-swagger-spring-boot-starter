package com.github.swagger;

import com.github.xiaoymin.swaggerbootstrapui.annotations.EnableSwaggerBootstrapUI;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Assert;
import org.springframework.util.CollectionUtils;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.ParameterBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.ModelRef;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.service.Parameter;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.paths.RelativePathProvider;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import javax.annotation.Resource;
import javax.servlet.ServletContext;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

/**
 * @Author kevin xiajun94@FoxMail.com
 * @Description
 * @name Swagger2
 * @Date 2020/05/28 9:21
 */
@Slf4j
@Configuration
@EnableSwagger2
@EnableSwaggerBootstrapUI
//@Profile(value = {"dev","test"}
@EnableConfigurationProperties(SwaggerConfigProperties.class)
@ConditionalOnProperty(name = "swagger.enable", havingValue = "true")
public class Swagger2 implements ApplicationContextAware {

    @Resource
    private ServletContext servletContext;
    @Resource
    private SwaggerConfigProperties configProperties;

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        List<SwaggerConfigProperties.Dock> dockets = configProperties.getDockets();
        if (CollectionUtils.isEmpty(dockets)){
            log.warn("未配置docks，无法加载swagger");
            return;
        }

        for (final SwaggerConfigProperties.Dock dock : dockets) {
            if (dock.isEnable()) {
                Docket docket = bind(dock);
                ((ConfigurableApplicationContext) applicationContext).getBeanFactory().registerSingleton(docket.getGroupName(), docket);
                log.info("create swagger docket：{}, scan package：{}", dock.getApi().getGroupName(), dock.getApi().getBasePackage());
            }
        }
    }

    private Docket bind(SwaggerConfigProperties.Dock dock) {

        SwaggerConfigProperties.Config config = configProperties.getConfig();
        SwaggerConfigProperties.Dock.Api api = dock.getApi();

        Assert.notNull(api, "swagger api 配置项不能为空");

        Docket docket = new Docket(DocumentationType.SWAGGER_2)
                .groupName(StringUtils.isEmpty(api.getGroupName()) ? "default" : api.getGroupName())
                .select()
                .apis(RequestHandlerSelectors.basePackage(api.getBasePackage()))
                .paths(PathSelectors.any())
                .build();

        if (null != config && StringUtils.isNotBlank(config.getPrefix())){
            docket.pathProvider(new RelativePathProvider(servletContext){
                @Override
                public String getApplicationBasePath() {
                    return config.getPrefix();
                }
            });
        }

        getGlobalParam(dock).ifPresent(docket::globalOperationParameters);
        return docket.apiInfo(apiInfo(dock));
    }

    private Optional<List<Parameter>> getGlobalParam(SwaggerConfigProperties.Dock dock){

        List<SwaggerConfigProperties.Dock.Parameters> parameters = dock.getParameters();
        if (CollectionUtils.isEmpty(parameters)) return Optional.empty();

        List<Parameter> parameterList = new ArrayList<>(parameters.size());
        Optional.ofNullable(parameters).ifPresent(params ->
                params.forEach(p -> {
                    ParameterBuilder builder = new ParameterBuilder();
                    if (StringUtils.isNotBlank(p.getName())) builder.name(p.getName());
                    if (StringUtils.isNotBlank(p.getDescription())) builder.description(p.getDescription());
                    if (StringUtils.isNotBlank(p.getDefaultValue())) builder.defaultValue(p.getDefaultValue());
                    if (Objects.nonNull(p.getRequired())) builder.required(p.getRequired());
                    if (StringUtils.isNotBlank(p.getParamType())) builder.parameterType(p.getParamType());
                    if (Objects.nonNull(p.getAllowMultiple())) builder.allowMultiple(p.getAllowMultiple());
                    if (Objects.nonNull(p.getAllowEmptyValue())) builder.allowEmptyValue(p.getAllowEmptyValue());
                    if (StringUtils.isNotBlank(p.getModelRefType())) builder.modelRef(new ModelRef(p.getModelRefType()));
                    parameterList.add(builder.build());
                })
        );

        return Optional.of(parameterList);
    }

    private ApiInfo apiInfo(SwaggerConfigProperties.Dock dock) {

        SwaggerConfigProperties.Dock.Api api = dock.getApi();
        SwaggerConfigProperties.Dock.Contact contact = dock.getContact();

        ApiInfoBuilder builder = new ApiInfoBuilder().title(api.getTitle()).version(api.getVersion());
        Optional.ofNullable(contact).ifPresent(c -> {
            builder.contact(new Contact(contact.getName(), contact.getUrl(), contact.getEmail()));
        });

        return builder.build();
    }

}