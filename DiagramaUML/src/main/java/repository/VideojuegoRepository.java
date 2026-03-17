package repository;

import model.Videojuego;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class VideojuegoRepository {
    private final List<Videojuego> videojuegos = new ArrayList<>();

    public void guardar(Videojuego videojuego) { videojuegos.add(videojuego); }

    public Optional<Videojuego> buscarPorNombre(String nombre) {
        return videojuegos.stream()
                .filter(v -> v.getNombre().equalsIgnoreCase(nombre))
                .findFirst();
    }

    public List<Videojuego> listarTodos() { return new ArrayList<>(videojuegos); }
}
