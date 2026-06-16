package model;

import java.time.LocalDate;

/**
 * Alquiler comun: se factura con el valor diario puro del vehiculo,
 * sin recargos ni descuentos. Se suman los kilometros excedentes, si los hay.
 */
public class AlquilerComun extends Alquiler {

    public AlquilerComun(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                         LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        super(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial);
    }

    @Override
    public double calcularImporteTotal() {
        return calcularImporteBase() + calcularKmExcedentes();
    }
}
