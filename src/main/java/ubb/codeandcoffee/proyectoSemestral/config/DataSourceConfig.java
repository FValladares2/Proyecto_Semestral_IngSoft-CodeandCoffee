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
    private String dbUrl; // ejem---> jdbc:mysql://localhost:3306/bdd_formulario

    // nombre de la base de datos por defecto
    private final String DEFAULT_DB = "bdd_formulario";

    /**
     * este es el Bean principal. Spring ya no se conectara directamente a una base,
     * sino a este "Enrutador" que decidira a cual ir según el contexto.
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        MultiTenantDataSource routingDataSource = new MultiTenantDataSource();

        // Crear la conexión por defecto (se usa la plantilla)
        // Se usa "bdd_formulario" como clave inicial
        DataSource defaultDataSource = crearConexion(DEFAULT_DB);

        //configurar el mapa inicial de conexiones
        Map<Object, Object> targetDataSources = new HashMap<>();
        targetDataSources.put(DEFAULT_DB, defaultDataSource);
        


        /*  
            codigo para un futuro, sirve para cargar estudios existentes al iniciar,
            pudiendo inyectar el repositorio 'EstudioRegistroRepository' y llenar este mapa con un bucle for.
         */

        routingDataSource.setTargetDataSources(targetDataSources);
        routingDataSource.setDefaultTargetDataSource(defaultDataSource);
        
        return routingDataSource;
    }

    
    /*
        metodo para crear una nueva conexion a una base de datos dada su nombre
    */
    public DataSource crearConexion(String nombreDb) {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("com.mysql.cj.jdbc.Driver");
        
        /* se construlle dinamicamente la URL. 
            en este ejemplo se esta usando 3066 como puerto o el servicio 'mysqldb' en docker, 
            se adpta la url base quitando el nombre de la DB actual.        
        */
        
        String baseUrl = dbUrl.substring(0, dbUrl.lastIndexOf("/") + 1); 
        // esto convierte "jdbc:mysql://localhost:3306/bdd_formulario" en "jdbc:mysql://localhost:3306/"
        
        String nuevaUrl = baseUrl + nombreDb + "?allowPublicKeyRetrieval=true&useSSL=false";
        
        dataSource.setUrl(nuevaUrl);
        dataSource.setUsername(dbUser);
        dataSource.setPassword(dbPass);
        
        return dataSource;
    }
}