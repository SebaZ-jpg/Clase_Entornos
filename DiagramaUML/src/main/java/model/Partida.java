package model;

public class Partida {
    private Usuario usuario;
    private Videojuego juego;
    private boolean activa;

    public Partida(Usuario usuario, Videojuego juego) {
        this.usuario = usuario;
        this.juego = juego;
        this.activa = true;
    }

    public void iniciar() { this.activa = true; }
    public void finalizar() { this.activa = false; }

    public Usuario getUsuario() { return usuario; }
    public Videojuego getJuego() { return juego; }
    public boolean isActiva() { return activa; }

    @Override
    public String toString() {
        return "java.model.Partida{usuario='" + usuario.getNombre() + "', juego='" +
                juego.getNombre() + "', activa=" + activa + "}";
    }
}