package lexicoparte1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;
import java_cup.runtime.Symbol;

public class IDECompilador extends JFrame {

    private JTextArea codigoTextArea;
    private JTextArea resultadoTextArea;
    private Lexico lexer; // Variable global del lexer

    public IDECompilador() {
        setTitle("IDE Analizador Léxico");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(950, 650);
        setLocationRelativeTo(null);

        // Panel de código
        JLabel codigoLabel = new JLabel("Código Fuente:");
        codigoLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 0));
        codigoTextArea = new JTextArea(15, 50);
        codigoTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 14));
        JScrollPane codigoScrollPane = new JScrollPane(codigoTextArea);

        // Panel de resultados
        JLabel resultadoLabel = new JLabel("Tokens Reconocidos:");
        resultadoLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
        resultadoTextArea = new JTextArea(10, 50);
        resultadoTextArea.setEditable(false);
        resultadoTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane resultadoScrollPane = new JScrollPane(resultadoTextArea);

        // Botones
        var buttonPanel = getJPanel();

        // Layout
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(codigoLabel, BorderLayout.NORTH);
        topPanel.add(codigoScrollPane, BorderLayout.CENTER);
        topPanel.add(buttonPanel, BorderLayout.SOUTH);

        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(resultadoLabel, BorderLayout.NORTH);
        bottomPanel.add(resultadoScrollPane, BorderLayout.CENTER);

        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, topPanel, bottomPanel);
        splitPane.setDividerLocation(380);
        add(splitPane);

        setVisible(true);
    }

    private JPanel getJPanel() {
        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton cargarBtn = new JButton("Cargar Archivo");
        JButton analizarBtn = new JButton("Analizar Léxico");
        JButton limpiarBtn = new JButton("Limpiar");
        JButton exportTabla = new JButton("Exportar Tabla");

        cargarBtn.addActionListener(e -> cargarArchivo());
        analizarBtn.addActionListener(e -> analizarLexico());
        limpiarBtn.addActionListener(e -> {
            codigoTextArea.setText("");
            resultadoTextArea.setText("");
            lexer = null; // Limpiar lexer
        });
        exportTabla.addActionListener(e -> {
            if (lexer != null) {
                lexer.tablaSimbolos.exportarArchivo();
                JOptionPane.showMessageDialog(this, "Tabla de símbolos exportada como ts.txt", "Éxito", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Primero analice el código para generar la tabla de símbolos.", "Error", JOptionPane.WARNING_MESSAGE);
            }
        });

        buttonPanel.add(cargarBtn);
        buttonPanel.add(analizarBtn);
        buttonPanel.add(limpiarBtn);
        buttonPanel.add(exportTabla);
        return buttonPanel;
    }

    private void cargarArchivo() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setFileFilter(new FileNameExtensionFilter("Archivos de código", "txt", "p", "prog", "code"));
        if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            try (BufferedReader reader = new BufferedReader(new FileReader(fileChooser.getSelectedFile()))) {
                codigoTextArea.read(reader, null);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this, "Error al cargar el archivo:\n" + ex.getMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void analizarLexico() {
        String codigo = codigoTextArea.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese código para analizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Redirigir System.out y System.err
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(salidaCapturada);
        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;

        System.setOut(printStream);
        System.setErr(printStream);

        String mensajeError = null;

        try (StringReader reader = new StringReader(codigo)) {
            lexer = new Lexico(reader); // Instanciar lexer global
            Symbol token;
            do {
                token = lexer.next_token(); // Recorrer todos los tokens
            } while (token.sym != sym.EOF);
        } catch (Throwable t) {
            mensajeError = t.getMessage();
            if (mensajeError == null || mensajeError.trim().isEmpty()) {
                mensajeError = "Error léxico: " + t.getClass().getSimpleName();
            }
        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }

        // Mostrar resultados
        resultadoTextArea.setText("");
        if (mensajeError != null) {
            resultadoTextArea.append("[!] ERROR LÉXICO:\n" + mensajeError);
        } else {
            String salida = salidaCapturada.toString().trim();
            if (!salida.isEmpty()) {
                resultadoTextArea.append(salida);
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IDECompilador::new);
    }
}
