# Sistema de Gestión de Alquiler de Vehículos — TP Integrador POO

Implementación en Java (JDK 24) del sistema de alquiler de vehículos, aplicando
**MVC**, **controladores Singleton** (patrón GRASP Controller, sin clase `Sistema`
"objeto dios"), **herencia/polimorfismo**, **interfaces Swing** y **pruebas JUnit 5**.

## Cómo ejecutar

### Desde IntelliJ IDEA (recomendado)
1. Abrir la carpeta del proyecto. El SDK del módulo es **openjdk-24**.
2. Marcar carpetas (si IntelliJ no las reconoce solo): `src/main/java` como *Sources*,
   `src/test/java` como *Test Sources*, `src/main/resources` como *Resources*.
   El archivo `.iml` ya viene configurado así y con la librería `lib/junit-platform-console-standalone-1.11.4.jar`.
3. Ejecutar la clase `Main` (carga datos de demo y abre el menú con las 2 ventanas).
4. Ejecutar los tests: click derecho sobre `src/test/java` → *Run All Tests*.

### Desde la línea de comandos
```bash
JAVA=~/.jdks/openjdk-24.0.1/bin       # ajustar según el sistema
JUNIT=lib/junit-platform-console-standalone-1.11.4.jar

# Compilar fuentes principales
$JAVA/javac -encoding UTF-8 -d out/production $(find src/main/java -name "*.java")

# Ejecutar la app
$JAVA/java -cp out/production Main

# Compilar y correr tests
$JAVA/javac -encoding UTF-8 -cp "out/production;$JUNIT" -d out/test $(find src/test/java -name "*.java")
$JAVA/java -jar $JUNIT execute --class-path "out/production;out/test" --scan-classpath
```
(En Windows/PowerShell el separador de classpath es `;`; en Linux/Mac es `:`.)

## Arquitectura (MVC + Singleton)

```
src/main/java/
├── Main.java                 # arranque + datos de demo
├── enums/                    # 9 enumeraciones del dominio
├── model/                    # MODELO: entidades y reglas de negocio
│   ├── Cliente, Vehiculo, Pago, HistorialCambioEstado
│   └── Alquiler (abstract) → AlquilerComun / AlquilerCorporativo / AlquilerTuristico
├── controller/               # CONTROLADORES Singleton (lógica de los casos de uso)
│   ├── AlquilerController     # orquestador: UC3, UC4, UC5 + confirmar/iniciar/cancelar
│   ├── ClienteController      # UC1, consulta de confirmados
│   ├── VehiculoController     # UC2
│   └── HistorialController    # auditoría transversal
└── view/                     # VISTA: ventanas Swing (sin lógica de negocio)
    ├── MenuPrincipal
    ├── VentanaSolicitarAlquiler        (UC3)
    └── VentanaConsultarDisponibles     (UC5)
src/test/java/tests/          # 5 pruebas JUnit 5
```

**Grafo de dependencias unidireccional:** `AlquilerController` → `ClienteController` +
`VehiculoController`; los tres registran auditoría en `HistorialController`. Ningún
controlador maestro depende del orquestador. Por eso `consultarVehiculosDisponibles`
(UC5) vive en `AlquilerController` (necesita la lista de alquileres).

## Dónde se cumple cada ítem de evaluación

| Ítem | Dónde |
|---|---|
| 1. Modelo de dominio (encapsulamiento, constructores) | `model/` — atributos privados + getters/setters, sin clase `Sistema` |
| 2. Relaciones, herencia y polimorfismo | `Alquiler` abstracta + 3 subclases; `calcularImporteTotal()` polimórfico; colecciones `List<>` |
| 3. Casos de uso (5 diagramas de secuencia) | UC1 `ClienteController.registrarCliente`; UC2 `VehiculoController.registrarVehiculo`; UC3 `AlquilerController.solicitarAlquiler`; UC4 `finalizarAlquiler`; UC5 `consultarVehiculosDisponibles` |
| 4. Patrón MVC | `view/` solo valida formato y delega; `controller/` coordina; `model/` tiene los datos y reglas |
| 5. Controladores Singleton | `instancia` static + constructor privado + `getInstance()` en los 4 controladores |
| 6. Interfaces gráficas (2 ventanas) | `VentanaSolicitarAlquiler` y `VentanaConsultarDisponibles` |
| 7. Pruebas unitarias (5) | `src/test/java/tests/` (ver abajo) |

### Las 4 consultas del enunciado
1. Total recaudado en período → `AlquilerController.totalRecaudadoEnPeriodo`
2. Alquileres confirmados de un cliente → `ClienteController.listarConfirmadosDeCliente`
3. % de recargo/descuento aplicable → `AlquilerController.consultarPorcentajeAplicable`
4. Vehículos disponibles → `AlquilerController.consultarVehiculosDisponibles`

### Las 5 pruebas unitarias
1. `AlquilerCorporativo` aplica −10% · 2. `AlquilerTuristico` aplica +15% ·
3. Cancelación >48h reintegra seña / <48h no · 4. `Vehiculo.estaDisponible` detecta
superposición · 5. `calcularSaldoPendiente` resta seña y suma km excedentes.

## Decisiones de diseño
- **Dinero como `double`** (simplicidad para el contexto académico; en producción se usaría `BigDecimal`).
- **Fechas:** `LocalDate` para períodos de alquiler; `LocalDateTime` para pagos, auditoría y cancelación (regla de 48 hs).
- **Composición** `Alquiler`→`Pago` (seña y saldo viven dentro del alquiler).
- **Relación `Cliente`→`Alquiler`** materializada en `Cliente` para resolver la consulta 2
  sin romper el grafo unidireccional de controladores.
- **Porcentajes parametrizables:** valores generales configurables en `AlquilerController`
  y **condición particular por cliente con vigencia** (`Cliente.setCondicionParticular`).
- `calcularImporteTotal()` = base ± (recargo/descuento) + km excedentes. La seña se resta en `calcularSaldoPendiente()`.
