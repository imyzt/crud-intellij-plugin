<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>2.1.4.RELEASE</version>
    </parent>

    <groupId>${groupId}</groupId>
    <artifactId>${artifactId}</artifactId>
    <version>${version}</version>
    <packaging>jar</packaging>

    <properties>
        <project.build.sourceEncoding>UTF-8</project.build.sourceEncoding>
        <project.reporting.outputEncoding>UTF-8</project.reporting.outputEncoding>
        <java.version>1.8</java.version>
    <#if ormType==0>
        <mybatis.plus.version>3.1.1</mybatis.plus.version>
    <#elseif ormType==1>
        <mybatis.version>2.0.1</mybatis.version>
        <pagehelper.version>1.2.10</pagehelper.version>
    </#if>
        <swagger.version>2.7.0</swagger.version>
        <druid.version>1.0.5</druid.version>
    </properties>

    <dependencies>

        <!--spring-->
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>

        <!--db dependency-->
    <#if ormType==0>
        <dependency>
            <groupId>com.baomidou</groupId>
            <artifactId>mybatis-plus-boot-starter</artifactId>
            <version>3.1.1</version>
        </dependency>
    <#elseif ormType==1>
        <dependency>
            <groupId>org.mybatis.spring.boot</groupId>
            <artifactId>mybatis-spring-boot-starter</artifactId>
            <version>2.0.1</version>
        </dependency>
        <dependency>
            <groupId>com.github.pagehelper</groupId>
            <artifactId>pagehelper-spring-boot-starter</artifactId>
            <version>1.2.10</version>
        </dependency>
    <#else>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
    </#if>
        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
        </dependency>

        <!--tool-->
        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
        </dependency>

        <!--doc-->
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger-ui</artifactId>
            <version>${r'${swagger.version}'}</version>
        </dependency>
        <dependency>
            <groupId>io.springfox</groupId>
            <artifactId>springfox-swagger2</artifactId>
            <version>${r'${swagger.version}'}</version>
        </dependency>
    </dependencies>

    <build>
        <plugins>
            <plugin>
                <groupId>org.springframework.boot</groupId>
                <artifactId>spring-boot-maven-plugin</artifactId>
            </plugin>
            <#if dockerfileSelected>
            <plugin>
                <groupId>com.spotify</groupId>
                <artifactId>docker-maven-plugin</artifactId>
                <version>1.2.0</version>
                <configuration>
                    <imageName>${r'${artifactId}'}:${r'${project.version}'}</imageName>
                    <dockerDirectory>${r'${project.basedir}'}/src/main/docker</dockerDirectory>
                    <resources>
                        <resoulsrce>
                            <targetPath>/</targetPath>
                            <directory>${r'${project.build.directory}'}</directory>
                            <include>${r'${project.build.finalName}'}.jar</include>
                        </resoulsrce>
                    </resources>
                </configuration>
                <executions>
                    <execution>
                        <id>build-image</id>
                        <phase>package</phase>
                        <goals>
                            <goal>build</goal>
                        </goals>
                    </execution>
                    <execution>
                        <id>push-image</id>
                        <phase>deploy</phase>
                        <goals>
                            <goal>push</goal>
                        </goals>
                        <configuration>
                            <imageName>${r'${artifactId}'}:${r'${project.version}'}</imageName>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
            </#if>
            <#if jibSelected>
                <plugin>
                    <groupId>com.google.cloud.tools</groupId>
                    <artifactId>jib-maven-plugin</artifactId>
                    <version>1.6.1</version>
                    <configuration>
                        <from>
                            <image>openjdk:8u242-jre-slim</image>
                        </from>
                        <to>
                            <#--配置镜像仓库地址-->
                            <image>${r'${artifactId}'}</image>
                            <tags>
                                <tag>${r'${project.version}'}</tag>
                            </tags>
                        </to>
                        <container>
                            <mainClass>${package}.Application</mainClass>
                            <jvmFlags>
                                <jvmFlag>-server</jvmFlag>
                                <jvmFlag>-Xms512m</jvmFlag>
                                <jvmFlag>-Xmx512m</jvmFlag>
                                <jvmFlag>-Djava.awt.headless=true</jvmFlag>
                                <jvmFlag>-Duser.timezone=PRC</jvmFlag>
                            </jvmFlags>
                            <environment>
                                <server.port>8080</server.port>
                            </environment>
                            <ports>
                                <port>80</port>
                            </ports>
                            <useCurrentTimestamp>true</useCurrentTimestamp>
                        </container>
                        <allowInsecureRegistries>true</allowInsecureRegistries>
                    </configuration>
                    <executions>
                        <execution>
                            <id>build-and-push-docker-image</id>
                            <phase>package</phase>
                            <goals>
                                <goal>build</goal>
                            </goals>
                        </execution>
                    </executions>
                </plugin>
            </#if>
        </plugins>
    </build>

</project>
