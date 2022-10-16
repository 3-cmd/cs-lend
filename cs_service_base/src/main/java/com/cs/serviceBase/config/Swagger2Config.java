package com.cs.serviceBase.config;

import com.google.common.base.Predicates;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

@Configuration
@EnableSwagger2
public class Swagger2Config {
    //将admin开头的路径展示为adminApi分组
    @Bean
    public Docket adminConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("adminApi")
                .apiInfo(adminApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/admin/.*")))
                .build();
    }
    //admin为文档进行标题的添加来描述文档
    private ApiInfo adminApiInfo(){
        return new ApiInfoBuilder().title("cs借贷后台管理系统API文档")
                .description("这个文档主要是用来描述后台管理系统的各个接口")
                .version("1.0")
                .contact(new Contact("CS","https://github.com/3-cmd","1397368928@qq.com"))
                .build();
    }

    //将api开头的路径展示为webApi分组
    @Bean
    public Docket webConfig(){
        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .paths(Predicates.and(PathSelectors.regex("/api/.*")))
                .build();
    }
    //为文档进行标题的添加来描述文档
    private ApiInfo webApiInfo(){
        return new ApiInfoBuilder().title("cs借贷web前端管理系统API文档")
                .description("这个文档主要是用来描述web前端系统")
                .version("1.0")
                .contact(new Contact("CS","https://github.com/3-cmd","1397368928@qq.com"))
                .build();
    }
}
