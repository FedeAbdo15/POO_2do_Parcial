package model;

import enums.EstadoAlquiler;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

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

    public abstract double calcularImporteTotal();

    public double getPorcentajeAplicado() {
        return 0.0;
    }

    public int calcularDias() {
        LocalDate fin = (fechaDevolucionReal != null) ? fechaDevolucionReal : fechaDevolucionEstimada;
        long dias = ChronoUnit.DAYS.between(fechaInicio, fin);
        return (int) Math.max(1, dias);
    }

    public double calcularImporteBase() {
        return calcularDias() * vehiculo.getValorDiario();
    }

    public int calcularKmRecorridos() {
        return Math.max(0, kilometrajeFinal - kilometrajeInicial);
    }

    public double calcularKmExcedentes() {
        return vehiculo.calcularCostoKmExcedente(calcularKmRecorridos(), calcularDias());
    }

    public double calcularSeniaAbonada() {
        double total = 0.0;
        for (Pago pago : pagos) {
            if (pago.esSeniaConfirmada()) {
                total += pago.getImporte();
            }
        }
        return total;
    }

    public double calcularSaldoPendiente() {
        return calcularImporteTotal() - calcularSeniaAbonada();
    }

    public long calcularHorasAnticipacion(LocalDateTime fechaCancelacion) {
        return ChronoUnit.HOURS.between(fechaCancelacion, fechaInicio.atStartOfDay());
    }

    public boolean puedeReintegrarSenia(LocalDateTime fechaCancelacion) {
        return estado == EstadoAlquiler.CONFIRMADO && calcularHorasAnticipacion(fechaCancelacion) > 48;
    }

    public void cambiarEstado(EstadoAlquiler nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean estaActivoParaReserva() {
        return estado == EstadoAlquiler.INGRESADO
                || estado == EstadoAlquiler.CONFIRMADO
                || estado == EstadoAlquiler.EN_CURSO;
    }

    public boolean correspondeAVehiculo(Vehiculo otro) {
        return vehiculo != null && otro != null
                && vehiculo.getPatente().equalsIgnoreCase(otro.getPatente());
    }

    public boolean seSuperpone(LocalDate inicio, LocalDate fin) {
        return !fechaInicio.isAfter(fin) && !inicio.isAfter(fechaDevolucionEstimada);
    }

    public void agregarPago(Pago pago) {
        this.pagos.add(pago);
    }

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

    public List<Pago> getPagos() {
        return new ArrayList<>(pagos);
    }
}
