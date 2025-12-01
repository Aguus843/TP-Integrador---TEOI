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
   TablaSimbolos tablaSimbolos = new TablaSimbolos();
%}

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

<YYINITIAL> {

    // PALABRAS RESERVADAS
    "WHILE"        { return new Symbol(sym.WHILE,yytext()); }
    "REPEAT"       { return new Symbol(sym.REPEAT,yytext()); }
    "IF"           { return new Symbol(sym.IF,yytext()); }
    "THEN"         { return new Symbol(sym.THEN,yytext()); }
    "ELSE"         { return new Symbol(sym.ELSE,yytext()); }
    "MOSTRAR"      { return new Symbol(sym.MOSTRAR,yytext()); }
    "DECVAR"       { return new Symbol(sym.DECVAR,yytext()); }
    "ENDDECVAR"    { return new Symbol(sym.ENDDECVAR,yytext()); }
    "IP"           { return new Symbol(sym.IP,yytext()); }
    "FP"           { return new Symbol(sym.FP,yytext()); }
    "INLIST"       { return new Symbol(sym.INLIST,yytext()); }
    "END"          { return new Symbol(sym.END,yytext()); }
    "INT"          { return new Symbol(sym.INT,yytext()); }
    "REAL"         { return new Symbol(sym.REAL,yytext()); }
    "STRING"       { return new Symbol(sym.STRING,yytext()); }

    // CONSTANTES NUMÉRICAS Y DE CADENA
    {C_INT} {
       int valor = Integer.parseInt(yytext());
       if (valor < 0 || valor > 32767) {
          throw new Error("Error: constante entera fuera de rango (0-32767): " + yytext() + " en la línea " + yyline);
       }
       tablaSimbolos.agregar(yytext(), "C_INT", valor, 0);
       return new Symbol(sym.C_INT, yytext());
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
       return new Symbol(sym.C_REAL, yytext());
    }

    // CONSTANTES STRING
    {C_STRING} {
       String contenido = yytext().substring(1, yytext().length() - 1); // quitar comillas
       if (contenido.length() > 30) {
          throw new Error("Error: cadena demasiado larga (máx 30 caracteres): " + yytext() + " en la línea " + yyline);
       }
       tablaSimbolos.agregar(contenido, "C_STRING", "_" + contenido, contenido.length());
       return new Symbol(sym.C_STRING, yytext());
    }

    // HEXADECIMALES
    {HEX} { return new Symbol(sym.HEX, yytext()); }

    // OPERADORES
    "+"   { return new Symbol(sym.SUMA, yytext()); }
    "-"   { return new Symbol(sym.MENOS, yytext()); }
    "*"   { return new Symbol(sym.MULT, yytext()); }
    "/"   { return new Symbol(sym.DIV, yytext()); }
    "="   { return new Symbol(sym.ASIGNAR, yytext()); }
    "("   { return new Symbol(sym.APARENT, yytext()); }
    ")"   { return new Symbol(sym.CPARENT, yytext()); }
    ";"   { return new Symbol(sym.PYC, yytext()); }
    ","   { return new Symbol(sym.COMA, yytext()); }
    "<="  { return new Symbol(sym.MENORIGUAL, yytext()); }
    ">="  { return new Symbol(sym.MAYORIGUAL, yytext()); }
    "<"   { return new Symbol(sym.MENOR, yytext()); }
    ">"   { return new Symbol(sym.MAYOR, yytext()); }
    "=="  { return new Symbol(sym.IGUAL, yytext()); }
    "!="  { return new Symbol(sym.DISTINTO, yytext()); }
    "&&"  { return new Symbol(sym.AND, yytext()); }
    "||"  { return new Symbol(sym.OR, yytext()); }
    ":"   { return new Symbol(sym.DOSPUNTOS, yytext()); }
    "["   { return new Symbol(sym.ACORCHETE, yytext()); }
    "]"   { return new Symbol(sym.CCORCHETE, yytext()); }

    // IDENTIFICADORES
    {ID} {
        if (tablaSimbolos.buscar(yytext()) == null) {
            tablaSimbolos.agregar(yytext(), "ID", null, 0);
        }
        return new Symbol(sym.ID, yytext());
    }

    // ESPACIOS Y COMENTARIOS
    {ESPACIO}    { /* Ignorar espacios */ }
    {COMENTARIO} { /* Ignorar comentarios */ }
}

// CARACTERES NO PERMITIDOS
[^] {
    throw new Error("Caracter no permitido: <" + yytext() + "> en la línea " + yyline);
}
