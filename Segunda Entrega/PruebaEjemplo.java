package lexicoparte1;

import java.io.FileNotFoundException;
import java.io.FileReader;

public class PruebaEjemplo {
    public static void main(String[] args) throws FileNotFoundException, Exception {
        FileReader f = new FileReader("prueba.txt");
        Lexico Lexer = new Lexico(f);
        parser sintactico = new parser(Lexer);
        sintactico.parse();
    }
}
