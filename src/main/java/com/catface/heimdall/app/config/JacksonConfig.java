//package com.catface.heimdall.app.config;
//
//
//import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
//import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
///**
// * @author by 大猫
// * @date 2022/3/24 10:55 PM catface996 出品
// */
//@Configuration
//public class JacksonConfig {
//
//    /**
//     * Jackson全局转化long类型为String，解决jackson序列化时long类型缺失精度问题
//     *
//     * @return Jackson2ObjectMapperBuilderCustomizer 注入的对象
//     */
//    @Bean
//    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
//        return jacksonObjectMapperBuilder -> {
//            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
//            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);
//        };
//    }
//}
