package controller;

import enums.EstadoAlquiler;
import enums.TipoEntidad;
import model.Alquiler;
import model.Cliente;

import java.util.ArrayList;
import java.util.List;

public class ClienteController {

    private static ClienteController instancia;

    private final List<Cliente> clientes;

    private ClienteController() {
        this.clientes = new ArrayList<>();
    }

    public static ClienteController getInstance() {
        if (instancia == null) {
            instancia = new ClienteController();
        }
        return instancia;
    }

    public Cliente registrarCliente(String dniCuit, String nombreRazonSocial, String telefono,
                                    String email, String direccion, String usuario) {
        if (dniCuit == null || dniCuit.isBlank()) {
            throw new IllegalArgumentException("El DNI/CUIT es obligatorio.");
        }
        if (buscarPorDni(dniCuit) != null) {
            throw new IllegalArgumentException("Ya existe un cliente con DNI/CUIT " + dniCuit + ".");
        }
        Cliente cliente = new Cliente(dniCuit, nombreRazonSocial, telefono, email, direccion);
        cliente.activar();
        clientes.add(cliente);
        HistorialController.getInstance().registrar(TipoEntidad.CLIENTE, dniCuit, "-", "ACTIVO", usuario);
        return cliente;
    }

    public Cliente buscarPorDni(String dniCuit) {
        for (Cliente cliente : clientes) {
            if (cliente.getDniCuit().equals(dniCuit)) {
                return cliente;
            }
        }
        return null;
    }

    public List<Alquiler> listarConfirmadosDeCliente(String dniCuit) {
        Cliente cliente = buscarPorDni(dniCuit);
        if (cliente == null) {
            throw new IllegalArgumentException("No existe un cliente con DNI/CUIT " + dniCuit + ".");
        }
        List<Alquiler> confirmados = new ArrayList<>();
        for (Alquiler alquiler : cliente.getAlquileres()) {
            if (alquiler.getEstado() == EstadoAlquiler.CONFIRMADO) {
                confirmados.add(alquiler);
            }
        }
        return confirmados;
    }

    public List<Cliente> getClientes() {
        return new ArrayList<>(clientes);
    }
}
