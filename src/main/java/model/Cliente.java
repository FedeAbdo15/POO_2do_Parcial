package model;

import enums.EstadoCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Cliente {

    private final String dniCuit;
    private String nombreRazonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private EstadoCliente estado;
    private double creditoAFavor;

    private final List<Alquiler> alquileres;

    private Double porcentajeEspecial;
    private LocalDate vigenciaDesde;
    private LocalDate vigenciaHasta;

    public Cliente(String dniCuit, String nombreRazonSocial, String telefono, String email, String direccion) {
        this.dniCuit = dniCuit;
        this.nombreRazonSocial = nombreRazonSocial;
        this.telefono = telefono;
        this.email = email;
        this.direccion = direccion;
        this.estado = EstadoCliente.INACTIVO;
        this.creditoAFavor = 0.0;
        this.alquileres = new ArrayList<>();
    }

    public void activar() {
        this.estado = EstadoCliente.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoCliente.INACTIVO;
    }

    public void agregarCredito(double importe) {
        if (importe < 0) {
            throw new IllegalArgumentException("El credito a agregar no puede ser negativo.");
        }
        this.creditoAFavor += importe;
    }

    public void agregarAlquiler(Alquiler alquiler) {
        this.alquileres.add(alquiler);
    }

    public void setCondicionParticular(double porcentaje, LocalDate desde, LocalDate hasta) {
        this.porcentajeEspecial = porcentaje;
        this.vigenciaDesde = desde;
        this.vigenciaHasta = hasta;
    }

    public boolean tieneCondicionVigente(LocalDate fecha) {
        if (porcentajeEspecial == null || vigenciaDesde == null || vigenciaHasta == null) {
            return false;
        }
        return !fecha.isBefore(vigenciaDesde) && !fecha.isAfter(vigenciaHasta);
    }

    public Double getPorcentajeEspecial() {
        return porcentajeEspecial;
    }

    public String getDniCuit() {
        return dniCuit;
    }

    public String getNombreRazonSocial() {
        return nombreRazonSocial;
    }

    public void setNombreRazonSocial(String nombreRazonSocial) {
        this.nombreRazonSocial = nombreRazonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public EstadoCliente getEstado() {
        return estado;
    }

    public double getCreditoAFavor() {
        return creditoAFavor;
    }

    public List<Alquiler> getAlquileres() {
        return new ArrayList<>(alquileres);
    }

    @Override
    public String toString() {
        return nombreRazonSocial + " (" + dniCuit + ")";
    }
}
