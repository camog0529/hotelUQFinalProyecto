package hoteluq.model.seguridad;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;

/** Cuenta local con contraseña almacenada como derivado PBKDF2 y sal aleatoria. */
public final class CuentaUsuario {
    private static final int ITERACIONES = 150_000;
    private static final int BITS_HASH = 256;
    private final String usuario;
    private final RolUsuario rol;
    private final String cedulaCliente;
    private final byte[] sal;
    private final byte[] hashContrasena;

    private CuentaUsuario(String usuario, RolUsuario rol, String cedulaCliente, byte[] sal, byte[] hashContrasena) {
        this.usuario = usuario.trim().toLowerCase(java.util.Locale.ROOT);
        this.rol = rol;
        this.cedulaCliente = cedulaCliente;
        this.sal = sal.clone();
        this.hashContrasena = hashContrasena.clone();
    }

    public static CuentaUsuario crear(String usuario, char[] contrasena, RolUsuario rol, String cedulaCliente) {
        if (usuario == null || usuario.isBlank() || contrasena == null || contrasena.length < 8 || rol == null) {
            throw new IllegalArgumentException("Usuario, rol y contraseña de al menos 8 caracteres son obligatorios.");
        }
        if (rol == RolUsuario.CLIENTE && (cedulaCliente == null || cedulaCliente.isBlank())) {
            throw new IllegalArgumentException("La cuenta de cliente debe asociarse a una cédula.");
        }
        byte[] sal = new byte[16];
        new SecureRandom().nextBytes(sal);
        return new CuentaUsuario(usuario, rol, cedulaCliente, sal, derivarHash(contrasena, sal));
    }

    public boolean verificarContrasena(char[] contrasena) {
        if (contrasena == null) return false;
        return MessageDigest.isEqual(hashContrasena, derivarHash(contrasena, sal));
    }

    private static byte[] derivarHash(char[] contrasena, byte[] sal) {
        PBEKeySpec spec = new PBEKeySpec(contrasena, sal, ITERACIONES, BITS_HASH);
        try {
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256")
                    .generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("No se pudo verificar la contraseña.", e);
        } finally {
            spec.clearPassword();
        }
    }

    public String getUsuario() { return usuario; }
    public RolUsuario getRol() { return rol; }
    public String getCedulaCliente() { return cedulaCliente; }
}
