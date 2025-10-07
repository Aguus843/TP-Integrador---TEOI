package lexicoparte1;

import java_cup.runtime.*;
import java.util.*;
import java.io.*;
import lexicoparte1.TablaSimbolos;



%%
%cup
%public
%class Lexico
%line
%column
%char
%{
   // Instancia global
   TablaSimbolos tablaSimbolos = new TablaSimbolos();
%}

// Definiciones de patrones
LETRA       = [a-zA-Z]
DIGITO      = [0-9]
ESPACIO     = [ \t\f\n\r]+
ID          = [A-Z]({LETRA}|{DIGITO}|_)*
HEX         = 0[hH][0-9a-fA-F]+
C_INT       = {DIGITO}+
C_REAL      = ({DIGITO}+\.{DIGITO}*)|(\.{DIGITO}+)
C_STRING    = \"[^\"\n]{0,30}\"
COMENTARIO  = \#([^#]*)\#

%%

// Reglas léxicas
<YYINITIAL> {

    // PALABRAS RESERVADAS
    "REPEAT"       { System.out.println("Token REPEAT encontrado, Lexema " + yytext()); }
    "IF"           { System.out.println("Token IF encontrado, Lexema " + yytext()); }
    "THEN"         { System.out.println("Token THEN encontrado, Lexema " + yytext()); }
    "ELSE"         { System.out.println("Token ELSE encontrado, Lexema " + yytext()); }
    "MOSTRAR"      { System.out.println("Token MOSTRAR encontrado, Lexema " + yytext()); }
    "DECVAR"       { System.out.println("Token DECVAR encontrado, Lexema " + yytext()); }
    "ENDDECVAR"    { System.out.println("Token ENDDECVAR encontrado, Lexema " + yytext()); }
    "IP"           { System.out.println("Token PROGRAM.SECTION encontrado, Lexema " + yytext()); }
    "FP"           { System.out.println("Token ENDPROGRAM.SECTION encontrado, Lexema " + yytext()); }
    "INLIST"       { System.out.println("Token INLIST encontrado, Lexema " + yytext()); }
    "END"          { System.out.println("Token END encontrado, Lexema " + yytext()); }
    "INT"          { System.out.println("Token TIPO_INT encontrado, Lexema " + yytext()); }
    "REAL"         { System.out.println("Token TIPO_REAL encontrado, Lexema " + yytext()); }
    "STRING"       { System.out.println("Token TIPO_STRING encontrado, Lexema " + yytext()); }

    // CONSTANTES NUMÉRICAS Y DE CADENA
    {C_INT} {
       int valor = Integer.parseInt(yytext());
       if (valor < 0 || valor > 32767) {
          throw new Error("Error: constante entera fuera de rango (0-32767): " + yytext() + " en la línea " + yyline);
       }
       tablaSimbolos.agregar(yytext(), "C_INT", valor, 0);
       System.out.println("Token C_INT encontrado, Lexema "+ yytext());
    }

    // CONSTANTES REALES
    {C_REAL} {
       try {
          float valor = Float.parseFloat(yytext());
       } catch (NumberFormatException e) {
           throw new Error("Error: constante real inválida: " + yytext() + " en la línea " + yyline);
       }
       float valor = Float.parseFloat(yytext());
       tablaSimbolos.agregar(yytext(), "C_REAL", valor, 0);
       System.out.println("Token C_REAL encontrado, Lexema "+ valor);
    }

    // CONSTANTES STRING
    {C_STRING} {
       String contenido = yytext().substring(1, yytext().length() - 1); // quitar comillas
       if (contenido.length() > 30) {
          throw new Error("Error: cadena demasiado larga (máx 30 caracteres): " + yytext() + " en la línea " + yyline);
       }
       tablaSimbolos.agregar("_" + contenido, "C_STRING", contenido, contenido.length());
       System.out.println("Token C_STRING encontrado, Lexema "+ yytext());
    }

    // HEXADECIMALES
    {HEX} { System.out.println("Token HEX encontrado, Lexema " + yytext()); }

    // OPERADORES
    "+"   { System.out.println("Token SUMA encontrado, Lexema " + yytext()); }
    "-"   { System.out.println("Token RESTA encontrado, Lexema " + yytext()); }
    "*"   { System.out.println("Token MULT encontrado, Lexema " + yytext()); }
    "/"   { System.out.println("Token DIV encontrado, Lexema " + yytext()); }
    "="   { System.out.println("Token ASIGNAR encontrado, Lexema " + yytext()); }
    "("   { System.out.println("Token APARENT encontrado, Lexema " + yytext()); }
    ")"   { System.out.println("Token CPARENT encontrado, Lexema " + yytext()); }
    ";"   { System.out.println("Token PYC encontrado, Lexema " + yytext()); }
    ","   { System.out.println("Token COMA encontrado, Lexema " + yytext()); }
    "<="  { System.out.println("Token MENORIGUAL encontrado, Lexema " + yytext()); }
    ">="  { System.out.println("Token MAYORIGUAL encontrado, Lexema " + yytext()); }
    "<"   { System.out.println("Token MENOR encontrado, Lexema " + yytext()); }
    ">"   { System.out.println("Token MAYOR encontrado, Lexema " + yytext()); }
    "=="  { System.out.println("Token IGUAL encontrado, Lexema " + yytext()); }
    "!="  { System.out.println("Token DISTINTO encontrado, Lexema " + yytext()); }
    "&&"  { System.out.println("Token AND encontrado, Lexema " + yytext()); }
    "||"  { System.out.println("Token OR encontrado, Lexema " + yytext()); }
    ":"   { System.out.println("Token DOSPUNTOS encontrado, Lexema " + yytext()); }
    "["   { System.out.println("Token ACORCHETE encontrado, Lexema " + yytext()); }
    "]"   { System.out.println("Token CCORCHETE encontrado, Lexema " + yytext()); }

    // IDENTIFICADORES
    {ID} {
        if (tablaSimbolos.buscar(yytext()) == null) {
            tablaSimbolos.agregar(yytext(), "ID", null, 0);
        }
        System.out.println("Token ID encontrado, Lexema "+ yytext());
    }

    // ESPACIOS Y COMENTARIOS
    {ESPACIO}    { /* Ignorar espacios */ }
    {COMENTARIO} { /* Ignorar comentarios */ }
}

// CARACTERES NO PERMITIDOS
[^] {
    throw new Error("Caracter no permitido: <" + yytext() + "> en la línea " + yyline);
}
