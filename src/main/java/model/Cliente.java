package model;

import enums.EstadoCliente;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * Cliente particular o empresa. Identificado por DNI/CUIT.
 *
 * <p>Mantiene la coleccion de sus alquileres (relacion Cliente 1 -> 0..* Alquiler),
 * lo que permite consultar sus alquileres sin que {@code ClienteController}
 * dependa de {@code AlquilerController} (se respeta el grafo unidireccional).</p>
 *
 * <p>Soporta una <b>condicion particular</b> opcional: un porcentaje de
 * recargo/descuento negociado, valido solo dentro de un periodo de vigencia.
 * Si esta vigente, reemplaza al porcentaje general del tipo de alquiler.</p>
 */
public class Cliente {

    private final String dniCuit;
    private String nombreRazonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private EstadoCliente estado;
    private double creditoAFavor;

    private final List<Alquiler> alquileres;

    // Condicion particular parametrizable por cliente (opcional).
    private Double porcentajeEspecial;   // null => sin condicion particular
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

    /** Marca el cliente como activo. */
    public void activar() {
        this.estado = EstadoCliente.ACTIVO;
    }

    public void desactivar() {
        this.estado = EstadoCliente.INACTIVO;
    }

    /** Suma credito a favor (por ejemplo, sena reintegrada en una cancelacion). */
    public void agregarCredito(double importe) {
        if (importe < 0) {
            throw new IllegalArgumentException("El credito a agregar no puede ser negativo.");
        }
        this.creditoAFavor += importe;
    }

    /** Asocia un alquiler a este cliente (lado 1 de la relacion). */
    public void agregarAlquiler(Alquiler alquiler) {
        this.alquileres.add(alquiler);
    }

    /**
     * Define una condicion particular de porcentaje con vigencia.
     *
     * @param porcentaje magnitud del recargo/descuento especial (en %)
     * @param desde      inicio de vigencia (inclusive)
     * @param hasta      fin de vigencia (inclusive)
     */
    public void setCondicionParticular(double porcentaje, LocalDate desde, LocalDate hasta) {
        this.porcentajeEspecial = porcentaje;
        this.vigenciaDesde = desde;
        this.vigenciaHasta = hasta;
    }

    /** Indica si en la fecha dada el cliente tiene una condicion particular vigente. */
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

    /** Devuelve una copia de la lista de alquileres para no exponer la coleccion interna. */
    public List<Alquiler> getAlquileres() {
        return new ArrayList<>(alquileres);
    }

    @Override
    public String toString() {
        return nombreRazonSocial + " (" + dniCuit + ")";
    }
}
