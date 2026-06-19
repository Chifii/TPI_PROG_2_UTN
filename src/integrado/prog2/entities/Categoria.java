package integrado.prog2.entities;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Categoria extends BaseEntity {
    private String nombre;
    private String descripcion;
    private final List<Producto> productos;

    public Categoria(Long id, String nombre, String descripcion) {
        super(id);
        this.nombre = nombre;
        this.descripcion = descripcion;
        this.productos = new ArrayList<>();
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public List<Producto> getProductos() {
        return Collections.unmodifiableList(productos);
    }

    public void addProducto(Producto producto) {
        if (!productos.contains(producto)) {
            productos.add(producto);
        }
    }

    public void removeProducto(Producto producto) {
        productos.remove(producto);
    }

    @Override
    public String toString() {
        return "Categoria{" +
                "id=" + getId() +
                ", nombre='" + nombre + '\'' +
                ", descripcion='" + descripcion + '\'' +
                '}';
    }
}
