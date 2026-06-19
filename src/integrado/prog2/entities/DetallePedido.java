package integrado.prog2.entities;

import java.util.concurrent.atomic.AtomicLong;

public class DetallePedido extends BaseEntity {
    private static final AtomicLong SEQUENCE = new AtomicLong(1);

    private int cantidad;
    private double subtotal;
    private final Producto producto;

    public DetallePedido(int cantidad, double subtotal, Producto producto) {
        super(SEQUENCE.getAndIncrement());
        this.cantidad = cantidad;
        this.subtotal = subtotal;
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public double getSubtotal() {
        return subtotal;
    }

    public Producto getProducto() {
        return producto;
    }

    public void incrementarCantidad(int cantidadExtra, double precioUnitario) {
        this.cantidad += cantidadExtra;
        this.subtotal = redondear(this.subtotal + (cantidadExtra * precioUnitario));
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "DetallePedido{" +
                "id=" + getId() +
                ", producto='" + producto.getNombre() + '\'' +
                ", cantidad=" + cantidad +
                ", subtotal=" + subtotal +
                '}';
    }
}
