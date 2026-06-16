package model;

import enums.EstadoPago;
import enums.MedioPago;
import enums.TipoPago;

import java.time.LocalDateTime;

/**
 * Pago asociado a un alquiler. Puede ser una <b>sena</b> (para confirmar la
 * reserva) o el <b>saldo</b> final. Forma parte de la composicion de
 * {@link Alquiler}: se crea y vive dentro del alquiler.
 */
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

    /** Confirma el pago (queda efectivamente acreditado). */
    public void confirmar() {
        this.estado = EstadoPago.CONFIRMADO;
    }

    /** Anula el pago. */
    public void anular() {
        this.estado = EstadoPago.ANULADO;
    }

    /** Indica si este pago es una sena que ya fue confirmada. */
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
