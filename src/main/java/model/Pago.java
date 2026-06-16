package model;

import enums.EstadoPago;
import enums.MedioPago;
import enums.TipoPago;

import java.time.LocalDateTime;

public class Pago {

    private static int proximoId = 1;

    private final int id;
    private final LocalDateTime fecha;
    private final double importe;
    private final MedioPago medioPago;
    private EstadoPago estado;
    private final String usuarioRegistrador;
    private final TipoPago tipoPago;

    public Pago(double importe, MedioPago medioPago, String usuarioRegistrador, TipoPago tipoPago) {
        if (importe <= 0) {
            throw new IllegalArgumentException("El importe del pago debe ser mayor a cero.");
        }
        this.id = proximoId++;
        this.fecha = LocalDateTime.now();
        this.importe = importe;
        this.medioPago = medioPago;
        this.estado = EstadoPago.REGISTRADO;
        this.usuarioRegistrador = usuarioRegistrador;
        this.tipoPago = tipoPago;
    }

    public void confirmar() {
        this.estado = EstadoPago.CONFIRMADO;
    }

    public void anular() {
        this.estado = EstadoPago.ANULADO;
    }

    public boolean esSeniaConfirmada() {
        return tipoPago == TipoPago.SENIA && estado == EstadoPago.CONFIRMADO;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }

    public double getImporte() {
        return importe;
    }

    public MedioPago getMedioPago() {
        return medioPago;
    }

    public EstadoPago getEstado() {
        return estado;
    }

    public String getUsuarioRegistrador() {
        return usuarioRegistrador;
    }

    public TipoPago getTipoPago() {
        return tipoPago;
    }
}
