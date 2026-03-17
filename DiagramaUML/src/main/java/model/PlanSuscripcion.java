package model;

import java.util.ArrayList;
import java.util.List;

public class PlanSuscripcion {
    private String nombre;
    private TipoPlan tipo;
    private List<Videojuego> catalogo;

    public PlanSuscripcion(TipoPlan tipo) {
        this.tipo = tipo;
        this.nombre = tipo.name();
        this.catalogo = new ArrayList<>();
    }

    public boolean puedeJugar(Videojuego videojuego) {
        return catalogo.contains(videojuego) &&
                tipo.getVelocidadMax() >= videojuego.getVelocidadMinima();
    }

    public void agregarVideojuego(Videojuego videojuego) {
        if (!catalogo.contains(videojuego)) {
            catalogo.add(videojuego);
        }
    }

    public String getNombre() { return nombre; }
    public TipoPlan getTipo() { return tipo; }
    public int getVelocidadMax() { return tipo.getVelocidadMax(); }
    public int getMaxPartidas() { return tipo.getMaxPartidas(); }
    public List<Videojuego> getCatalogo() { return new ArrayList<>(catalogo); }

    @Override
    public String toString() {
        return "java.model.PlanSuscripcion{tipo=" + tipo + ", velocidadMax=" + getVelocidadMax() +
                ", maxPartidas=" + getMaxPartidas() + "}";
    }
}
