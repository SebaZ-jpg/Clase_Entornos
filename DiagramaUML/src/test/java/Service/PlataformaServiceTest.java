package Service;

import model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.UsuarioRepository;
import repository.VideojuegoRepository;

import static org.junit.jupiter.api.Assertions.*;

class PlataformaServiceTest {

    private PlataformaService service;
    private PlanSuscripcion planBasic;
    private PlanSuscripcion planAdvanced;
    private PlanSuscripcion planPremium;

    @BeforeEach
    void setUp() {
        service = new PlataformaService(new UsuarioRepository(), new VideojuegoRepository());
        planBasic = new PlanSuscripcion(TipoPlan.BASIC);
        planAdvanced = new PlanSuscripcion(TipoPlan.ADVANCED);
        planPremium  = new PlanSuscripcion(TipoPlan.PREMIUM);
    }

    @Test
    @DisplayName("Registrar un usuario nuevo devuelve el usuario creado")
    void registrarUsuario() {
        Usuario u = service.registrarUsuario("Marcel Abad", "marcel@gmail.com", planBasic);
        assertNotNull(u);
        assertEquals("Marcel Abad", u.getNombre());
        assertEquals(TipoPlan.BASIC, u.getPlanActivo().getTipo());
    }

    @Test
    @DisplayName("Registrar dos usuarios con el mismo email lanza excepción")
    void registrarUsuario_emailDuplicado() {
        service.registrarUsuario("Alejandro Galazo", "alejandro@gmail.com", planBasic);
        assertThrows(IllegalArgumentException.class,
                () -> service.registrarUsuario("Otro", "alejandro@gmail.com", planBasic));
    }

    @Test
    @DisplayName("Buscar usuario por email existente devuelve el usuario")
    void buscarUsuarioPorEmail_encontrado() {
        service.registrarUsuario("Carlos", "carlos@gmail.com", planAdvanced);
        assertTrue(service.buscarUsuarioPorEmail("carlos@gmail.com").isPresent());
    }

    @Test
    @DisplayName("Buscar usuario por email inexistente devuelve vacío")
    void buscarUsuarioPorEmail() {
        assertTrue(service.buscarUsuarioPorEmail("patricio@gmail.com").isEmpty());
    }

    @Test
    @DisplayName("Cambiar plan actualiza correctamente el plan del usuario")
    void cambiarPlan_exitoso() {
        service.registrarUsuario("Juan Carlos", "juan@gmailcom", planBasic);
        service.cambiarPlan("juan@gmail.com", planPremium);
        assertEquals(TipoPlan.PREMIUM,
                service.buscarUsuarioPorEmail("juan@gmail.com").get().getPlanActivo().getTipo());
    }

    @Test
    @DisplayName("Iniciar partida válida incrementa el contador de partidas activas")
    void iniciarPartida_valida() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Luciana García", "lucia@gmail.com", planBasic);

        Partida p = service.iniciarPartida("lucia@gmail.com", "Minecraft");
        assertTrue(p.isActiva());
        assertEquals(1, service.buscarUsuarioPorEmail("lucia@gmail.com").get().getPartidasActivas());
    }

    @Test
    @DisplayName("Iniciar partida excediendo el límite del plan lanza excepción")
    void iniciarPartida_superaLimite() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Ana García", "ana@gmail.com", planBasic);
        service.iniciarPartida("ana@gmail.com", "Minecraft");

        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("ana@gmail.com", "Minecraft"));
    }

    @Test
    @DisplayName("Iniciar partida con juego no incluido en el plan lanza excepción")
    void iniciarPartida_juegoNoEnCatalogo() {
        service.añadirVideojuego("CyberPunk", "RPG", 100);
        service.registrarUsuario("Ana García", "ana@gmail.com", planBasic);
        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("ana@gmail.com", "CyberPunk"));
    }

    @Test
    @DisplayName("Iniciar partida con velocidad insuficiente lanza excepción")
    void iniciarPartida_velocidadInsuficiente() {
        Videojuego juegoHD = service.añadirVideojuego("UltraHD Game", "Acción", 200);
        planBasic.agregarVideojuego(juegoHD);
        service.registrarUsuario("Rodrigo Boto", "rodri@gmail.com", planBasic);
        assertThrows(IllegalStateException.class,
                () -> service.iniciarPartida("@gmail.com", "UltraHD Game"));
    }

    @Test
    @DisplayName("Finalizar partida activa decrementa el contador y la marca como inactiva")
    void finalizarPartida() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Williams Infanzon", "willy@gmail.com", planBasic);

        Partida p = service.iniciarPartida("willy@gmail.com", "Minecraft");
        service.finalizarPartida(p);

        assertFalse(p.isActiva());
        assertEquals(0, service.buscarUsuarioPorEmail("willy@gmail.com").get().getPartidasActivas());
    }

    @Test
    @DisplayName("Finalizar una partida ya finalizada lanza excepción")
    void finalizarPartida_yaFinalizada() {
        Videojuego juego = service.añadirVideojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        service.registrarUsuario("Oscar Renilla", "oscar@gmail.com", planBasic);

        Partida p = service.iniciarPartida("oscar@gmail.com", "Minecraft");
        service.finalizarPartida(p);
        assertThrows(IllegalStateException.class, () -> service.finalizarPartida(p));
    }

    @Test
    @DisplayName("puedeJugar devuelve true cuando el juego está en catálogo y la velocidad es suficiente")
    void puedeJugar_condicionesCorrectas() {
        Videojuego juego = new Videojuego("Minecraft", "Sandbox", 30);
        planBasic.agregarVideojuego(juego);
        assertTrue(planBasic.puedeJugar(juego));
    }

    @Test
    @DisplayName("puedeJugar devuelve false cuando la velocidad mínima supera la del plan")
    void puedeJugar_velocidadInsuficiente() {
        Videojuego juegoHD = new Videojuego("UltraHD", "Acción", 200);
        planBasic.agregarVideojuego(juegoHD);
        assertFalse(planBasic.puedeJugar(juegoHD));
    }

    @Test
    @DisplayName("puedeJugar devuelve false cuando el juego no está en el catálogo")
    void puedeJugar_noEnCatalogo() {
        Videojuego juego = new Videojuego("Minecraft", "Sandbox", 30);
        assertFalse(planBasic.puedeJugar(juego));
    }

    @Test
    @DisplayName("Listar usuarios devuelve todos los registrados")
    void listarUsuarios_devuelveTodos() {
        service.registrarUsuario("Ana García", "ana@gmail.com", planBasic);
        service.registrarUsuario("Luis Martínez", "luis@gmail.com", planPremium);
        assertEquals(2, service.listarUsuarios().size());
    }
}

