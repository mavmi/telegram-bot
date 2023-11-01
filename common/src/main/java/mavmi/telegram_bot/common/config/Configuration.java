package mavmi.telegram_bot.common.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Profile;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.PreparedStatement;

@org.springframework.context.annotation.Configuration
@ComponentScan("mavmi.telegram_bot.common")
public class Configuration {
    @Profile("DEV")
    @Bean("DataSource")
    public DataSource getDevDataSource(
            @Value("${db.url}") String dbUrl,
            @Value("${db.username}") String dbUsername,
            @Value("${db.password}") String dbPassword,
            @Value("${db.driver.name}") String dbDriver
    ){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);

        runSqlQueries(dataSource);

        return dataSource;
    }

    @Profile("PROM")
    @Bean("DataSource")
    public DataSource getPromDataSource(
            @Value("${db.url}") String dbUrl,
            @Value("${db.username}") String dbUsername,
            @Value("${db.password}") String dbPassword,
            @Value("${db.driver.name}") String dbDriver
    ){
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setUrl(dbUrl);
        dataSource.setUsername(dbUsername);
        dataSource.setPassword(dbPassword);
        dataSource.setDriverClassName(dbDriver);

        return dataSource;
    }

    private void runSqlQueries(DataSource dataSource) {
        for (String filePath : new String[]{ "/sql/user.sql", "/sql/request.sql", "/sql/rule.sql" }) {
            String sqlQuery = readFile(filePath);
            try {
                Connection connection = dataSource.getConnection();
                PreparedStatement statement = connection.prepareStatement(sqlQuery);
                if (statement == null) {
                    throw new RuntimeException("Cannot create prepared statement for file " + filePath);
                }
                statement.execute();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private String readFile(String filePath) {
        InputStream inputStream = Configuration.class.getResourceAsStream(filePath);
        if (inputStream == null) {
            throw new RuntimeException("Cannot open resource file " + filePath);
        }

        try (BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;

            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append(" ");
            }

            return stringBuilder.toString();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
