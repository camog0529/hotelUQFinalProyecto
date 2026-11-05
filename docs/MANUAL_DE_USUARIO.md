# Manual de usuario de HotelUQ

## Inicio de sesión

La pantalla inicial permite ingresar con una cuenta existente o crear una cuenta de cliente.

### Cuentas de demostración

| Perfil | Usuario / cédula | Contraseña |
| --- | --- | --- |
| Administrador | `admin` | `HotelUQ2026!` |
| Cliente de demostración | `1111` o `2222` | `Cliente2026!` |

Para probar el sistema, puedes iniciar sesión con una de estas cuentas. Usa **Crear cuenta de cliente** si quieres registrar un cliente nuevo con tu propia cédula, nombre, correo y contraseña. La contraseña debe tener al menos 8 caracteres. El registro público crea cuentas de cliente, no cuentas de administrador.

## Funciones del cliente

Después de iniciar sesión como cliente:

1. En **Gestionar Reservas**, selecciona una habitación disponible y las fechas de entrada y salida.
2. Si quieres, agrega servicios extra o selecciona una promoción disponible.
3. Pulsa **Calcular Costo** para revisar el valor.
4. Pulsa **Confirmar y Pagar con Tarjeta** para registrar la reserva.
5. En **Clientes y Tarjetas** / **Mi cuenta**, consulta tus datos, saldo, puntos y movimientos.
6. Usa **Cerrar sesión** para salir.

## Funciones del administrador

Inicia sesión con la cuenta de administrador de demostración para abrir el panel administrativo. Desde allí se pueden consultar reservas y ocupación, cancelar una reserva, gestionar clientes y habitaciones, administrar promociones y exportar un reporte de ingresos.

## Ejecutar desde IntelliJ IDEA

El proyecto usa Maven y JavaFX. Selecciona la configuración Maven `javafx:run` junto a la flecha verde y pulsa **Run**. Si no aparece, abre **Run → Edit Configurations…**, crea una configuración **Maven**, escribe `javafx:run` en **Run** y verifica que el directorio de trabajo sea la carpeta raíz del proyecto, donde está `pom.xml`.

## Nota sobre las cuentas de demostración

Las credenciales anteriores son únicamente para probar esta aplicación local. No las uses como contraseñas personales ni en otros servicios.
