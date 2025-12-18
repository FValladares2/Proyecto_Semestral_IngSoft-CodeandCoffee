package ubb.codeandcoffee.proyectoSemestral.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ubb.codeandcoffee.proyectoSemestral.modelo.EstudioRegistro;
import ubb.codeandcoffee.proyectoSemestral.repositorios.EstudioRegistroRepository;

import javax.sql.DataSource;
import java.util.List;

@Component
public class DataSourceInitializer implements CommandLineRunner {

    private final EstudioRegistroRepository estudioRepository;
    private final DataSource dataSource; // Este es el bean principal (MultiTenantDataSource)
    private final DataSourceConfig dataSourceConfig;

    public DataSourceInitializer(EstudioRegistroRepository estudioRepository, 
                                 DataSource dataSource,
                                 DataSourceConfig dataSourceConfig) {
        this.estudioRepository = estudioRepository;
        this.dataSource = dataSource;
        this.dataSourceConfig = dataSourceConfig;
    }

    @Override
    public void run(String... args) {
        
        // Verificamos que el DataSource inyectado sea de nuestro tipo personalizado
        if (dataSource instanceof MultiTenantDataSource) {
            MultiTenantDataSource routingDataSource = (MultiTenantDataSource) dataSource;

            System.out.println(">>> [INIT] Buscando estudios existentes para restaurar conexiones...");
            
            
            List<EstudioRegistro> estudios = estudioRepository.findAll();

            if (estudios.isEmpty()) {
                System.out.println(">>> [INIT] No hay estudios registrados. Sistema limpio.");
                return;
            }

            int count = 0;
            for (EstudioRegistro estudio : estudios) {
                
                String nombreDb = estudio.getNombreBd(); 
                
                if (nombreDb != null && !nombreDb.isEmpty()) {
                    try {
                        
                        DataSource nuevaConexion = dataSourceConfig.crearConexion(nombreDb);
                        
                        // Registramos en el mapa de beans activos
                        routingDataSource.addDataSource(nombreDb, nuevaConexion);
                        count++;
                    } catch (Exception e) {
                        System.err.println(">>> [ERROR] Falló la restauración de: " + nombreDb + ". Motivo: " + e.getMessage());
                    }
                }
            }
            System.out.println(">>> [INIT] Sistema listo. Se restauraron " + count + " conexiones de estudios.");
        }
    }
}