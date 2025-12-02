package lexicoparte1;


public class Simbolo {
    public String nombre;
    public String token;
    public String tipo;
    public Object valor;     // usado solo para constantes
    public int longitud;     // usado solo para strings

    public Simbolo(String nombre, String token, String tipo, Object valor, int longitud) {
        this.nombre = nombre;
        this.token = token;
        this.tipo = tipo;
        this.valor = valor;
        this.longitud = longitud;
    }

    @Override
    public String toString() {
        return nombre + " | " + token + " | " + tipo + " | " + valor + " | " + longitud;
    }
}
