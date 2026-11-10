# Arquitectura y patrones de HotelUQ

## Separación por capas

- **Vista:** `src/main/resources/fxml` y `src/main/resources/css`.
- **Controlador:** `hoteluq.controller`; recibe eventos de JavaFX y llama a la fachada.
- **Servicios/casos de uso:** `hoteluq.service`; coordina operaciones del hotel.
- **Modelo:** `hoteluq.model`; contiene entidades, reglas de negocio y patrones.
- **Repositorio:** `hoteluq.repository.HotelUQData`; almacena datos en memoria para la demostración.
- **Seguridad de sesión:** `AutenticacionService`, `CuentaUsuario`, `RolUsuario` y `SesionUsuario` gestionan cuentas, autenticación y rol activo.

Los controladores de las vistas no consultan el repositorio directamente. `HotelUQFacade` concentra esas operaciones y el `MediadorReservas` coordina el pago y la confirmación.

## Registro e inicio de sesión

El programa abre en `LoginView.fxml`. Los clientes pueden crear una cuenta; el registro no permite escoger el rol de administrador. Las contraseñas se guardan como hashes PBKDF2 con una sal aleatoria. `SesionUsuario` mantiene la identidad activa hasta cerrar sesión o terminar la aplicación.

Las operaciones administrativas se validan en la fachada y en `ProxyRecargas`, además de ocultarse las pantallas según el rol. Las cuentas de demostración son `admin / HotelUQ2026!` y `1111` o `2222 / Cliente2026!`. Cambia o elimina esas credenciales antes de presentar una versión que no sea una demostración académica.

En el panel administrativo, la pestaña **Clientes** permite crear cuentas, consultar perfiles, actualizar nombre/correo y restablecer contraseña. El sistema conserva la cédula y bloquea la eliminación cuando hay reservas asociadas para proteger el historial.

La pestaña **Promociones** permite crear, editar, activar/desactivar y eliminar códigos de descuento. Cada promoción define fechas de validez, noches mínimas, porcentaje y, opcionalmente, tipo de habitación. Una reserva solo la acepta si cumple todas las condiciones.

## Patrones creacionales

| Patrón | Clases | Problema y uso |
|---|---|---|
| Singleton | `HotelUQData` | Mantiene una única colección en memoria durante la ejecución, compartida por los casos de uso. |
| Factory | `HabitacionFactory`, `HabitacionEstandar`, `HabitacionFamiliar`, `HabitacionSuite` | Centraliza la creación de habitaciones según su tipo y evita que la interfaz elija directamente las subclases. |
| Builder | `ReservaBuilder`, `Reserva` | Construye reservas con cliente, habitación, fechas, servicios y tarifa; valida fechas y calcula el total antes de crear la entidad. |

## Patrones estructurales

| Patrón | Clases | Problema y uso |
|---|---|---|
| Facade | `HotelUQFacade` | Ofrece a los controladores una entrada simple para los casos de uso de reservas, habitaciones, clientes, puntos y reportes. |
| Proxy | `ProxyRecargas` | Comprueba el permiso administrativo antes de delegar una recarga a `ComandoProcesarPago`. |
| Adapter | `ProcesadorPago`, `PasarelaPagoAdapter`, `MediadorReservas` | Traduce el cobro de una reserva a las operaciones de saldo que ofrece `Cliente`, detrás de un contrato intercambiable. |
| Composite | `ComponenteServicio`, `ServicioIndividual`, `PaqueteServicios` | Permite sumar servicios individuales y paquetes con la misma interfaz. La creación de reservas usa este componente para calcular extras. |
| Decorator | `ServicioDecorador`, `DecoradorSeguroCancelacion`, `ReservaBuilder` | Añade el costo y descripción del seguro opcional alrededor del paquete sin cambiar las clases de cada servicio. El seguro habilita reembolso completo si se cancela antes de la entrada. |

## Patrones de comportamiento

| Patrón | Clases | Problema y uso |
|---|---|---|
| Strategy | `EstrategiaTarifa`, `TarifaTemporadaAlta`, `TarifaTemporadaBaja`, `ReservaBuilder` | Cambia el cálculo por noche sin alterar el builder. La interfaz selecciona temporada alta en diciembre y enero; las demás fechas usan temporada baja. |
| Observer | `ObservadorPuntos`, `SistemaPuntosFidelidad`, `SistemaNotificaciones`, `MediadorReservas` | Después de confirmar una reserva, notifica sistemas independientes para acumular puntos y registrar la notificación. |
| Command | `ComandoPago`, `ComandoProcesarPago`, `ProxyRecargas` | Encapsula la operación de recarga para ejecutarla desde el proxy y permite revertir el saldo con `deshacer()`. |
| Memento | `MementoTarjeta`, `ComandoProcesarPago`, `Cliente` | Conserva el saldo anterior al comando y permite restaurarlo con un movimiento de reversión en la bitácora. |
| Mediator | `MediadorReservas`, `ProcesadorPago`, `ObservadorPuntos` | Coordina disponibilidad, cobro, confirmación, persistencia en memoria y notificaciones sin que la vista orqueste esos objetos. |
| Visitor | `VisitanteEstadisticas`, `VisitanteReporte`, `HotelUQFacade` | Separa el cálculo de estadísticas y la generación del contenido del reporte de las entidades de reserva. |
| Chain of Responsibility | `ManejadorFiltro`, `FiltroDisponibilidad` | Encadena filtros de habitaciones: cada manejador aplica una regla y pasa los resultados al siguiente. `FiltroDisponibilidad` valida el rango de fechas y conserva las habitaciones disponibles; la estructura permite agregar otros filtros, como precio o capacidad. |

## Principios SOLID evidenciados

| Principio | Problema que aborda | Clases y solución | Por qué aplica |
|---|---|---|---|
| SRP | Si el controlador calcula tarifas, valida pagos y genera reportes, cualquier cambio afecta a la interfaz. | `MainController` recoge datos; `ReservaBuilder` calcula el valor; `MediadorReservas` coordina la confirmación; `VisitanteReporte` genera el texto. | Cada clase concentra una razón principal de cambio dentro del flujo implementado. |
| OCP | Agregar una tarifa estacional no debería requerir reescribir el cálculo de la reserva. | `EstrategiaTarifa` permite agregar otra estrategia concreta; `ReservaBuilder` consume la abstracción. | Se amplía el comportamiento añadiendo estrategias, conservando el contrato de cálculo. |
| LSP | El sistema debe poder tratar los tipos de habitación como habitaciones en cualquier flujo común. | `HabitacionEstandar`, `HabitacionFamiliar` y `HabitacionSuite` heredan de `Habitacion`; `HabitacionFactory` devuelve el tipo base. | La selección, disponibilidad y tarifa funcionan con la abstracción común. |
| ISP | Un cliente de pago no debe depender de operaciones de reportes o notificaciones. | `ProcesadorPago` expone solo `procesarPago`; `ObservadorPuntos` expone solo `actualizar`. | Cada consumidor depende de un contrato pequeño y enfocado. |
| DIP | El mediador no debería depender de detalles concretos del mecanismo de pago. | `MediadorReservas` recibe `ProcesadorPago`; `PasarelaPagoAdapter` es la implementación predeterminada. | La implementación puede sustituirse sin modificar las reglas del mediador. |

## Reglas adicionales definidas

1. Las tarifas suben 30 % en temporada alta (diciembre y enero) y bajan 10 % en los demás meses.
2. El seguro opcional de cancelación cuesta $30.000 y devuelve el total pagado cuando la cancelación ocurre antes del día de entrada.

## Alcance actual y pendientes de producto

Las cuentas y demás datos continúan guardándose en memoria y se reinician al cerrar la aplicación. La autenticación y los controles de rol protegen los flujos dentro de esta ejecución, pero las cuentas de demostración son conocidas y no hay almacenamiento persistente. Quedan por agregar edición/eliminación de habitaciones, persistencia y diagramas PDF de clases/patrones para la entrega.
