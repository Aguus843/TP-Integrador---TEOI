package lexicoparte1;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.io.*;

public class IDECompilador extends JFrame {

    private JTextArea codigoTextArea;
    private JTextArea resultadoTextArea;

    // Guardamos el parser para poder exportar la tabla después
    private parser ultimoParser = null;

    public IDECompilador() {
        setTitle("IDE Analizador Sintáctico - Final");
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
        JLabel resultadoLabel = new JLabel("Salida del Parser:");
        resultadoLabel.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
        resultadoTextArea = new JTextArea(10, 50);
        resultadoTextArea.setEditable(false);
        resultadoTextArea.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        JScrollPane resultadoScrollPane = new JScrollPane(resultadoTextArea);

        // Botones:
        JPanel buttonPanel = getJPanel();

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
        JButton analizarBtn = new JButton("Analizar Sintáctico");
        JButton limpiarBtn = new JButton("Limpiar");
        JButton exportTabla = new JButton("Exportar Tabla");

        cargarBtn.addActionListener(e -> cargarArchivo());
        analizarBtn.addActionListener(e -> analizarSintactico());
        limpiarBtn.addActionListener(e -> {
            codigoTextArea.setText("");
            resultadoTextArea.setText("");
            ultimoParser = null;
        });

        exportTabla.addActionListener(e -> exportarTabla());

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

    private void analizarSintactico() {

        String codigo = codigoTextArea.getText();
        if (codigo.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Ingrese código para analizar.", "Advertencia", JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Capturar System.out y System.err
        ByteArrayOutputStream salidaCapturada = new ByteArrayOutputStream();
        PrintStream printStream = new PrintStream(salidaCapturada);
        PrintStream outOriginal = System.out;
        PrintStream errOriginal = System.err;

        System.setOut(printStream);
        System.setErr(printStream);

        String error = null;

        try (StringReader reader = new StringReader(codigo)) {

            Lexico lexer = new Lexico(reader);
            ultimoParser = new parser(lexer);     // Guardamos el parser

            ultimoParser.parse();                 // SOLO parsea, NO imprime tabla

        } catch (Throwable t) {
            error = t.getMessage();
            if (error == null || error.trim().isEmpty()) {
                error = "Error sintáctico: " + t.getClass().getSimpleName();
            }
        } finally {
            System.setOut(outOriginal);
            System.setErr(errOriginal);
        }

        resultadoTextArea.setText("");

        if (error != null) {
            resultadoTextArea.append("[!] ERROR SINTÁCTICO:\n" + error);
        } else {
            String salida = salidaCapturada.toString().trim();
            if (!salida.isEmpty()) {
                resultadoTextArea.append(salida);
            }
            resultadoTextArea.append("\n\n[OK] Análisis sintáctico completado.");
        }
    }

    private void exportarTabla() {
        if (ultimoParser == null) {
            JOptionPane.showMessageDialog(this,
                    "Primero ejecute el análisis sintáctico.",
                    "Tabla no disponible",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Mostrar tabla por pantalla
        ultimoParser.TS.imprimir();

        // Exportar archivo
        ultimoParser.TS.exportarArchivo();

        JOptionPane.showMessageDialog(this,
                "Tabla de símbolos exportada correctamente.",
                "Éxito",
                JOptionPane.INFORMATION_MESSAGE);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(IDECompilador::new);
    }
}
