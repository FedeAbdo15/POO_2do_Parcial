package enums;

/** Ciclo de vida de un {@code Alquiler}: INGRESADO -> CONFIRMADO -> EN_CURSO -> FINALIZADO (o CANCELADO). */
public enum EstadoAlquiler {
    INGRESADO,
    CONFIRMADO,
    EN_CURSO,
    FINALIZADO,
    CANCELADO
}
