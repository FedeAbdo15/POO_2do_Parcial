package model;

import java.time.LocalDate;

/**
 * Alquiler corporativo: aplica un <b>descuento</b> sobre el importe base.
 * El porcentaje es parametrizable (10% por defecto), pudiendo provenir de una
 * condicion particular negociada con el cliente.
 */
public class AlquilerCorporativo extends Alquiler {

    public static final double DESCUENTO_POR_DEFECTO = 10.0;

    private double porcentajeDescuento;

    /** Crea el alquiler con el descuento por defecto. */
    public AlquilerCorporativo(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                               LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        this(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial, DESCUENTO_POR_DEFECTO);
    }

    /** Crea el alquiler con un descuento especifico (parametrizable). */
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
        return -porcentajeDescuento; // negativo: es un descuento
    }

    public double getPorcentajeDescuento() {
        return porcentajeDescuento;
    }

    public void setPorcentajeDescuento(double porcentajeDescuento) {
        this.porcentajeDescuento = porcentajeDescuento;
    }
}
