package ubb.codeandcoffee.proyectoSemestral.config;

public class DBContextHolder {
    
    // guarda el nombre de la DB actual para el hilo en curso
    private static final ThreadLocal<String> contextHolder = new ThreadLocal<>();

    public static void setCurrentDb(String dbType) {
        contextHolder.set(dbType);
    }

    public static String getCurrentDb() {
        return contextHolder.get();
    }

    public static void clear() {
        contextHolder.remove();
    }
}