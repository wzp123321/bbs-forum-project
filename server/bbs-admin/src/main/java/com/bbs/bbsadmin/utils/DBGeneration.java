package com.bbs.bbsadmin.utils;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.config.rules.DateType;
import com.baomidou.mybatisplus.generator.config.rules.NamingStrategy;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;

public class DBGeneration {

    protected static final Logger log = LoggerFactory.getLogger(Logger.class);

    // 配置数据源
    private static final DataSourceConfig.Builder dataSourceConfig = new DataSourceConfig.Builder("jdbc:mysql://localhost:3306/bbs?serverTimezone=UTC&useUnicode=true&characterEncoding=utf-8", "root", "123456")
            .schema("bbs")
            .keyWordsHandler(new MySqlKeyWordsHandler());

    private static final String outputDir = System.getProperty("user.dir") + "/src/main/java";

    public static void main(String[] args) {
        log.info("======= Done 相关代码开始生成  ========");
        FastAutoGenerator.create(dataSourceConfig)
                .globalConfig(builder -> {
                    builder.author("zhcao") // 设置作者
                            .outputDir(outputDir) // 指定输出目录
                            .disableOpenDir() //禁止打开输出目录
                            .dateType(DateType.TIME_PACK)
                            .commentDate("yyyy-MM-dd");
                })
                .packageConfig(builder -> {
                    // builder.parent("com.ya.boottest.fruit.autoCode"); // 设置父包名
                    builder.parent("com.bbs.bbsadmin") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, System.getProperty("user.dir") + "/src/main/resources/mapper"));
                })
                .strategyConfig(builder -> {
                    builder.addInclude("user_info")                             //设置生成需要映射的表名
                            .enableSchema()                                     //开启Schema
                            .serviceBuilder()                                   //构建Service层
                            .formatServiceFileName("%sService")                 //业务层接口类命名
                            .formatServiceImplFileName("%sServiceImpl")         //业务层接口实现类命名

                            .entityBuilder().enableFileOverride()               //构建实体类
                            .columnNaming(NamingStrategy.underline_to_camel)    //字段名驼峰命名
                            .naming(NamingStrategy.underline_to_camel)          //表名驼峰命名
                            .enableTableFieldAnnotation()                       //启动字段注解
                            .idType(IdType.INPUT)
                            .enableLombok()

                            .controllerBuilder()                                //构建Controller类
                            .formatFileName("%sController")                     //Controller类命名
                            .enableRestStyle()                                  //标记@RestController注解

                            .mapperBuilder().enableFileOverride()               //构建mapper接口类
                            .enableBaseResultMap()                              //生成基本的resultMap
                            .formatMapperFileName("%sMapper")                   //Mapper接口类明名
                            .formatXmlFileName("%sMapper")                      //Mapper.xml命名
                            .enableBaseColumnList()                             //生成基本的SQL片段
                            .build();
                })
                .templateEngine(new FreemarkerTemplateEngine())
                .execute();
        log.info("======= Done 相关代码生成完毕  ========");
    }
}
