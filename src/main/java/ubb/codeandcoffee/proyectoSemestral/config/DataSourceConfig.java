package ubb.codeandcoffee.proyectoSemestral.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class DataSourceConfig {

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    @Value("${spring.datasource.url}")
    private String dbUrl; // Viene del properties: jdbc:mysql://mysqldb:3306/bdd_formulario...

    private final String DEFAULT_DB = "bdd_formulario";

    @Bean
    @Primary
    public DataSource dataSource() {
        MultiTenantDataSource routingDataSource = new MultiTenantDataSource();

        // configuramos la conexión por defecto
        DataSource defaultDataSource = crearConexion(DEFAULT_DB);

        // mapa inicial
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DEFAULT_DB, defaultDataSource);

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);

        return routingDataSource;
    }

    /**
      crea una conexión a una base de datos específica.
      Maneja la URL de Docker correctamente.
     */
    public DataSource crearConexion(String nombreDb) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");

       /*
            se usa para limpiar la url de la base de datos y reconstruirla
            entrada: jdbc:mysql://mysqldb:3306/bdd_formulario?opciones...
            salida: jdbc:mysql://mysqldb:3306/nuevo_estudio?opciones
        */
        
        String cleanUrl = dbUrl;
        // se cortan los parámetros GET si es que existen
        if (cleanUrl.contains("?")) {
            cleanUrl = cleanUrl.substring(0, cleanUrl.indexOf("?"));
        }
        // obtenemos la raíz (jdbc:mysql://mysqldb:3306/)
        String baseUrl = cleanUrl.substring(0, cleanUrl.lastIndexOf("/") + 1);
        
        // reconstruimos con los parámetros necesarios para Docker
        String nuevaUrl = baseUrl + nombreDb + "?allowPublicKeyRetrieval=true&useSSL=false&createDatabaseIfNotExist=false";

        dataSource.setUrl(nuevaUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);

        return dataSource;
    }
}