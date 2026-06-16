package view;

import controller.AlquilerController;
import model.Alquiler;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/**
 * Vista Swing del caso de uso UC3 - Solicitar Alquiler.
 *
 * <p>La vista solo valida el formato de la entrada y delega toda la logica de
 * negocio en {@link AlquilerController}. No contiene reglas de negocio.</p>
 */
public class VentanaSolicitarAlquiler extends JFrame {

    private final JTextField txtDniCuit = new JTextField();
    private final JTextField txtPatente = new JTextField();
    private final JTextField txtFechaInicio = new JTextField();
    private final JTextField txtFechaDevolucion = new JTextField();
    private final JComboBox<String> cboTipo = new JComboBox<>(new String[]{"COMUN", "CORPORATIVO", "TURISTICO"});
    private final JTextField txtUsuario = new JTextField("admin");

    public VentanaSolicitarAlquiler() {
        super("Solicitar Alquiler (UC3)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(420, 320);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel formulario = new JPanel(new GridLayout(6, 2, 8, 8));
        formulario.add(new JLabel("DNI/CUIT del cliente:"));
        formulario.add(txtDniCuit);
        formulario.add(new JLabel("Patente del vehiculo:"));
        formulario.add(txtPatente);
        formulario.add(new JLabel("Fecha inicio (AAAA-MM-DD):"));
        formulario.add(txtFechaInicio);
        formulario.add(new JLabel("Fecha devolucion (AAAA-MM-DD):"));
        formulario.add(txtFechaDevolucion);
        formulario.add(new JLabel("Tipo de alquiler:"));
        formulario.add(cboTipo);
        formulario.add(new JLabel("Usuario:"));
        formulario.add(txtUsuario);

        JButton btnSolicitar = new JButton("Solicitar");
        btnSolicitar.addActionListener(e -> solicitar());

        JPanel panelBoton = new JPanel();
        panelBoton.add(btnSolicitar);

        add(new JLabel("  Complete los datos del alquiler"), BorderLayout.NORTH);
        add(formulario, BorderLayout.CENTER);
        add(panelBoton, BorderLayout.SOUTH);
    }

    private void solicitar() {
        // Validaciones de entrada (responsabilidad de la vista).
        String dni = txtDniCuit.getText().trim();
        String patente = txtPatente.getText().trim();
        String usuario = txtUsuario.getText().trim();
        if (dni.isEmpty() || patente.isEmpty() || usuario.isEmpty()) {
            mostrarError("Complete DNI/CUIT, patente y usuario.");
            return;
        }

        LocalDate fechaInicio;
        LocalDate fechaDevolucion;
        try {
            fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            fechaDevolucion = LocalDate.parse(txtFechaDevolucion.getText().trim());
        } catch (DateTimeParseException ex) {
            mostrarError("Las fechas deben tener el formato AAAA-MM-DD.");
            return;
        }
        if (fechaDevolucion.isBefore(fechaInicio)) {
            mostrarError("La fecha de devolucion no puede ser anterior a la de inicio.");
            return;
        }

        String tipo = (String) cboTipo.getSelectedItem();

        // Delegacion al controlador (logica de negocio).
        try {
            Alquiler alquiler = AlquilerController.getInstance()
                    .solicitarAlquiler(dni, patente, fechaInicio, fechaDevolucion, tipo, usuario);
            JOptionPane.showMessageDialog(this,
                    "Alquiler ingresado con exito.\n"
                            + "Id: " + alquiler.getId() + "\n"
                            + "Estado: " + alquiler.getEstado() + "\n"
                            + "Dias: " + alquiler.calcularDias() + "\n"
                            + "Importe base estimado: $" + String.format("%.2f", alquiler.calcularImporteBase()),
                    "Operacion exitosa", JOptionPane.INFORMATION_MESSAGE);
        } catch (RuntimeException ex) {
            mostrarError(ex.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}
