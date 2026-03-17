package model;

public enum TipoPlan {
    BASIC(50, 1),
    ADVANCED(150, 2),
    PREMIUM(500, 4);

    private final int velocidadMax;
    private final int maxPartidas;

    TipoPlan(int velocidadMax, int maxPartidas) {
        this.velocidadMax = velocidadMax;
        this.maxPartidas = maxPartidas;
    }

    public int getVelocidadMax() { return velocidadMax; }
    public int getMaxPartidas() { return maxPartidas; }
}