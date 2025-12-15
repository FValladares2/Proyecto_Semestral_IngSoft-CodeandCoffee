package ubb.codeandcoffee.proyectoSemestral.config;

public class DBContextHolder {

    // guardamos el nombre de la base de datos actual en un ThreadLocal
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setDBType(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getDBType() {
        return contextHolder.get();
    }

    public static void clearDBType() {
        contextHolder.remove();
    }
    
}
