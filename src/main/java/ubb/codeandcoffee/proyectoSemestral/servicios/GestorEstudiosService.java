package ubb.codeandcoffee.proyectoSemestral.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ubb.codeandcoffee.proyectoSemestral.config.DataSourceConfig;
import ubb.codeandcoffee.proyectoSemestral.config.MultiTenantDataSource;
import ubb.codeandcoffee.proyectoSemestral.modelo.EstudioRegistro;
import ubb.codeandcoffee.proyectoSemestral.modelo.Usuario;
import ubb.codeandcoffee.proyectoSemestral.repositorios.EstudioRegistroRepository;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

@Service
public class GestorEstudiosService {

    @Autowired
    private EstudioRegistroRepository estudioRepo;

    @Autowired
    private DataSource dataSource;

    @Autowired
    private DataSourceConfig dataSourceConfig;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPass;

    //! IMPORTANTE: Nombre del host en Docker (ver docker-compose.yml)
    private final String DB_HOST = "mysqldb"; 
    private final String DB_PLANTILLA = "bdd_formulario";

    public List<EstudioRegistro> listarEstudios() {
        return estudioRepo.findAll();
    }

    @Transactional
    public String crearNuevoEstudio(String nombreVisible, Usuario adminCreador) {
        String safeName = nombreVisible.toLowerCase()
                .replaceAll("\\s+", "_")
                .replaceAll("[^a-z0-9_]", "");
        String nombreDbFisica = "estudio_" + safeName + "_" + System.currentTimeMillis();

        try {
            crearBaseDatosFisica(nombreDbFisica);
            clonarTablas(nombreDbFisica);
            registrarEnSpring(nombreDbFisica);

            EstudioRegistro nuevoRegistro = new EstudioRegistro(nombreDbFisica, nombreVisible, adminCreador);
            estudioRepo.save(nuevoRegistro);

            return "Estudio creado: " + nombreDbFisica;
        } catch (Exception e) {
            e.printStackTrace();
            // Importante: RuntimeException para que @Transactional haga rollback si falla
            throw new RuntimeException("Error: " + e.getMessage());
        }
    }

    private void crearBaseDatosFisica(String nombreDb) throws IOException, InterruptedException {
        // CORREGIDO: Se agrega --skip-ssl para compatibilidad con MariaDB client
        String[] comando = {
                "mysql",
                "--skip-ssl",
                "-h", DB_HOST, 
                "-u" + dbUser,
                "-p" + dbPass,
                "-e",
                "CREATE DATABASE " + nombreDb + " CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;"
        };
        ejecutarComando(comando);
    }

    private void clonarTablas(String nombreDbDestino) throws IOException, InterruptedException {
        String tablasConfig = "seccion datosolicitado opcion criterio criterio_datosolicitado dato_criterio usuario";
        String tablasDatos = "sujetoestudio antecedentes usuario_sujeto";

        // CORREGIDO: Se agrega --skip-ssl
        String hostFlag = " -h " + DB_HOST + " --skip-ssl ";

        String cmdConfig = String.format(
                "mysqldump %s -u%s -p%s %s %s | mysql %s -u%s -p%s %s",
                hostFlag, dbUser, dbPass, DB_PLANTILLA, tablasConfig,
                hostFlag, dbUser, dbPass, nombreDbDestino
        );
        ejecutarComandoBash(cmdConfig);

        String cmdEstructura = String.format(
                "mysqldump %s -u%s -p%s --no-data %s %s | mysql %s -u%s -p%s %s",
                hostFlag, dbUser, dbPass, DB_PLANTILLA, tablasDatos,
                hostFlag, dbUser, dbPass, nombreDbDestino
        );
        ejecutarComandoBash(cmdEstructura);
    }

    private void registrarEnSpring(String nombreDb) {
        if (dataSource instanceof MultiTenantDataSource) {
            MultiTenantDataSource router = (MultiTenantDataSource) dataSource;
            DataSource nuevaConexion = dataSourceConfig.crearConexion(nombreDb);
            router.addDataSource(nombreDb, nuevaConexion);
        }
    }

    private void ejecutarComando(String[] comando) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        leerSalida(p); 
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new RuntimeException("Error comando SQL. Exit: " + exitCode);
    }

    private void ejecutarComandoBash(String comandoCompleto) throws IOException, InterruptedException {
        // CORRECCIÓN CRÍTICA: Cambiado de "/bin/bash" a "/bin/sh" para Alpine Linux
        String[] comando = {"/bin/sh", "-c", comandoCompleto};
        
        ProcessBuilder pb = new ProcessBuilder(comando);
        pb.redirectErrorStream(true);
        Process p = pb.start();
        leerSalida(p);
        int exitCode = p.waitFor();
        if (exitCode != 0) throw new RuntimeException("Error comando Bash. Exit: " + exitCode);
    }

    private void leerSalida(Process p) throws IOException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println("[DB LOG]: " + line);
            }
        }
    }
}