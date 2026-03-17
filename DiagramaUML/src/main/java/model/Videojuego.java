package model;

public class Videojuego {
    private String nombre;
    private String genero;
    private int velocidadMinima;

    public Videojuego(String nombre, String genero, int velocidadMinima) {
        this.nombre = nombre;
        this.genero = genero;
        this.velocidadMinima = velocidadMinima;
    }

    public String getNombre() { return nombre; }
    public String getGenero() { return genero; }
    public int getVelocidadMinima() { return velocidadMinima; }

    @Override
    public String toString() {
        return "java.model.Videojuego{nombre='" + nombre + "', genero='" + genero +
                "', velocidadMinima=" + velocidadMinima + "}";
    }
}
