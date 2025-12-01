package lexicoparte1;

import java.util.*;
import java.io.*;

public class TablaSimbolos {
    private LinkedHashMap<String, Simbolo> tabla = new LinkedHashMap<>();

    public void agregar(String nombre, String token, Object valor, int longitud) {
        if (!tabla.containsKey(nombre)) {
            tabla.put(nombre, new Simbolo(nombre, token, null, valor, longitud));
        }
    }

    public Simbolo buscar(String nombre) {
        return tabla.get(nombre);
    }

    public void imprimir() {
        System.out.println("\nTabla de símbolos:");
        System.out.println("NOMBRE | TOKEN | TIPO | VALOR | LONG");
        for (Simbolo s : tabla.values()) {
            System.out.println(s);
        }
    }

    public void exportarArchivo() {
        String nombreArchivo = "ts.txt";
        try (PrintWriter writer = new PrintWriter(new FileWriter(nombreArchivo))) {
            writer.println("NOMBRE | TOKEN | TIPO | VALOR | LONG");
            for (Simbolo s : tabla.values()) {
                writer.println(s.toString());
            }
            System.out.println("Tabla de símbolos exportada a " + nombreArchivo);
        } catch (Exception e) {
            System.err.println("Error exportando tabla: " + e.getMessage());
        }
    }

}
