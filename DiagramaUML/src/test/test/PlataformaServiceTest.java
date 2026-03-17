package test;

import model.*;
import repository.UsuarioRepository;
import repository.VideojuegoRepository;
import Service.PlataformaService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class PlataformaServiceTest {
    private PlataformaService service;
    private PlanSuscripcion planBasic;
    private PlanSuscripcion planAdvanced;
    private PlanSuscripcion planPremium;

    @BeforeEach
    void setUp() {
        service = new PlataformaService(new UsuarioRepository(), new VideojuegoRepository());
        planBasic    = new PlanSuscripcion(TipoPlan.BASIC);
        planAdvanced = new PlanSuscripcion(TipoPlan.ADVANCED);
        planPremium  = new PlanSuscripcion(TipoPlan.PREMIUM);
    }

    @Test
    @DisplayName("Registrar un usuario nuevo devuelve el usuario creado")
    void registrarUsuario_exitoso() {
        Usuario u = service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        assertNotNull(u);
        assertEquals("Ana García", u.getNombre());
        assertEquals(TipoPlan.BASIC, u.getPlanActivo().getTipo());
    }

    @Test
    @DisplayName("Registrar dos usuarios con el mismo email lanza excepción")
    void registrarUsuario_emailDuplicado_lanzaExcepcion() {
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarUsuario("Otro", "ana@test.com", planBasic));
    }

    @Test
    @DisplayName("Buscar usuario por email existente devuelve el usuario")
    void buscarUsuarioPorEmail_encontrado() {
        service.registrarUsuario("Carlos", "carlos@test.com", planAdvanced);
        assertTrue(service.buscarUsuarioPorEmail("carlos@test.com").isPresent());
    }

    @Test
    @DisplayName("Buscar usuario por email inexistente devuelve vacío")
    void buscarUsuarioPorEmail_noEncontrado() {
        assertTrue(service.buscarUsuarioPorEmail("noexiste@test.com").isEmpty());
    }

    @Test
    @DisplayName("Cambiar plan actualiza correctamente el plan del usuario")
    void cambiarPlan_exitoso() {
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        service.cambiarPlan("ana@test.com", planPremium);
        assertEquals(TipoPlan.PREMIUM,
                service.buscarUsuarioPorEmail("ana@test.com").get().getPlanActivo().getTipo());
    }

    @Test
    @DisplayName("Iniciar partida válida incrementa el contador de partidas activas")
    void iniciarPartida_valida_incrementaContador() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);

        Partida p = service.iniciarPartida("ana@test.com", "Minecraft");
        assertTrue(p.isActiva());
        assertEquals(1, service.buscarUsuarioPorEmail("ana@test.com").get().getPartidasActivas());
    }

    @Test
    @DisplayName("Iniciar partida excediendo el límite del plan lanza excepción")
    void iniciarPartida_superaLimite_lanzaExcepcion() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        service.iniciarPartida("ana@test.com", "Minecraft");

        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("ana@test.com", "Minecraft"));
    }

    @Test
    @DisplayName("Iniciar partida con juego no incluido en el plan lanza excepción")
    void iniciarPartida_juegoNoEnCatalogo_lanzaExcepcion() {
        service.añadirVideojuego("CyberPunk", "RPG", 100);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("ana@test.com", "CyberPunk"));
    }

    @Test
    @DisplayName("Iniciar partida con velocidad insuficiente lanza excepción")
    void iniciarPartida_velocidadInsuficiente_lanzaExcepcion() {
        Videojuego juegoHD = service.añadirVideojuego("UltraHD Game", "Acción", 200);
        planBasic.agregarVideojuego(juegoHD);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("ana@test.com", "UltraHD Game"));
    }

    @Test
    @DisplayName("Finalizar partida activa decrementa el contador y la marca como inactiva")
    void finalizarPartida_exitosa() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);

        Partida p = service.iniciarPartida("ana@test.com", "Minecraft");
        service.finalizarPartida(p);

        assertFalse(p.isActiva());
        assertEquals(0, service.buscarUsuarioPorEmail("ana@test.com").get().getPartidasActivas());
    }

    @Test
    @DisplayName("Finalizar una partida ya finalizada lanza excepción")
    void finalizarPartida_yaFinalizada_lanzaExcepcion() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);

        Partida p = service.iniciarPartida("ana@test.com", "Minecraft");
        service.finalizarPartida(p);
        assertThrows(IllegalStateException.class, () -> service.finalizarPartida(p));
    }

    @Test
    @DisplayName("puedeJugar devuelve true cuando el juego está en catálogo y la velocidad es suficiente")
    void puedeJugar_condicionesCorrectas_devuelveTrue() {
        Videojuego juego = new Videojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        assertTrue(planBasic.puedeJugar(juego));
    }

    @Test
    @DisplayName("puedeJugar devuelve false cuando la velocidad mínima supera la del plan")
    void puedeJugar_velocidadInsuficiente_devuelveFalse() {
        Videojuego juegoHD = new Videojuego("UltraHD", "Acción", 200);
        planBasic.agregarVideojuego(juegoHD);
        assertFalse(planBasic.puedeJugar(juegoHD));
    }

    @Test
    @DisplayName("puedeJugar devuelve false cuando el juego no está en el catálogo")
    void puedeJugar_noEnCatalogo_devuelveFalse() {
        Videojuego juego = new Videojuego("Minecraft", "Sandbox", 30);
        assertFalse(planBasic.puedeJugar(juego));
    }

    @Test
    @DisplayName("Listar usuarios devuelve todos los registrados")
    void listarUsuarios_devuelveTodos() {
        service.registrarUsuario("Ana García", "ana@test.com", planBasic);
        service.registrarUsuario("Luis Martínez", "luis@test.com", planPremium);
        assertEquals(2, service.listarUsuarios().size());
    }
}
