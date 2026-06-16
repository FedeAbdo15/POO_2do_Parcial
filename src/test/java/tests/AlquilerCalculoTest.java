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

class AlquilerCalculoTest {

    private Cliente clienteDemo() {
        return new Cliente("11111111", "Cliente Test", "0", "t@t.com", "-");
    }

    private Vehiculo vehiculoSinExcedente(double valorDiario) {
        return new Vehiculo("AA000AA", "Marca", "Modelo", 2022, 0, valorDiario,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 1000, 50);
    }

    @Test
    void corporativoAplicaDescuentoDel10PorCiento() {
        Vehiculo vehiculo = vehiculoSinExcedente(10000);

        AlquilerCorporativo alquiler = new AlquilerCorporativo(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0, 10.0);

        assertEquals(18000.0, alquiler.calcularImporteTotal(), 0.001);
        assertEquals(-10.0, alquiler.getPorcentajeAplicado(), 0.001);
    }

    @Test
    void turisticoAplicaRecargoDel15PorCiento() {
        Vehiculo vehiculo = vehiculoSinExcedente(10000);

        AlquilerTuristico alquiler = new AlquilerTuristico(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0, 15.0);

        assertEquals(23000.0, alquiler.calcularImporteTotal(), 0.001);
        assertEquals(15.0, alquiler.getPorcentajeAplicado(), 0.001);
    }

    @Test
    void saldoPendienteRestaSeniaYSumaKmExcedentes() {

        Vehiculo vehiculo = new Vehiculo("BB111BB", "Marca", "Modelo", 2022, 0, 10000,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 100, 50);
        AlquilerComun alquiler = new AlquilerComun(clienteDemo(), vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 12), 0);

        alquiler.setKilometrajeFinal(300);
        assertEquals(25000.0, alquiler.calcularImporteTotal(), 0.001);

        Pago sena = new Pago(8000, MedioPago.EFECTIVO, "test", TipoPago.SENIA);
        sena.confirmar();
        alquiler.agregarPago(sena);

        assertEquals(8000.0, alquiler.calcularSeniaAbonada(), 0.001);
        assertEquals(17000.0, alquiler.calcularSaldoPendiente(), 0.001);
    }
}
