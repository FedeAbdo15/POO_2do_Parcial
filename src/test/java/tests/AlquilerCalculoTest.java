package tests;

import enums.MedioPago;
import enums.TipoCombustible;
import enums.TipoPago;
import enums.TipoVehiculo;
import model.AlquilerComun;
import model.AlquilerCorporativo;
import model.AlquilerTuristico;
import model.Cliente;
import model.Pago;
import model.Vehiculo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Pruebas de los calculos de importe (polimorficos) y del saldo pendiente.
 * No dependen de la GUI ni de los controladores Singleton.
 */
class AlquilerCalculoTest {

    private Cliente clienteDemo() {
        return new Cliente("11111111", "Cliente Test", "0", "t@t.com", "-");
    }

    /** Vehiculo con kilometraje incluido alto: nunca genera excedente en estas pruebas. */
    private Vehiculo vehiculoSinExcedente(double valorDiario) {
        return new Vehiculo("AA000AA", "Marca", "Modelo", 2022, 0, valorDiario,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 1000, 50);
    }

    @Test
    void corporativoAplicaDescuentoDel10PorCiento() {
        Vehiculo vehiculo = vehiculoSinExcedente(10000);
        // 2 dias * 10000 = 20000 base; descuento 10% => 18000
        AlquilerCorporativo alquiler = new AlquilerCorporativo(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0, 10.0);

        assertEquals(18000.0, alquiler.calcularImporteTotal(), 0.001);
        assertEquals(-10.0, alquiler.getPorcentajeAplicado(), 0.001);
    }

    @Test
    void turisticoAplicaRecargoDel15PorCiento() {
        Vehiculo vehiculo = vehiculoSinExcedente(10000);
        // 2 dias * 10000 = 20000 base; recargo 15% => 23000
        AlquilerTuristico alquiler = new AlquilerTuristico(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0, 15.0);

        assertEquals(23000.0, alquiler.calcularImporteTotal(), 0.001);
        assertEquals(15.0, alquiler.getPorcentajeAplicado(), 0.001);
    }

    @Test
    void saldoPendienteRestaSeniaYSumaKmExcedentes() {
        // 100 km incluidos por dia, $50 por km excedente.
        Vehiculo vehiculo = new Vehiculo("BB111BB", "Marca", "Modelo", 2022, 0, 10000,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 100, 50);
        AlquilerComun alquiler = new AlquilerComun(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0);

        // 2 dias -> base 20000. Recorre 300 km, incluidos 200 -> 100 km excedentes * 50 = 5000.
        alquiler.setKilometrajeFinal(300);
        assertEquals(25000.0, alquiler.calcularImporteTotal(), 0.001);

        // Sena confirmada de 8000.
        Pago sena = new Pago(8000, MedioPago.EFECTIVO, "test", TipoPago.SENIA);
        sena.confirmar();
        alquiler.agregarPago(sena);

        assertEquals(8000.0, alquiler.calcularSeniaAbonada(), 0.001);
        assertEquals(17000.0, alquiler.calcularSaldoPendiente(), 0.001); // 25000 - 8000
    }
}
