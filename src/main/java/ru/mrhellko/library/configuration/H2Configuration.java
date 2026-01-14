package ru.mrhellko.library.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.h2.tools.Server;

import java.sql.SQLException;

@Configuration
public class H2Configuration {

    /**
     * Дает возможность подключаться к in-memory БД h2 во время работы приложения
     * По следующему адресу: jdbc:h2:tcp://localhost:9090/mem:library
     */
    @Bean(initMethod = "start", destroyMethod = "stop")
    public Server inMemoryH2DatabaseServer() throws SQLException {
        return Server.createTcpServer("-tcp", "-tcpAllowOthers", "-tcpPort", "9090");
    }

}
