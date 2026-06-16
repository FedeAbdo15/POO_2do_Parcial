package model;

import enums.EstadoAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * Alquiler de un vehiculo por parte de un cliente. Clase <b>abstracta</b>: el
 * calculo del importe total es <b>polimorfico</b> y lo define cada subtipo
 * (comun, corporativo, turistico).
 *
 * <p>Posee sus {@link Pago} por <b>composicion</b> (sena y saldo): se crean y
 * viven dentro del alquiler.</p>
 */
public abstract class Alquiler {

    private static int proximoId = 1;

    private final int id;
    private final Cliente cliente;
    private final Vehiculo vehiculo;
    private final LocalDate fechaInicio;
    private final LocalDate fechaDevolucionEstimada;
    private LocalDate fechaDevolucionReal;
    private EstadoAlquiler estado;
    private final int kilometrajeInicial;
    private int kilometrajeFinal;
    private double importeTotal;
    private double importePendiente;

    private final List<Pago> pagos;

    protected Alquiler(Cliente cliente, Vehiculo vehiculo, LocalDate fechaInicio,
                       LocalDate fechaDevolucionEstimada, int kilometrajeInicial) {
        if (fechaDevolucionEstimada.isBefore(fechaInicio)) {
            throw new IllegalArgumentException("La fecha de devolucion no puede ser anterior a la de inicio.");
        }
        this.id = proximoId++;
        this.cliente = cliente;
        this.vehiculo = vehiculo;
        this.fechaInicio = fechaInicio;
        this.fechaDevolucionEstimada = fechaDevolucionEstimada;
        this.kilometrajeInicial = kilometrajeInicial;
        this.kilometrajeFinal = kilometrajeInicial;
        this.estado = EstadoAlquiler.INGRESADO;
        this.pagos = new ArrayList<>();
    }

    // ---------------------------------------------------------------------
    // Calculo de importes
    // ---------------------------------------------------------------------

    /** Importe total a facturar. Polimorfico: cada subtipo aplica su regla. */
    public abstract double calcularImporteTotal();

    /**
     * Porcentaje de recargo (positivo) o descuento (negativo) que aplica este
     * alquiler. Comun = 0. Sirve para la consulta de porcentaje aplicable.
     */
    public double getPorcentajeAplicado() {
        return 0.0;
    }

    /** Cantidad de dias del alquiler (minimo 1). Usa la fecha real si ya se devolvio. */
    public int calcularDias() {
        LocalDate fin = (fechaDevolucionReal != null) ? fechaDevolucionReal : fechaDevolucionEstimada;
        long dias = ChronoUnit.DAYS.between(fechaInicio, fin);
        return (int) Math.max(1, dias);
    }

    /** Importe base sin recargos ni descuentos: dias por valor diario del vehiculo. */
    public double calcularImporteBase() {
        return calcularDias() * vehiculo.getValorDiario();
    }

    /** Kilometros recorridos (final - inicial, nunca negativo). */
    public int calcularKmRecorridos() {
        return Math.max(0, kilometrajeFinal - kilometrajeInicial);
    }

    /** Costo de los kilometros excedentes, delegado al vehiculo. */
    public double calcularKmExcedentes() {
        return vehiculo.calcularCostoKmExcedente(calcularKmRecorridos(), calcularDias());
    }

    /** Suma de las senas efectivamente confirmadas. */
    public double calcularSeniaAbonada() {
        double total = 0.0;
        for (Pago pago : pagos) {
            if (pago.esSeniaConfirmada()) {
                total += pago.getImporte();
            }
        }
        return total;
    }

    /** Saldo pendiente = importe total (con recargo/descuento y km excedentes) - sena abonada. */
    public double calcularSaldoPendiente() {
        return calcularImporteTotal() - calcularSeniaAbonada();
    }

    /**
     * Horas de anticipacion entre la cancelacion y el inicio del alquiler.
     * Positivo => se cancela antes de empezar.
     */
    public long calcularHorasAnticipacion(LocalDateTime fechaCancelacion) {
        return ChronoUnit.HOURS.between(fechaCancelacion, fechaInicio.atStartOfDay());
    }

    /**
     * Regla de negocio: la sena se reintegra como credito solo si el alquiler
     * esta confirmado y se cancela con mas de 48 horas de anticipacion.
     */
    public boolean puedeReintegrarSenia(LocalDateTime fechaCancelacion) {
        return estado == EstadoAlquiler.CONFIRMADO && calcularHorasAnticipacion(fechaCancelacion) > 48;
    }

    // ---------------------------------------------------------------------
    // Estado y relaciones
    // ---------------------------------------------------------------------

    public void cambiarEstado(EstadoAlquiler nuevoEstado) {
        this.estado = nuevoEstado;
    }

    /** Indica si el alquiler bloquea la disponibilidad del vehiculo (reserva activa). */
    public boolean estaActivoParaReserva() {
        return estado == EstadoAlquiler.INGRESADO
                || estado == EstadoAlquiler.CONFIRMADO
                || estado == EstadoAlquiler.EN_CURSO;
    }

    /** Indica si el alquiler corresponde al vehiculo dado (comparacion por patente). */
    public boolean correspondeAVehiculo(Vehiculo otro) {
        return vehiculo != null && otro != null
                && vehiculo.getPatente().equalsIgnoreCase(otro.getPatente());
    }

    /**
     * Determina si el periodo [inicio, fin] se superpone con el periodo de este
     * alquiler [fechaInicio, fechaDevolucionEstimada] (limites inclusive).
     */
    public boolean seSuperpone(LocalDate inicio, LocalDate fin) {
        return !fechaInicio.isAfter(fin) && !inicio.isAfter(fechaDevolucionEstimada);
    }

    /** Agrega un pago a la composicion del alquiler. */
    public void agregarPago(Pago pago) {
        this.pagos.add(pago);
    }

    // ---------------------------------------------------------------------
    // Getters / setters
    // ---------------------------------------------------------------------

    public int getId() {
        return id;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public Vehiculo getVehiculo() {
        return vehiculo;
    }

    public LocalDate getFechaInicio() {
        return fechaInicio;
    }

    public LocalDate getFechaDevolucionEstimada() {
        return fechaDevolucionEstimada;
    }

    public LocalDate getFechaDevolucionReal() {
        return fechaDevolucionReal;
    }

    public void setFechaDevolucionReal(LocalDate fechaDevolucionReal) {
        this.fechaDevolucionReal = fechaDevolucionReal;
    }

    public EstadoAlquiler getEstado() {
        return estado;
    }

    public int getKilometrajeInicial() {
        return kilometrajeInicial;
    }

    public int getKilometrajeFinal() {
        return kilometrajeFinal;
    }

    public void setKilometrajeFinal(int kilometrajeFinal) {
        this.kilometrajeFinal = kilometrajeFinal;
    }

    public double getImporteTotal() {
        return importeTotal;
    }

    public void setImporteTotal(double importeTotal) {
        this.importeTotal = importeTotal;
    }

    public double getImportePendiente() {
        return importePendiente;
    }

    public void setImportePendiente(double importePendiente) {
        this.importePendiente = importePendiente;
    }

    /** Devuelve una copia de los pagos para no exponer la coleccion interna. */
    public List<Pago> getPagos() {
        return new ArrayList<>(pagos);
    }
}
