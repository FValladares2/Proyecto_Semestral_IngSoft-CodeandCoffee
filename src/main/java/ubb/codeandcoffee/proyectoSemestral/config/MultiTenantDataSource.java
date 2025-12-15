package ubb.codeandcoffee.proyectoSemestral.config;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;
import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MultiTenantDataSource extends AbstractRoutingDataSource {

    // usamos un mapa local para poder agregar conexiones dinámicamente
    private final Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();

    @Override
    protected Object determineCurrentLookupKey() {
        return DBContextHolder.getCurrentDb();
    }

    // permite agregar una nueva DB mientras la app corre
    public void addDataSource(String key, DataSource dataSource) {
        targetDataSources.put(key, dataSource);
        this.setTargetDataSources(targetDataSources);
        this.afterPropertiesSet(); // refresca la configuracion de Spring
    }
}