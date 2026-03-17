package controller;
import Service.PlataformaService;
import model.PlanSuscripcion;
import model.Videojuego;
import model.Partida;
import model.Usuario;

import java.util.List;
import java.util.Optional;

public class PlataformaController {
    private final PlataformaService service;

    public PlataformaController(PlataformaService service) {
        this.service = service;
    }

    public Usuario registrarUsuario(String nombre, String email, PlanSuscripcion plan) {
        return service.registrarUsuario(nombre, email, plan);
    }

    public Optional<Usuario> buscarUsuario(String email) {
        return service.buscarUsuarioPorEmail(email);
    }

    public List<Usuario> listarUsuarios() {
        return service.listarUsuarios();
    }

    public void cambiarPlan(String email, PlanSuscripcion nuevoPlan) {
        service.cambiarPlan(email, nuevoPlan);
    }

    public Videojuego añadirVideojuego(String nombre, String genero, int velocidadMinima) {
        return service.añadirVideojuego(nombre, genero, velocidadMinima);
    }

    public Optional<Videojuego> buscarVideojuego(String nombre) {
        return service.buscarVideojuegoPorNombre(nombre);
    }

    public Partida iniciarPartida(String email, String nombreJuego) {
        return service.iniciarPartida(email, nombreJuego);
    }

    public void finalizarPartida(Partida partida) {
        service.finalizarPartida(partida);
    }
}
