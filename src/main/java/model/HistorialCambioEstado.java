package model;

import enums.TipoEntidad;

import java.time.LocalDateTime;

public class HistorialCambioEstado {

    private static int proximoId = 1;

    private final int id;
    private final LocalDateTime fechaCambio;
    private final String estadoAnterior;
    private final String estadoNuevo;
    private final TipoEntidad tipoEntidad;
    private final String referenciaEntidad;
    private final String usuarioResponsable;

    public HistorialCambioEstado(String estadoAnterior, String estadoNuevo, TipoEntidad tipoEntidad,
                                 String referenciaEntidad, String usuarioResponsable) {
        this.id = proximoId++;
        this.fechaCambio = LocalDateTime.now();
        this.estadoAnterior = estadoAnterior;
        this.estadoNuevo = estadoNuevo;
        this.tipoEntidad = tipoEntidad;
        this.referenciaEntidad = referenciaEntidad;
        this.usuarioResponsable = usuarioResponsable;
    }

    public int getId() {
        return id;
    }

    public LocalDateTime getFechaCambio() {
        return fechaCambio;
    }

    public String getEstadoAnterior() {
        return estadoAnterior;
    }

    public String getEstadoNuevo() {
        return estadoNuevo;
    }

    public TipoEntidad getTipoEntidad() {
        return tipoEntidad;
    }

    public String getReferenciaEntidad() {
        return referenciaEntidad;
    }

    public String getUsuarioResponsable() {
        return usuarioResponsable;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s %s: %s -> %s (por %s)",
                fechaCambio, tipoEntidad, referenciaEntidad, estadoAnterior, estadoNuevo, usuarioResponsable);
    }
}
