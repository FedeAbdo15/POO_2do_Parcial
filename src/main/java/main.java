import controller.AlquilerController;
import controller.ClienteController;
import controller.VehiculoController;
import enums.TipoCombustible;
import enums.TipoVehiculo;
import view.MenuPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.time.LocalDate;

/**
 * Punto de entrada de la aplicacion. Carga datos de demostracion a traves de
 * los controladores Singleton y abre el menu principal con las dos vistas Swing.
 */
public class Main {

    public static void main(String[] args) {
        cargarDatosDemo();

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
            // Si falla, se usa el look and feel por defecto.
        }

        SwingUtilities.invokeLater(() -> new MenuPrincipal().setVisible(true));
    }

    /** Precarga clientes, vehiculos y un alquiler de ejemplo. */
    private static void cargarDatosDemo() {
        ClienteController clientes = ClienteController.getInstance();
        VehiculoController vehiculos = VehiculoController.getInstance();
        AlquilerController alquileres = AlquilerController.getInstance();

        clientes.registrarCliente("20304050607", "Juan Perez", "1133334444",
                "juan@mail.com", "Av. Siempre Viva 123", "admin");
        clientes.registrarCliente("30712345678", "Transportes SRL", "1144445555",
                "contacto@transportes.com", "Calle Falsa 456", "admin");

        vehiculos.registrarVehiculo("AA123BB", "Toyota", "Corolla", 2022, 35000, 25000,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 200, 80, "admin");
        vehiculos.registrarVehiculo("AC456DD", "Ford", "Ranger", 2021, 60000, 40000,
                TipoVehiculo.CAMIONETA, TipoCombustible.DIESEL, 250, 100, "admin");
        vehiculos.registrarVehiculo("AD789EE", "Renault", "Kangoo", 2020, 80000, 30000,
                TipoVehiculo.UTILITARIO, TipoCombustible.NAFTA, 0, 0, "admin");
        vehiculos.registrarVehiculo("AE012FF", "Honda", "CB 190", 2023, 12000, 15000,
                TipoVehiculo.MOTO, TipoCombustible.NAFTA, 150, 60, "admin");

        // Alquiler de ejemplo: deja el auto AA123BB reservado del 1 al 5 de julio de 2026.
        alquileres.solicitarAlquiler("20304050607", "AA123BB",
                LocalDate.of(2026, 7, 1), LocalDate.of(2026, 7, 5), "COMUN", "admin");
    }
}
