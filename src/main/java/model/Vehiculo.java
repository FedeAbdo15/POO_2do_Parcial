package model;

import enums.EstadoVehiculo;
import enums.TipoCombustible;
import enums.TipoVehiculo;

import java.time.LocalDate;
import java.util.List;

public class Vehiculo {

    private final String patente;
    private String marca;
    private String modelo;
    private int anio;
    private int kilometrajeActual;
    private EstadoVehiculo estado;
    private double valorDiario;
    private TipoVehiculo tipoVehiculo;
    private TipoCombustible tipoCombustible;
    private int kmIncluidosPorDia;
    private double costoKmExcedente;

    public Vehiculo(String patente, String marca, String modelo, int anio, int kilometrajeActual,
                    double valorDiario, TipoVehiculo tipoVehiculo, TipoCombustible tipoCombustible,
                    int kmIncluidosPorDia, double costoKmExcedente) {
        this.patente = patente;
        this.marca = marca;
        this.modelo = modelo;
        this.anio = anio;
        this.kilometrajeActual = kilometrajeActual;
        this.valorDiario = valorDiario;
        this.tipoVehiculo = tipoVehiculo;
        this.tipoCombustible = tipoCombustible;
        this.kmIncluidosPorDia = kmIncluidosPorDia;
        this.costoKmExcedente = costoKmExcedente;
        this.estado = EstadoVehiculo.DISPONIBLE;
    }

    public void cambiarEstado(EstadoVehiculo nuevoEstado) {
        this.estado = nuevoEstado;
    }

    public boolean estaDisponible(LocalDate fechaInicio, LocalDate fechaFin, List<Alquiler> alquileres) {
        if (estado == EstadoVehiculo.BAJA || estado == EstadoVehiculo.MANTENIMIENTO) {
            return false;
        }
        for (Alquiler alquiler : alquileres) {
            if (alquiler.correspondeAVehiculo(this)
                    && alquiler.estaActivoParaReserva()
                    && alquiler.seSuperpone(fechaInicio, fechaFin)) {
                return false;
            }
        }
        return true;
    }

    public void actualizarKilometraje(int kilometrajeFinal) {
        if (kilometrajeFinal < this.kilometrajeActual) {
            throw new IllegalArgumentException("El kilometraje final no puede ser menor al actual.");
        }
        this.kilometrajeActual = kilometrajeFinal;
    }

    public double calcularCostoKmExcedente(int kmRecorridos, int dias) {
        if (kmIncluidosPorDia <= 0) {
            return 0.0;
        }
        int kmIncluidos = kmIncluidosPorDia * dias;
        int exceso = Math.max(0, kmRecorridos - kmIncluidos);
        return exceso * costoKmExcedente;
    }

    public String getPatente() {
        return patente;
    }

    public String getMarca() {
        return marca;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public String getModelo() {
        return modelo;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public int getAnio() {
        return anio;
    }

    public void setAnio(int anio) {
        this.anio = anio;
    }

    public int getKilometrajeActual() {
        return kilometrajeActual;
    }

    public EstadoVehiculo getEstado() {
        return estado;
    }

    public double getValorDiario() {
        return valorDiario;
    }

    public void setValorDiario(double valorDiario) {
        this.valorDiario = valorDiario;
    }

    public TipoVehiculo getTipoVehiculo() {
        return tipoVehiculo;
    }

    public void setTipoVehiculo(TipoVehiculo tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }

    public TipoCombustible getTipoCombustible() {
        return tipoCombustible;
    }

    public void setTipoCombustible(TipoCombustible tipoCombustible) {
        this.tipoCombustible = tipoCombustible;
    }

    public int getKmIncluidosPorDia() {
        return kmIncluidosPorDia;
    }

    public double getCostoKmExcedente() {
        return costoKmExcedente;
    }

    @Override
    public String toString() {
        return patente + " - " + marca + " " + modelo + " (" + tipoVehiculo + ")";
    }
}
