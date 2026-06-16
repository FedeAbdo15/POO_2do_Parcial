package tests;

import enums.EstadoAlquiler;
import enums.MedioPago;
import enums.TipoCombustible;
import enums.TipoPago;
import enums.TipoVehiculo;
import model.AlquilerComun;
import model.Cliente;
import model.Pago;
import model.Vehiculo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CancelacionTest {

    private AlquilerComun alquilerConfirmadoConSenia(Cliente cliente, double sena) {
        Vehiculo vehiculo = new Vehiculo("CC222CC", "Marca", "Modelo", 2022, 0, 10000,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 200, 50);
        AlquilerComun alquiler = new AlquilerComun(cliente, vehiculo,
                LocalDate.of(2026, 3, 10), LocalDate.of(2026, 3, 15), 0);
        Pago pago = new Pago(sena, MedioPago.TRANSFERENCIA, "test", TipoPago.SENIA);
        pago.confirmar();
        alquiler.agregarPago(pago);
        alquiler.cambiarEstado(EstadoAlquiler.CONFIRMADO);
        return alquiler;
    }

    @Test
    void cancelacionConMasDe48hsReintegraSeniaYConMenosNo() {

        Cliente cliente1 = new Cliente("22222222", "Cliente 1", "0", "c1@t.com", "-");
        AlquilerComun alquiler1 = alquilerConfirmadoConSenia(cliente1, 5000);
        LocalDateTime cancela72hsAntes = LocalDateTime.of(2026, 3, 7, 0, 0);

        assertTrue(alquiler1.puedeReintegrarSenia(cancela72hsAntes));
        if (alquiler1.puedeReintegrarSenia(cancela72hsAntes)) {
            cliente1.agregarCredito(alquiler1.calcularSeniaAbonada());
        }
        assertEquals(5000.0, cliente1.getCreditoAFavor(), 0.001);

        Cliente cliente2 = new Cliente("33333333", "Cliente 2", "0", "c2@t.com", "-");
        AlquilerComun alquiler2 = alquilerConfirmadoConSenia(cliente2, 5000);
        LocalDateTime cancela24hsAntes = LocalDateTime.of(2026, 3, 9, 0, 0);

        assertFalse(alquiler2.puedeReintegrarSenia(cancela24hsAntes));
        if (alquiler2.puedeReintegrarSenia(cancela24hsAntes)) {
            cliente2.agregarCredito(alquiler2.calcularSeniaAbonada());
        }
        assertEquals(0.0, cliente2.getCreditoAFavor(), 0.001);
    }
}
