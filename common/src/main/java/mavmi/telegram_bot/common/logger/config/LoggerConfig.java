package mavmi.telegram_bot.common.logger.config;

import jakarta.annotation.PostConstruct;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.appender.db.jdbc.ColumnConfig;
import org.apache.logging.log4j.core.appender.db.jdbc.FactoryMethodConnectionSource;
import org.apache.logging.log4j.core.appender.db.jdbc.JdbcAppender;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class LoggerConfig {

    private static DataSource DATA_SOURCE;

    public LoggerConfig(DataSource dataSource) {
        DATA_SOURCE = dataSource;
    }

    @PostConstruct
    public void setupDatabaseLogAppender() {
        LoggerContext loggerContext = (LoggerContext) LogManager.getContext(false);
        org.apache.logging.log4j.core.config.Configuration configuration = loggerContext.getConfiguration();

        FactoryMethodConnectionSource connectionSource = FactoryMethodConnectionSource.createConnectionSource(
                "mavmi.telegram_bot.common.logger.config.LoggerConfig",
                "getDataSource"
        );

        JdbcAppender jdbcAppender = JdbcAppender.newBuilder()
                .setName("DATABASE")
                .setConnectionSource(connectionSource)
                .setTableName("logs.logs")
                .setColumnConfigs(getColumnConfigs(configuration))
                .build();

        jdbcAppender.start();

        configuration.addAppender(jdbcAppender);
        configuration.getRootLogger().addAppender(jdbcAppender, Level.INFO, null);

        loggerContext.updateLoggers();
    }

    public static DataSource getDataSource() {
        return DATA_SOURCE;
    }

    private ColumnConfig[] getColumnConfigs(org.apache.logging.log4j.core.config.Configuration configuration) {
        return new ColumnConfig[]{
                ColumnConfig.newBuilder()
                        .setConfiguration(configuration)
                        .setName("timestamp")
                        .setPattern(null)
                        .setLiteral(null)
                        .setEventTimestamp(true)
                        .setUnicode(false)
                        .setClob(false)
                        .build(),
                ColumnConfig.newBuilder()
                        .setConfiguration(configuration)
                        .setName("level")
                        .setPattern("%level")
                        .setLiteral(null)
                        .setEventTimestamp(false)
                        .setUnicode(false)
                        .setClob(false)
                        .build(),
                ColumnConfig.newBuilder()
                        .setConfiguration(configuration)
                        .setName("application")
                        .setPattern("${spring:spring.application.name}")
                        .setLiteral(null)
                        .setEventTimestamp(false)
                        .setUnicode(false)
                        .setClob(false)
                        .build(),
                ColumnConfig.newBuilder()
                        .setConfiguration(configuration)
                        .setName("message")
                        .setPattern("%message")
                        .setLiteral(null)
                        .setEventTimestamp(false)
                        .setUnicode(false)
                        .setClob(false)
                        .build(),
                ColumnConfig.newBuilder()
                        .setConfiguration(configuration)
                        .setName("logger")
                        .setPattern("%logger")
                        .setLiteral(null)
                        .setEventTimestamp(false)
                        .setUnicode(false)
                        .setClob(false)
                        .build()
        };
    }
}
