package tests;

import enums.EstadoAlquiler;
import enums.EstadoVehiculo;
import enums.TipoCombustible;
import enums.TipoVehiculo;
import model.Alquiler;
import model.AlquilerComun;
import model.Cliente;
import model.Vehiculo;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class VehiculoDisponibilidadTest {

    @Test
    void detectaSuperposicionDeFechas() {
        Cliente cliente = new Cliente("44444444", "Cliente Test", "0", "t@t.com", "-");
        Vehiculo vehiculo = new Vehiculo("DD333DD", "Marca", "Modelo", 2022, 0, 10000,
                TipoVehiculo.AUTO, TipoCombustible.NAFTA, 200, 50);

        Alquiler reserva = new AlquilerComun(cliente, vehiculo,
                LocalDate.of(2026, 1, 10), LocalDate.of(2026, 1, 15), 0);
        reserva.cambiarEstado(EstadoAlquiler.CONFIRMADO);
        List<Alquiler> alquileres = new ArrayList<>();
        alquileres.add(reserva);

        assertFalse(vehiculo.estaDisponible(LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 14), alquileres));

        assertTrue(vehiculo.estaDisponible(LocalDate.of(2026, 1, 16), LocalDate.of(2026, 1, 18), alquileres));

        reserva.cambiarEstado(EstadoAlquiler.CANCELADO);
        assertTrue(vehiculo.estaDisponible(LocalDate.of(2026, 1, 12), LocalDate.of(2026, 1, 14), alquileres));

        reserva.cambiarEstado(EstadoAlquiler.CONFIRMADO);
        vehiculo.cambiarEstado(EstadoVehiculo.MANTENIMIENTO);
        assertFalse(vehiculo.estaDisponible(LocalDate.of(2026, 5, 1), LocalDate.of(2026, 5, 2), new ArrayList<>()));
    }
}
