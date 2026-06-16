package controller;

import enums.EstadoAlquiler;
import enums.TipoEntidad;
import model.Alquiler;
import model.Cliente;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Singleton de la entidad maestra {@link Cliente}.
 * No depende de otros controladores (salvo el de auditoria).
 */
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

    /**
     * Registra un nuevo cliente (UC1). Valida que el DNI/CUIT no este repetido,
     * lo activa y registra el cambio en la auditoria.
     */
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

    /** Busca un cliente por DNI/CUIT. Devuelve null si no existe. */
    public Cliente buscarPorDni(String dniCuit) {
        for (Cliente cliente : clientes) {
            if (cliente.getDniCuit().equals(dniCuit)) {
                return cliente;
            }
        }
        return null;
    }

    /**
     * Consulta 2 del enunciado: alquileres confirmados de un cliente.
     * Se resuelve navegando la coleccion propia del cliente, por lo que este
     * controlador no necesita depender de {@code AlquilerController}.
     */
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
