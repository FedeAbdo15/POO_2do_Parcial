package model;

import java.time.LocalDate;

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
