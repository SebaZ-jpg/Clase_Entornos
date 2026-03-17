package model;

public class Usuario {
    private String nombre;
    private String email;
    private PlanSuscripcion planActivo;
    private int partidasActivas;

    public Usuario(String nombre, String email, PlanSuscripcion planActivo) {
        this.nombre = nombre;
        this.email = email;
        this.planActivo = planActivo;
        this.partidasActivas = 0;
    }

    public void iniciarPartida() { this.partidasActivas++; }

    public void finalizarPartida() {
        if (this.partidasActivas > 0) this.partidasActivas--;
    }

    public void cambiarPlan(PlanSuscripcion nuevoPlan) { this.planActivo = nuevoPlan; }

    public int consultarVelocidad() { return planActivo.getVelocidadMax(); }

    public String getNombre() { return nombre; }
    public String getEmail() { return email; }
    public PlanSuscripcion getPlanActivo() { return planActivo; }
    public int getPartidasActivas() { return partidasActivas; }

    @Override
    public String toString() {
        return "java.model.Usuario{nombre='" + nombre + "', email='" + email +
                "', plan=" + planActivo.getTipo() +
                ", partidasActivas=" + partidasActivas + "}";
    }
}