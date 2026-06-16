package controller;

import enums.EstadoAlquiler;
import enums.EstadoVehiculo;
import enums.MedioPago;
import enums.TipoEntidad;
import enums.TipoPago;
import enums.TipoVehiculo;
import model.Alquiler;
import model.AlquilerComun;
import model.AlquilerCorporativo;
import model.AlquilerTuristico;
import model.Cliente;
import model.Pago;
import model.Vehiculo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Singleton <b>orquestador</b> del negocio. Coordina los casos de
 * uso de alquiler (UC3 solicitar, UC4 finalizar, UC5 consultar disponibles) y
 * el ciclo de vida (confirmar con sena, iniciar, cancelar).
 *
 * <p>Depende de {@link ClienteController} y {@link VehiculoController} (via
 * {@code getInstance()}) y registra auditoria con {@link HistorialController}.
 * El grafo de dependencias es unidireccional: ningun controlador maestro
 * depende de este.</p>
 */
public class AlquilerController {

    private static AlquilerController instancia;

    private final List<Alquiler> alquileres;

    // Porcentajes generales parametrizables (pueden cambiarse en tiempo de ejecucion).
    private double porcentajeDescuentoCorporativo = AlquilerCorporativo.DESCUENTO_POR_DEFECTO;
    private double porcentajeRecargoTuristico = AlquilerTuristico.RECARGO_POR_DEFECTO;

    private AlquilerController() {
        this.alquileres = new ArrayList<>();
    }

    public static AlquilerController getInstance() {
        if (instancia == null) {
            instancia = new AlquilerController();
        }
        return instancia;
    }

    // ---------------------------------------------------------------------
    // UC3 - Solicitar alquiler
    // ---------------------------------------------------------------------

    /**
     * Solicita un alquiler (UC3). Verifica que existan cliente y vehiculo, que
     * el vehiculo este disponible en el periodo (sin superposicion), crea el
     * alquiler del tipo indicado y lo deja en estado INGRESADO.
     *
     * @param claseAlquiler "COMUN", "CORPORATIVO" o "TURISTICO"
     */
    public Alquiler solicitarAlquiler(String dniCuit, String patente, LocalDate fechaInicio,
                                      LocalDate fechaDevolucionEstimada, String claseAlquiler, String usuario) {
        Cliente cliente = ClienteController.getInstance().buscarPorDni(dniCuit);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con DNI/CUIT " + dniCuit + ".");
        }
        Vehiculo vehiculo = VehiculoController.getInstance().buscarPorPatente(patente);
        if (vehiculo == null) {
            throw new IllegalArgumentException("No existe un vehiculo con patente " + patente + ".");
        }
        if (!vehiculo.estaDisponible(fechaInicio, fechaDevolucionEstimada, alquileres)) {
            throw new IllegalStateException("El vehiculo no esta disponible en el periodo solicitado.");
        }

        Alquiler alquiler = crearAlquiler(claseAlquiler, cliente, vehiculo, fechaInicio, fechaDevolucionEstimada);
        alquiler.cambiarEstado(EstadoAlquiler.INGRESADO);
        alquileres.add(alquiler);
        cliente.agregarAlquiler(alquiler);
        HistorialController.getInstance().registrar(TipoEntidad.ALQUILER,
                String.valueOf(alquiler.getId()), "-", "INGRESADO", usuario);
        return alquiler;
    }

    /**
     * Crea el alquiler concreto segun la clase indicada (polimorfismo). Para los
     * tipos con porcentaje, usa la condicion particular vigente del cliente si
     * existe; en caso contrario, el porcentaje general parametrizable.
     */
    private Alquiler crearAlquiler(String claseAlquiler, Cliente cliente, Vehiculo vehiculo,
                                   LocalDate fechaInicio, LocalDate fechaDevolucionEstimada) {
        int kilometrajeInicial = vehiculo.getKilometrajeActual();
        String clase = (claseAlquiler == null) ? "" : claseAlquiler.trim().toUpperCase();
        switch (clase) {
            case "COMUN":
                return new AlquilerComun(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada, kilometrajeInicial);
            case "CORPORATIVO": {
                double porcentaje = cliente.tieneCondicionVigente(fechaInicio)
                        ? cliente.getPorcentajeEspecial() : porcentajeDescuentoCorporativo;
                return new AlquilerCorporativo(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada,
                        kilometrajeInicial, porcentaje);
            }
            case "TURISTICO": {
                double porcentaje = cliente.tieneCondicionVigente(fechaInicio)
                        ? cliente.getPorcentajeEspecial() : porcentajeRecargoTuristico;
                return new AlquilerTuristico(cliente, vehiculo, fechaInicio, fechaDevolucionEstimada,
                        kilometrajeInicial, porcentaje);
            }
            default:
                throw new IllegalArgumentException("Clase de alquiler invalida: " + claseAlquiler);
        }
    }

    // ---------------------------------------------------------------------
    // Ciclo de vida: confirmar con sena, iniciar, cancelar
    // ---------------------------------------------------------------------

    /** Registra la sena y confirma la reserva (INGRESADO -> CONFIRMADO). */
    public Pago confirmarAlquilerConSenia(int idAlquiler, double importe, MedioPago medioPago, String usuario) {
        Alquiler alquiler = obtenerAlquiler(idAlquiler);
        if (alquiler.getEstado() != EstadoAlquiler.INGRESADO) {
            throw new IllegalStateException("Solo se puede senar un alquiler en estado INGRESADO.");
        }
        Pago sena = new Pago(importe, medioPago, usuario, TipoPago.SENIA);
        sena.confirmar();
        alquiler.agregarPago(sena);
        String anterior = alquiler.getEstado().name();
        alquiler.cambiarEstado(EstadoAlquiler.CONFIRMADO);
        HistorialController.getInstance().registrar(TipoEntidad.ALQUILER,
                String.valueOf(alquiler.getId()), anterior, "CONFIRMADO", usuario);
        return sena;
    }

    /** Inicia el alquiler al llegar la fecha de inicio (CONFIRMADO -> EN_CURSO). */
    public void iniciarAlquiler(int idAlquiler, String usuario) {
        Alquiler alquiler = obtenerAlquiler(idAlquiler);
        if (alquiler.getEstado() != EstadoAlquiler.CONFIRMADO) {
            throw new IllegalStateException("Solo se puede iniciar un alquiler en estado CONFIRMADO.");
        }
        String anterior = alquiler.getEstado().name();
        alquiler.cambiarEstado(EstadoAlquiler.EN_CURSO);

        Vehiculo vehiculo = alquiler.getVehiculo();
        String estadoVehAnterior = vehiculo.getEstado().name();
        vehiculo.cambiarEstado(EstadoVehiculo.ALQUILADO);

        HistorialController historial = HistorialController.getInstance();
        historial.registrar(TipoEntidad.ALQUILER, String.valueOf(alquiler.getId()), anterior, "EN_CURSO", usuario);
        historial.registrar(TipoEntidad.VEHICULO, vehiculo.getPatente(), estadoVehAnterior, "ALQUILADO", usuario);
    }

    /**
     * Cancela un alquiler. Si estaba confirmado y se cancela con mas de 48 horas
     * de anticipacion, la sena queda como credito a favor del cliente; con menos
     * de 48 horas, la sena no se reintegra.
     */
    public void cancelarAlquiler(int idAlquiler, LocalDateTime fechaCancelacion, String usuario) {
        Alquiler alquiler = obtenerAlquiler(idAlquiler);
        if (alquiler.getEstado() == EstadoAlquiler.FINALIZADO
                || alquiler.getEstado() == EstadoAlquiler.CANCELADO) {
            throw new IllegalStateException("No se puede cancelar un alquiler en estado " + alquiler.getEstado() + ".");
        }

        if (alquiler.puedeReintegrarSenia(fechaCancelacion)) {
            alquiler.getCliente().agregarCredito(alquiler.calcularSeniaAbonada());
        }

        String anterior = alquiler.getEstado().name();
        alquiler.cambiarEstado(EstadoAlquiler.CANCELADO);

        HistorialController historial = HistorialController.getInstance();
        historial.registrar(TipoEntidad.ALQUILER, String.valueOf(alquiler.getId()), anterior, "CANCELADO", usuario);

        Vehiculo vehiculo = alquiler.getVehiculo();
        if (vehiculo.getEstado() == EstadoVehiculo.ALQUILADO) {
            vehiculo.cambiarEstado(EstadoVehiculo.DISPONIBLE);
            historial.registrar(TipoEntidad.VEHICULO, vehiculo.getPatente(), "ALQUILADO", "DISPONIBLE", usuario);
        }
    }

    // ---------------------------------------------------------------------
    // UC4 - Finalizar alquiler y calcular saldo
    // ---------------------------------------------------------------------

    /**
     * Finaliza el alquiler (UC4): registra kilometraje final y fecha de
     * devolucion real, calcula el importe total (polimorfico) y el saldo
     * pendiente, libera el vehiculo y devuelve el saldo a cobrar.
     */
    public double finalizarAlquiler(int idAlquiler, int kilometrajeFinal,
                                    LocalDate fechaDevolucionReal, String usuario) {
        Alquiler alquiler = obtenerAlquiler(idAlquiler);
        if (alquiler.getEstado() != EstadoAlquiler.EN_CURSO) {
            throw new IllegalStateException("Solo se puede finalizar un alquiler en estado EN_CURSO.");
        }

        alquiler.setKilometrajeFinal(kilometrajeFinal);
        alquiler.setFechaDevolucionReal(fechaDevolucionReal);

        Vehiculo vehiculo = alquiler.getVehiculo();
        vehiculo.actualizarKilometraje(kilometrajeFinal);

        double importeTotal = alquiler.calcularImporteTotal();
        alquiler.setImporteTotal(importeTotal);
        double saldoPendiente = alquiler.calcularSaldoPendiente();
        alquiler.setImportePendiente(saldoPendiente);

        String anterior = alquiler.getEstado().name();
        alquiler.cambiarEstado(EstadoAlquiler.FINALIZADO);
        String estadoVehAnterior = vehiculo.getEstado().name();
        vehiculo.cambiarEstado(EstadoVehiculo.DISPONIBLE);

        HistorialController historial = HistorialController.getInstance();
        historial.registrar(TipoEntidad.ALQUILER, String.valueOf(alquiler.getId()), anterior, "FINALIZADO", usuario);
        historial.registrar(TipoEntidad.VEHICULO, vehiculo.getPatente(), estadoVehAnterior, "DISPONIBLE", usuario);

        return saldoPendiente;
    }

    // ---------------------------------------------------------------------
    // UC5 y demas consultas del enunciado
    // ---------------------------------------------------------------------

    /**
     * UC5: vehiculos disponibles para un periodo y (opcionalmente) un tipo.
     * Si {@code tipoVehiculo} es null, no se filtra por tipo.
     */
    public List<Vehiculo> consultarVehiculosDisponibles(LocalDate fechaInicio, LocalDate fechaFin,
                                                        TipoVehiculo tipoVehiculo) {
        List<Vehiculo> resultado = new ArrayList<>();
        for (Vehiculo vehiculo : VehiculoController.getInstance().getVehiculos()) {
            if (tipoVehiculo != null && vehiculo.getTipoVehiculo() != tipoVehiculo) {
                continue;
            }
            if (vehiculo.estaDisponible(fechaInicio, fechaFin, alquileres)) {
                resultado.add(vehiculo);
            }
        }
        return resultado;
    }

    /**
     * Consulta 1 del enunciado: total recaudado por alquileres finalizados cuya
     * fecha de devolucion real cae dentro del periodo [desde, hasta].
     */
    public double totalRecaudadoEnPeriodo(LocalDate desde, LocalDate hasta) {
        double total = 0.0;
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getEstado() != EstadoAlquiler.FINALIZADO) {
                continue;
            }
            LocalDate fecha = alquiler.getFechaDevolucionReal();
            if (fecha != null && !fecha.isBefore(desde) && !fecha.isAfter(hasta)) {
                total += alquiler.getImporteTotal();
            }
        }
        return total;
    }

    /**
     * Consulta 3 del enunciado: porcentaje de recargo (positivo) o descuento
     * (negativo) aplicable a un alquiler segun su tipo y la condicion del cliente.
     */
    public double consultarPorcentajeAplicable(int idAlquiler) {
        return obtenerAlquiler(idAlquiler).getPorcentajeAplicado();
    }

    // ---------------------------------------------------------------------
    // Auxiliares
    // ---------------------------------------------------------------------

    private Alquiler buscarAlquilerPorId(int idAlquiler) {
        for (Alquiler alquiler : alquileres) {
            if (alquiler.getId() == idAlquiler) {
                return alquiler;
            }
        }
        return null;
    }

    private Alquiler obtenerAlquiler(int idAlquiler) {
        Alquiler alquiler = buscarAlquilerPorId(idAlquiler);
        if (alquiler == null) {
            throw new IllegalArgumentException("No existe un alquiler con id " + idAlquiler + ".");
        }
        return alquiler;
    }

    public List<Alquiler> getAlquileres() {
        return new ArrayList<>(alquileres);
    }

    public double getPorcentajeDescuentoCorporativo() {
        return porcentajeDescuentoCorporativo;
    }

    public void setPorcentajeDescuentoCorporativo(double porcentajeDescuentoCorporativo) {
        this.porcentajeDescuentoCorporativo = porcentajeDescuentoCorporativo;
    }

    public double getPorcentajeRecargoTuristico() {
        return porcentajeRecargoTuristico;
    }

    public void setPorcentajeRecargoTuristico(double porcentajeRecargoTuristico) {
        this.porcentajeRecargoTuristico = porcentajeRecargoTuristico;
    }
}
