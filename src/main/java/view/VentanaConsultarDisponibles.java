package view;

import controller.AlquilerController;
import enums.TipoVehiculo;
import model.Vehiculo;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

public class VentanaConsultarDisponibles extends JFrame {

    private final JTextField txtFechaInicio = new JTextField(10);
    private final JTextField txtFechaFin = new JTextField(10);
    private final JComboBox<String> cboTipo =
            new JComboBox<>(new String[]{"TODOS", "AUTO", "CAMIONETA", "UTILITARIO", "MOTO"});

    private final DefaultTableModel modeloTabla = new DefaultTableModel(
            new Object[]{"Patente", "Marca", "Modelo", "Tipo", "Combustible", "$/dia"}, 0) {
        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }
    };

    public VentanaConsultarDisponibles() {
        super("Consultar Vehiculos Disponibles (UC5)");
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setSize(620, 380);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel filtros = new JPanel();
        filtros.add(new JLabel("Inicio (AAAA-MM-DD):"));
        filtros.add(txtFechaInicio);
        filtros.add(new JLabel("Fin (AAAA-MM-DD):"));
        filtros.add(txtFechaFin);
        filtros.add(new JLabel("Tipo:"));
        filtros.add(cboTipo);

        JButton btnConsultar = new JButton("Consultar");
        btnConsultar.addActionListener(e -> consultar());
        filtros.add(btnConsultar);

        add(filtros, BorderLayout.NORTH);
        add(new JScrollPane(new JTable(modeloTabla)), BorderLayout.CENTER);
    }

    private void consultar() {
        LocalDate fechaInicio;
        LocalDate fechaFin;
        try {
            fechaInicio = LocalDate.parse(txtFechaInicio.getText().trim());
            fechaFin = LocalDate.parse(txtFechaFin.getText().trim());
        } catch (DateTimeParseException ex) {
            JOptionPane.showMessageDialog(this, "Las fechas deben tener el formato AAAA-MM-DD.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (fechaFin.isBefore(fechaInicio)) {
            JOptionPane.showMessageDialog(this, "La fecha fin no puede ser anterior a la de inicio.",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String seleccion = (String) cboTipo.getSelectedItem();
        TipoVehiculo tipo = "TODOS".equals(seleccion) ? null : TipoVehiculo.valueOf(seleccion);

        List<Vehiculo> disponibles = AlquilerController.getInstance()
                .consultarVehiculosDisponibles(fechaInicio, fechaFin, tipo);

        modeloTabla.setRowCount(0);
        for (Vehiculo v : disponibles) {
            modeloTabla.addRow(new Object[]{
                    v.getPatente(), v.getMarca(), v.getModelo(),
                    v.getTipoVehiculo(), v.getTipoCombustible(),
                    String.format("%.2f", v.getValorDiario())
            });
        }

        if (disponibles.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No hay vehiculos disponibles para ese periodo y tipo.",
                    "Sin resultados", JOptionPane.INFORMATION_MESSAGE);
        }
    }
}
