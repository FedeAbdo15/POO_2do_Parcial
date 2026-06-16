package model;

import java.time.LocalDate;

public class AlquilerTuristico extends Alquiler {

    public static final double RECARGO_POR_DEFECTO = 15.0;

    private double porcentajeRecargo;

    public AlquilerTuristico(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                             LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        this(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial, RECARGO_POR_DEFECTO);
    }

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
        return porcentajeRecargo;
    }

    public double getPorcentajeRecargo() {
        return porcentajeRecargo;
    }

    public void setPorcentajeRecargo(double porcentajeRecargo) {
        this.porcentajeRecargo = porcentajeRecargo;
    }
}
