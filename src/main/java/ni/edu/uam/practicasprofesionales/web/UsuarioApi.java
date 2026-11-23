package ni.edu.uam.practicasprofesionales.web;

/** Datos de usuario recibidos de JSONPlaceholder (servicio externo de demostración). */
public record UsuarioApi(int id, String nombre, String usuario, String correo, String ciudad) { }
