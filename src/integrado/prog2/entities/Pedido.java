package integrado.prog2.entities;

import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.exception.StockInsufficientException;
import integrado.prog2.exception.ValidationException;
import integrado.prog2.interfaces.Calculable;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class Pedido extends BaseEntity implements Calculable {
    private LocalDate fecha;
    private Estado estado;
    private double total;
    private FormaPago formaPago;
    private final Usuario usuario;
    private final List<DetallePedido> detalles;

    public Pedido(Long id, LocalDate fecha, Estado estado, FormaPago formaPago, Usuario usuario) {
        super(id);
        if (usuario == null) {
            throw new ValidationException("El pedido debe tener un usuario asociado.");
        }
        this.fecha = fecha;
        this.estado = estado;
        this.formaPago = formaPago;
        this.usuario = usuario;
        this.detalles = new ArrayList<>();
        this.total = 0.0;
    }

    public LocalDate getFecha() {
        return fecha;
    }

    public void setFecha(LocalDate fecha) {
        this.fecha = fecha;
    }

    public Estado getEstado() {
        return estado;
    }

    public void setEstado(Estado estado) {
        this.estado = estado;
    }

    public double getTotal() {
        return total;
    }

    public FormaPago getFormaPago() {
        return formaPago;
    }

    public void setFormaPago(FormaPago formaPago) {
        this.formaPago = formaPago;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public List<DetallePedido> getDetalles() {
        return Collections.unmodifiableList(detalles);
    }

    public void addDetallePedido(int cantidad, Double precioUnitario, Producto producto) {
        if (producto == null || producto.isEliminado()) {
            throw new ValidationException("El producto del detalle no es válido.");
        }
        if (!producto.isDisponible()) {
            throw new ValidationException("El producto seleccionado no está disponible.");
        }
        if (cantidad <= 0) {
            throw new ValidationException("La cantidad del detalle debe ser mayor a cero.");
        }
        if (precioUnitario == null || precioUnitario < 0) {
            throw new ValidationException("El precio unitario del detalle no es válido.");
        }
        if (producto.getStock() < cantidad) {
            throw new StockInsufficientException(
                    "Stock insuficiente para '" + producto.getNombre() + "'. Disponible: " + producto.getStock());
        }

        Optional<DetallePedido> detalleExistente = findeDetallePedidoByProducto(producto);
        producto.descontarStock(cantidad);

        if (detalleExistente.isPresent()) {
            detalleExistente.get().incrementarCantidad(cantidad, precioUnitario);
        } else {
            double subtotal = redondear(cantidad * precioUnitario);
            detalles.add(new DetallePedido(cantidad, subtotal, producto));
        }

        calcularTotal();
    }

    public Optional<DetallePedido> findeDetallePedidoByProducto(Producto producto) {
        return detalles.stream()
                .filter(detalle -> !detalle.isEliminado())
                .filter(detalle -> detalle.getProducto().equals(producto))
                .findFirst();
    }

    public void deleteDetallePedidoByProducto(Producto producto) {
        DetallePedido detalle = findeDetallePedidoByProducto(producto)
                .orElseThrow(() -> new ValidationException("El producto no existe dentro del pedido."));
        producto.reponerStock(detalle.getCantidad());
        detalle.marcarEliminado();
        calcularTotal();
    }

    @Override
    public void calcularTotal() {
        this.total = redondear(detalles.stream()
                .filter(detalle -> !detalle.isEliminado())
                .mapToDouble(DetallePedido::getSubtotal)
                .sum());
    }

    public int getCantidadDeItemsActivos() {
        return (int) detalles.stream().filter(detalle -> !detalle.isEliminado()).count();
    }

    private double redondear(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + getId() +
                ", fecha=" + fecha +
                ", estado=" + estado +
                ", formaPago=" + formaPago +
                ", total=" + total +
                ", usuario='" + usuario.getNombre() + ' ' + usuario.getApellido() + '\'' +
                '}';
    }
}
