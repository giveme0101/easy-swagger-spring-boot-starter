package com.github.swagger;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

import java.util.List;

/**
 * @Author kevin xiajun94@FoxMail.com
 * @Description
 * @name SwaggerConfigProperties
 * @Date 2019/09/18 9:39
 */
@Data
@ConfigurationProperties(prefix = "swagger")
public class SwaggerConfigProperties {

    /**
     * 是否启用swagger
     */
    private boolean enable = true;

    /**
     * k8s配置
     */
    @NestedConfigurationProperty
    private Config config;

    private List<Dock> dockets;

    @Data
    public static class Config {
        /**
         * k8s访问前缀
         */
        private String prefix;
    }

    @Data
    public static class Dock {
        /**
         * 是否启用改docket
         */
        private boolean enable = true;
        /**
         * 扫描配置
         */
        @NestedConfigurationProperty
        private Api api;
        /**
         * 联系人配置
         */
        @NestedConfigurationProperty
        private Contact contact;
        /**
         * 全局变量配置
         */
        private List<Parameters> parameters;

        @Data
        public static class Api{
            /**
             * 名称
             */
            private String groupName;
            /**
             * 扫描包
             */
            private String basePackage;
            /**
             * 版本
             */
            private String version;
            /**
             * 标题
             */
            private String title;
            /**
             * 描述
             */
            private String description;
        }

        @Data
        public static class Contact {
            /**
             * 联系作者
             */
            private String name;
            /**
             * 联系url
             */
            private String url;
            /**
             * 联系人邮箱
             */
            private String email;
        }

        @Data
        public static class Parameters {
            /**
             * 变量名
             */
            private String name;
            /**
             * 字段描述
             */
            private String description;
            /**
             * 默认值
             */
            private String defaultValue;
            /**
             * 是否必填字段
             */
            private Boolean required = false;
            /**
             * 是否允许多个
             */
            private Boolean allowMultiple = false;
//        private ModelReference modelRef;
            /**
             * 类型
             */
            private String modelRefType = "string";
//        private Optional<ResolvedType> type;
//        private AllowableValues allowableValues;
            /**
             * 参数位置 header, cookie, body, query
             */
            private String paramType = "query";
//        private String paramAccess;
//        private Boolean hidden;
//        private String pattern;
//        private String collectionFormat;
//        private int order;
//        private Object scalarExample;
//        private Multimap<String, Example> examples;
//        private List<VendorExtension> vendorExtensions;
            /**
             * 是否允许为空
             */
            private Boolean allowEmptyValue = false;
        }
    }
}