package controller;

import enums.TipoCombustible;
import enums.TipoEntidad;
import enums.TipoVehiculo;
import model.Vehiculo;

import java.util.ArrayList;
import java.util.List;

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
