package com.nemo.testing.core.Persistence;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jooq.ConnectionProvider;
import org.jooq.impl.DataSourceConnectionProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for setting up persistence-related beans and properties.
 * <p>
 * This class uses Spring's {@code @Configuration} and {@code @ComponentScan} annotations to mark it as a
 * source of bean definitions and specifies base packages to scan for Spring components, respectively.
 * Additionally, it leverages {@code @Value} annotations to inject properties from the application's configuration file.
 * <p>
 * The following beans are configured:<br/>
 * - {@code ConnectionProvider}: Provides a connection from the data source.<br/>
 * - {@code HikariDataSource}: Configures a HikariCP data source.<br/>
 * - {@code HikariConfig}: Configures properties for the HikariCP data source.<br/>
 * - {@code PasswordEncoder}: Creates a password encoder for encoding passwords.
 */
@Configuration
@ComponentScan(
    basePackages = {"com.nemo.webHub.Decibel", "org.springframework.boot.autoconfigure.jooq"},
    lazyInit = true
)
public class PersistenceConfig {

    @Value("${spring.datasource.url}")
    private String jdbcUrl;

    @Value("${spring.datasource.driver-class-name}")
    private String jdbcDriver;

    @Value("${spring.datasource.username}")
    private String DBUsername;

    @Value("${spring.datasource.password}")
    private String DBPassword;

    @Bean
    public ConnectionProvider connectionProvider() {
        return new DataSourceConnectionProvider(dataSource());
    }

    @Bean
    public HikariDataSource dataSource() {
        return new HikariDataSource(hikariConfig());
    }

    @Bean
    public HikariConfig hikariConfig() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(jdbcUrl);
        config.setUsername(DBUsername);
        config.setPassword(DBPassword);
        config.setDriverClassName(jdbcDriver);
        return config;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
