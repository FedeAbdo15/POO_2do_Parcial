package controller;

import enums.TipoEntidad;
import model.HistorialCambioEstado;

import java.util.ArrayList;
import java.util.List;

/**
 * Controlador Singleton de auditoria. Servicio <b>transversal</b>: los demas
 * controladores lo invocan para registrar cada cambio de estado de las
 * entidades del sistema. No depende de ningun otro controlador.
 */
public class HistorialController {

    private static HistorialController instancia;

    private final List<HistorialCambioEstado> historiales;

    private HistorialController() {
        this.historiales = new ArrayList<>();
    }

    public static HistorialController getInstance() {
        if (instancia == null) {
            instancia = new HistorialController();
        }
        return instancia;
    }

    /** Registra un cambio de estado en el historial de auditoria. */
    public void registrar(TipoEntidad tipoEntidad, String referenciaEntidad,
                          String estadoAnterior, String estadoNuevo, String usuario) {
        historiales.add(new HistorialCambioEstado(estadoAnterior, estadoNuevo,
                tipoEntidad, referenciaEntidad, usuario));
    }

    /** Lista los cambios registrados para una entidad concreta. */
    public List<HistorialCambioEstado> listarPorEntidad(TipoEntidad tipoEntidad, String referenciaEntidad) {
        List<HistorialCambioEstado> resultado = new ArrayList<>();
        for (HistorialCambioEstado h : historiales) {
            if (h.getTipoEntidad() == tipoEntidad && h.getReferenciaEntidad().equals(referenciaEntidad)) {
                resultado.add(h);
            }
        }
        return resultado;
    }

    public List<HistorialCambioEstado> getHistoriales() {
        return new ArrayList<>(historiales);
    }
}
