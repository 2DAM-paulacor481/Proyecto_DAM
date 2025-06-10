package iesmm.pmdm.eventconnect.Model;

public class Usuario {

    private String correo, username;

    public Usuario(String correo, String username) {
        this.correo = correo;
        this.username = username;
    }

    public Usuario() {}

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
