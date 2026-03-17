package Service;

import model.Partida;
import model.PlanSuscripcion;
import model.Usuario;
import model.Videojuego;
import repository.UsuarioRepository;
import repository.VideojuegoRepository;

import java.util.List;
import java.util.Optional;

public class PlataformaService {
    private final UsuarioRepository usuarioRepository;
    private final VideojuegoRepository videojuegoRepository;

    public PlataformaService(UsuarioRepository usuarioRepository,
                             VideojuegoRepository videojuegoRepository) {
        this.usuarioRepository = usuarioRepository;
        this.videojuegoRepository = videojuegoRepository;
    }

    public Usuario registrarUsuario(String nombre, String email, PlanSuscripcion plan) {
        if (usuarioRepository.existePorEmail(email)) {
            throw new IllegalArgumentException("Ya existe un usuario con el email: " + email);
        }
        Usuario usuario = new Usuario(nombre, email, plan);
        usuarioRepository.guardar(usuario);
        return usuario;
    }

    public Optional<Usuario> buscarUsuarioPorEmail(String email) {
        return usuarioRepository.buscarPorEmail(email);
    }

    public List<Usuario> listarUsuarios() {
        return usuarioRepository.listarTodos();
    }

    public void cambiarPlan(String email, PlanSuscripcion nuevoPlan) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("java.model.Usuario no encontrado: " + email));
        usuario.cambiarPlan(nuevoPlan);
    }

    public Videojuego añadirVideojuego(String nombre, String genero, int velocidadMinima) {
        Videojuego videojuego = new Videojuego(nombre, genero, velocidadMinima);
        videojuegoRepository.guardar(videojuego);
        return videojuego;
    }

    public Optional<Videojuego> buscarVideojuegoPorNombre(String nombre) {
        return videojuegoRepository.buscarPorNombre(nombre);
    }

    public Partida iniciarPartida(String email, String nombreJuego) {
        Usuario usuario = usuarioRepository.buscarPorEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("java.model.Usuario no encontrado: " + email));

        Videojuego videojuego = videojuegoRepository.buscarPorNombre(nombreJuego)
                .orElseThrow(() -> new IllegalArgumentException("java.model.Videojuego no encontrado: " + nombreJuego));

        PlanSuscripcion plan = usuario.getPlanActivo();

        if (usuario.getPartidasActivas() >= plan.getMaxPartidas()) {
            throw new IllegalStateException("Has alcanzado el máximo de partidas simultáneas para tu plan " + plan.getTipo());
        }

        if (!plan.puedeJugar(videojuego)) {
            throw new IllegalStateException("Tu plan '" + plan.getTipo() +
                    "' no permite jugar a '" + nombreJuego + "'.");
        }

        usuario.iniciarPartida();
        return new Partida(usuario, videojuego);
    }

    public void finalizarPartida(Partida partida) {
        if (!partida.isActiva()) {
            throw new IllegalStateException("La partida ya estaba finalizada.");
        }
        partida.finalizar();
        partida.getUsuario().finalizarPartida();
    }
}
