package model;

import java.time.LocalDate;

public class AlquilerCorporativo extends Alquiler {

    public static final double DESCUENTO_POR_DEFECTO = 10.0;

    private double porcentajeDescuento;

    public AlquilerCorporativo(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                               LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        this(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial, DESCUENTO_POR_DEFECTO);
    }

    public AlquilerCorporativo(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                               LocalDate fechaDevolucionEstimada, int kilometrajeInicial,
                               double porcentajeDescuento) {
        super(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial);
        this.porcentajeDescuento = porcentajeDescuento;
    }

    @Override
    public double calcularImporteTotal() {
        double base = calcularImporteBase() * (1 - porcentajeDescuento / 100.0);
        return base + calcularKmExcedentes();
    }

    @Override
    public double getPorcentajeAplicado() {
        return -porcentajeDescuento;
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
}
