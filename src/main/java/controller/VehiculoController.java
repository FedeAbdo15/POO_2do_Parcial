package controller;

import enums.TipoCombustible;
import enums.TipoEntidad;
import enums.TipoVehiculo;
import model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Singleton de la entidad maestra {@link Vehiculo}.
 * No depende de otros controladores (salvo el de auditoria).
 */
public class VehiculoController {

    private static VehiculoController instancia;

    private final List<Vehiculo> vehiculos;

    private VehiculoController() {
        this.vehiculos = new ArrayList<>();
    }

    public static VehiculoController getInstance() {
        if (instancia == null) {
            instancia = new VehiculoController();
        }
        return instancia;
    }

    /**
     * Registra un nuevo vehiculo (UC2). Valida que la patente no este repetida,
     * lo deja disponible y registra el cambio en la auditoria.
     */
    public Vehiculo registrarVehiculo(String patente, String marca, String modelo, int anio,
                                      int kilometrajeActual, double valorDiario, TipoVehiculo tipoVehiculo,
                                      TipoCombustible tipoCombustible, int kmIncluidosPorDia,
                                      double costoKmExcedente, String usuario) {
        if (patente == null || patente.isBlank()) {
            throw new IllegalArgumentException("La patente es obligatoria.");
        }
        if (buscarPorPatente(patente) != null) {
            throw new IllegalArgumentException("Ya existe un vehiculo con patente " + patente + ".");
        }
        Vehiculo vehiculo = new Vehiculo(patente, marca, modelo, anio, kilometrajeActual, valorDiario,
                tipoVehiculo, tipoCombustible, kmIncluidosPorDia, costoKmExcedente);
        vehiculo.cambiarEstado(enums.EstadoVehiculo.DISPONIBLE);
        vehiculos.add(vehiculo);
        HistorialController.getInstance().registrar(TipoEntidad.VEHICULO, patente, "-", "DISPONIBLE", usuario);
        return vehiculo;
    }

    /** Busca un vehiculo por patente (sin distinguir mayusculas). Devuelve null si no existe. */
    public Vehiculo buscarPorPatente(String patente) {
        for (Vehiculo vehiculo : vehiculos) {
            if (vehiculo.getPatente().equalsIgnoreCase(patente)) {
                return vehiculo;
            }
        }
        return null;
    }

    public List<Vehiculo> getVehiculos() {
        return new ArrayList<>(vehiculos);
    }
}
