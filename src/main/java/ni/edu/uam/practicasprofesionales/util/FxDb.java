package ni.edu.uam.practicasprofesionales.util;

import javafx.concurrent.Task;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.function.Consumer;

/** Ejecuta operaciones de BD fuera del hilo JavaFX y entrega resultados en ese hilo. */
public final class FxDb {
    private static final ExecutorService EXECUTOR = Executors.newFixedThreadPool(2,
        new ThreadFactory() {
            private int siguiente = 1;
            @Override public synchronized Thread newThread(Runnable trabajo) {
                Thread hilo = new Thread(trabajo, "hibernate-fx-" + siguiente++);
                hilo.setDaemon(true);
                return hilo;
            }
        });

    private FxDb() { }

    public static <T> void ejecutar(Callable<T> accion,
                                   Consumer<T> correcto,
                                   Consumer<Throwable> fallo) {
        Task<T> tarea = new Task<>() {
            @Override protected T call() throws Exception {
                return accion.call();
            }
        };
        tarea.setOnSucceeded(evento -> correcto.accept(tarea.getValue()));
        tarea.setOnFailed(evento -> fallo.accept(tarea.getException()));
        EXECUTOR.execute(tarea);
    }

    public static void cerrar() {
        EXECUTOR.shutdown();
    }
}
