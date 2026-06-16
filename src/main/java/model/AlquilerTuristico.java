package model;

import java.time.LocalDate;

/**
 * Alquiler turistico: aplica un <b>recargo</b> sobre el importe base.
 * El porcentaje es parametrizable (15% por defecto), pudiendo provenir de una
 * condicion particular negociada con el cliente.
 */
public class AlquilerTuristico extends Alquiler {

    public static final double RECARGO_POR_DEFECTO = 15.0;

    private double porcentajeRecargo;

    /** Crea el alquiler con el recargo por defecto. */
    public AlquilerTuristico(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                             LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        this(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial, RECARGO_POR_DEFECTO);
    }

    /** Crea el alquiler con un recargo especifico (parametrizable). */
    public AlquilerTuristico(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                             LocalDate fechaDevolucionEstimada, int kilometrajeInicial,
                             double porcentajeRecargo) {
        super(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial);
        this.porcentajeRecargo = porcentajeRecargo;
    }

    @Override
    public double calcularImporteTotal() {
        double base = calcularImporteBase() * (1 + porcentajeRecargo / 100.0);
        return base + calcularKmExcedentes();
    }

    @Override
    public double getPorcentajeAplicado() {
        return porcentajeRecargo; // positivo: es un recargo
    }

    public double getPorcentajeRecargo() {
        return porcentajeRecargo;
    }

    public void setPorcentajeRecargo(double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }
}
