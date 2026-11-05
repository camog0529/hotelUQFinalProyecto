# Manual de usuario de HotelUQ

## Iniciar sesión

La pantalla inicial permite ingresar con un usuario y contraseña o abrir el formulario de registro de clientes. Para las pruebas locales se cargan estas cuentas:

| Perfil | Usuario | Contraseña |
| --- | --- | --- |
| Administrador | admin | HotelUQ2026! |
| Cliente de prueba | 1111 | Cliente2026! |
| Cliente de prueba | 2222 | Cliente2026! |

Escribe las credenciales y pulsa **Ingresar**. El sistema abre el panel correspondiente al rol de la cuenta: el panel administrativo para el administrador y la pantalla de reservas para el cliente. Si los datos no coinciden, verifica el usuario y la contraseña e inténtalo de nuevo.

Las cuentas de demostración son solo para esta aplicación local. No reutilices esas contraseñas en otros servicios.

## Crear una cuenta de cliente

Desde la pantalla de inicio de sesión:

1. Pulsa **Crear cuenta de cliente**.
2. Escribe la cédula, el nombre completo, el correo y una contraseña.
3. Confirma la contraseña y pulsa **Crear cuenta**.
4. Al terminar, la aplicación inicia la sesión con la nueva cuenta y abre la pantalla del cliente.

La contraseña debe tener al menos ocho caracteres y ambas entradas deben coincidir. La cédula, el correo y el usuario deben ser únicos. El usuario de acceso del cliente es su cédula.

El registro público crea únicamente cuentas de cliente. La aplicación inicia con una cuenta administrativa de demostración; no hay una opción en la interfaz para registrar administradores. Las cuentas administrativas adicionales requieren configuración por parte del equipo que mantiene el sistema.

## Reservar una habitación

En la vista **Gestionar Reservas**:

1. Selecciona la fecha de entrada y la fecha de salida.
2. Elige una habitación disponible. La disponibilidad depende de las fechas seleccionadas.
3. Si deseas, marca los servicios extra. La aplicación ofrece acceso premium a Spa, desayuno, parqueadero y seguro de cancelación; sus precios aparecen junto a cada opción.
4. Si hay una promoción aplicable, selecciónala en el campo **Promoción**. Las promociones pueden depender de las fechas, del tipo de habitación y de un mínimo de noches.
5. Pulsa **Calcular Costo** para revisar el valor antes de confirmar.
6. Pulsa **Confirmar y Pagar con Tarjeta** para registrar la reserva.

La confirmación requiere que la habitación siga disponible y que la tarjeta virtual del cliente tenga saldo suficiente. El sistema muestra un aviso si no puede completar la operación. Los pagos no se procesan con una entidad financiera real. La aplicación registra los movimientos en la cuenta y simula la notificación de confirmación.

Las reservas registradas aparecen en la tabla **Reservas registradas**. Selecciona una reserva y pulsa **Cancelar reserva seleccionada** para solicitar su cancelación como cliente. La aplicación valida el estado de la reserva y aplica sus reglas de reembolso según el momento de la solicitud.

## Consultar la cuenta del cliente

Pulsa **Clientes y Tarjetas** o **Mi cuenta** para consultar los datos del usuario autenticado, el saldo de su tarjeta virtual, los puntos acumulados y el historial de movimientos. Desde esa pantalla puedes volver a **Mis reservas** y continuar la gestión.

Para terminar, pulsa **Cerrar sesión**. La sesión se cierra y la aplicación regresa a la pantalla de inicio.

## Funciones del administrador

Inicia sesión con una cuenta cuyo rol sea administrador. El panel administrativo organiza sus funciones en pestañas.

### Panel general

El panel muestra las reservas registradas, el porcentaje de ocupación y la cantidad de reservas por tipo de habitación. Pulsa **Actualizar** para volver a cargar estos datos.

Para cancelar una reserva por parte del hotel, selecciónala en la tabla y pulsa **Cancelar reserva (hotel)**. Esta operación registra la cancelación del hotel y el reembolso completo.

Para guardar un reporte, pulsa **Exportar ingresos TXT**, elige la ubicación y el nombre del archivo y guarda el documento. El reporte se genera en formato de texto.

### Registrar una habitación

En **Panel general**, completa el número de habitación, el tipo y la tarifa por noche; después pulsa **Agregar**. El número debe ser único y la tarifa debe ser un valor numérico válido. La habitación registrada queda disponible para futuras reservas cuando no esté ocupada.

### Recargar la tarjeta virtual de un cliente

En **Panel general**, escribe la cédula del cliente y el monto de la recarga y pulsa **Registrar recarga**. La cédula debe corresponder a un cliente existente y el monto debe ser mayor que cero. El movimiento queda reflejado en el saldo y en el historial del cliente. Esta recarga es una operación de prueba dentro del sistema.

### Gestionar clientes

Abre la pestaña **Clientes**. Para crear una cuenta de cliente desde el panel administrativo, completa cédula, nombre, correo y contraseña y pulsa **Guardar / actualizar**. Esta operación crea un perfil de cliente; no crea un administrador.

Para modificar un cliente, selecciónalo en la tabla, cambia su nombre o correo y pulsa **Guardar / actualizar**. Si quieres cambiar la contraseña, escribe una nueva de al menos ocho caracteres; deja el campo vacío para conservar la contraseña actual.

Para eliminar un cliente, selecciónalo y pulsa **Eliminar seleccionado**. El sistema no permite eliminar clientes que tengan reservas registradas, porque debe conservarse el historial. Usa **Limpiar** para vaciar el formulario y comenzar otra operación.

### Administrar promociones

En la pestaña **Promociones**, completa el código, la descripción, las fechas de inicio y fin, el tipo de habitación (o todos los tipos, si no se especifica), el mínimo de noches, el porcentaje de descuento y el estado activo. Pulsa **Guardar** para crear la promoción.

Para editar una promoción, selecciónala en la tabla, modifica sus datos y guarda los cambios. Para quitarla, selecciónala y pulsa **Eliminar**. Los clientes solo pueden elegir promociones activas cuyas condiciones coincidan con su reserva.

## Datos de prueba

La aplicación mantiene clientes, habitaciones, reservas, promociones, saldo y movimientos en memoria. Los cambios sirven durante la ejecución, pero no permanecen después de cerrar la aplicación. No se envían correos reales ni se realizan cargos bancarios reales.
