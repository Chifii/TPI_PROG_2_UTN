package integrado.prog2.service;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.DuplicateEntityException;
import integrado.prog2.exception.EntityNotFoundException;
import integrado.prog2.exception.ValidationException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class FoodStoreService {
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final List<Categoria> categorias = new ArrayList<>();
    private final List<Producto> productos = new ArrayList<>();
    private final List<Usuario> usuarios = new ArrayList<>();
    private final List<Pedido> pedidos = new ArrayList<>();

    private long categoriaSequence = 1L;
    private long productoSequence = 1L;
    private long usuarioSequence = 1L;
    private long pedidoSequence = 1L;

    public List<Categoria> listarCategorias() {
        return categorias.stream()
                .filter(categoria -> !categoria.isEliminado())
                .sorted(Comparator.comparing(Categoria::getId))
                .toList();
    }

    public Categoria obtenerCategoriaActiva(Long id) {
        return categorias.stream()
                .filter(categoria -> categoria.getId().equals(id) && !categoria.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No existe una categoría activa con id " + id + '.'));
    }

    public Categoria crearCategoria(String nombre, String descripcion) {
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre de la categoría es obligatorio.");
        String descripcionNormalizada = validarTextoRequerido(descripcion,
                "La descripción de la categoría es obligatoria.");
        validarNombreCategoriaUnico(nombreNormalizado, null);

        Categoria categoria = new Categoria(categoriaSequence++, nombreNormalizado, descripcionNormalizada);
        categorias.add(categoria);
        return categoria;
    }

    public Categoria actualizarCategoria(Long id, String nombre, String descripcion) {
        Categoria categoria = obtenerCategoriaActiva(id);
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre de la categoría es obligatorio.");
        String descripcionNormalizada = validarTextoRequerido(descripcion,
                "La descripción de la categoría es obligatoria.");

        validarNombreCategoriaUnico(nombreNormalizado, categoria.getId());

        categoria.setNombre(nombreNormalizado);
        categoria.setDescripcion(descripcionNormalizada);
        return categoria;
    }

    public void eliminarCategoria(Long id) {
        Categoria categoria = obtenerCategoriaActiva(id);
        boolean tieneProductosActivos = productos.stream()
                .anyMatch(producto -> !producto.isEliminado() && producto.getCategoria().equals(categoria));
        if (tieneProductosActivos) {
            throw new ValidationException(
                    "No se puede eliminar la categoría porque tiene productos activos asociados.");
        }
        categoria.marcarEliminado();
    }

    public List<Producto> listarProductos() {
        return productos.stream()
                .filter(producto -> !producto.isEliminado())
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public List<Producto> listarProductosPorCategoria(Long categoriaId) {
        Categoria categoria = obtenerCategoriaActiva(categoriaId);
        return productos.stream()
                .filter(producto -> !producto.isEliminado())
                .filter(producto -> producto.getCategoria().equals(categoria))
                .sorted(Comparator.comparing(Producto::getId))
                .toList();
    }

    public Producto obtenerProductoActivo(Long id) {
        return productos.stream()
                .filter(producto -> producto.getId().equals(id) && !producto.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No existe un producto activo con id " + id + '.'));
    }

    public Producto crearProducto(String nombre,
                                  String descripcion,
                                  double precio,
                                  int stock,
                                  String imagen,
                                  boolean disponible,
                                  Long categoriaId) {
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre del producto es obligatorio.");
        String descripcionNormalizada = validarTextoRequerido(descripcion,
                "La descripción del producto es obligatoria.");
        String imagenNormalizada = validarTextoRequerido(imagen, "La imagen del producto es obligatoria.");
        validarPrecioYStock(precio, stock);
        Categoria categoria = obtenerCategoriaActiva(categoriaId);

        Producto producto = new Producto(productoSequence++, nombreNormalizado, precio, descripcionNormalizada,
                stock, imagenNormalizada, disponible && stock > 0, categoria);
        productos.add(producto);
        categoria.addProducto(producto);
        return producto;
    }

    public Producto actualizarProducto(Long id,
                                       String nombre,
                                       String descripcion,
                                       double precio,
                                       int stock,
                                       String imagen,
                                       boolean disponible,
                                       Long categoriaId) {
        Producto producto = obtenerProductoActivo(id);
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre del producto es obligatorio.");
        String descripcionNormalizada = validarTextoRequerido(descripcion,
                "La descripción del producto es obligatoria.");
        String imagenNormalizada = validarTextoRequerido(imagen, "La imagen del producto es obligatoria.");
        validarPrecioYStock(precio, stock);
        Categoria categoriaNueva = obtenerCategoriaActiva(categoriaId);
        Categoria categoriaAnterior = producto.getCategoria();

        producto.setNombre(nombreNormalizado);
        producto.setDescripcion(descripcionNormalizada);
        producto.setPrecio(precio);
        producto.setStock(stock);
        producto.setImagen(imagenNormalizada);
        producto.setDisponible(disponible && stock > 0);
        producto.setCategoria(categoriaNueva);

        if (!categoriaAnterior.equals(categoriaNueva)) {
            categoriaAnterior.removeProducto(producto);
            categoriaNueva.addProducto(producto);
        }
        return producto;
    }

    public void eliminarProducto(Long id) {
        Producto producto = obtenerProductoActivo(id);
        producto.marcarEliminado();
        producto.setDisponible(false);
    }

    public List<Usuario> listarUsuarios() {
        return usuarios.stream()
                .filter(usuario -> !usuario.isEliminado())
                .sorted(Comparator.comparing(Usuario::getId))
                .toList();
    }

    public Usuario obtenerUsuarioActivo(Long id) {
        return usuarios.stream()
                .filter(usuario -> usuario.getId().equals(id) && !usuario.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario activo con id " + id + '.'));
    }

    public Usuario crearUsuario(String nombre,
                                String apellido,
                                String mail,
                                String celular,
                                String contrasena,
                                Rol rol) {
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre del usuario es obligatorio.");
        String apellidoNormalizado = validarTextoRequerido(apellido, "El apellido del usuario es obligatorio.");
        String mailNormalizado = validarMail(mail, null);
        String celularNormalizado = validarTextoRequerido(celular, "El celular es obligatorio.");
        String contrasenaNormalizada = validarTextoRequerido(contrasena, "La contraseña es obligatoria.");
        Rol rolFinal = rol == null ? Rol.USUARIO : rol;

        Usuario usuario = new Usuario(usuarioSequence++, nombreNormalizado, apellidoNormalizado, mailNormalizado,
                celularNormalizado, contrasenaNormalizada, rolFinal);
        usuarios.add(usuario);
        return usuario;
    }

    public Usuario actualizarUsuario(Long id,
                                     String nombre,
                                     String apellido,
                                     String mail,
                                     String celular,
                                     String contrasena,
                                     Rol rol) {
        Usuario usuario = obtenerUsuarioActivo(id);
        String nombreNormalizado = validarTextoRequerido(nombre, "El nombre del usuario es obligatorio.");
        String apellidoNormalizado = validarTextoRequerido(apellido, "El apellido del usuario es obligatorio.");
        String mailNormalizado = validarMail(mail, usuario.getId());
        String celularNormalizado = validarTextoRequerido(celular, "El celular es obligatorio.");
        String contrasenaNormalizada = validarTextoRequerido(contrasena, "La contraseña es obligatoria.");
        Rol rolFinal = rol == null ? usuario.getRol() : rol;

        usuario.setNombre(nombreNormalizado);
        usuario.setApellido(apellidoNormalizado);
        usuario.setMail(mailNormalizado);
        usuario.setCelular(celularNormalizado);
        usuario.setContrasena(contrasenaNormalizada);
        usuario.setRol(rolFinal);
        return usuario;
    }

    public void eliminarUsuario(Long id) {
        Usuario usuario = obtenerUsuarioActivo(id);
        usuario.marcarEliminado();
    }

    public List<Pedido> listarPedidos() {
        return pedidos.stream()
                .filter(pedido -> !pedido.isEliminado())
                .sorted(Comparator.comparing(Pedido::getId))
                .toList();
    }

    public List<Pedido> listarPedidosPorUsuario(Long usuarioId) {
        Usuario usuario = obtenerUsuarioActivo(usuarioId);
        return pedidos.stream()
                .filter(pedido -> !pedido.isEliminado())
                .filter(pedido -> pedido.getUsuario().equals(usuario))
                .sorted(Comparator.comparing(Pedido::getId))
                .toList();
    }

    public Pedido obtenerPedidoActivo(Long id) {
        return pedidos.stream()
                .filter(pedido -> pedido.getId().equals(id) && !pedido.isEliminado())
                .findFirst()
                .orElseThrow(() -> new EntityNotFoundException("No existe un pedido activo con id " + id + '.'));
    }

    public Pedido crearPedido(Long usuarioId, FormaPago formaPago, List<DetalleRequest> detallesSolicitados) {
        Usuario usuario = obtenerUsuarioActivo(usuarioId);
        if (detallesSolicitados == null || detallesSolicitados.isEmpty()) {
            throw new ValidationException("El pedido debe tener al menos un detalle.");
        }

        Pedido pedido = new Pedido(pedidoSequence++, LocalDate.now(), Estado.PENDIENTE,
                formaPago == null ? FormaPago.EFECTIVO : formaPago, usuario);

        Map<Producto, ProductSnapshot> stockOriginal = new LinkedHashMap<>();

        try {
            for (DetalleRequest detalleRequest : detallesSolicitados) {
                Producto producto = obtenerProductoActivo(detalleRequest.productoId());
                stockOriginal.putIfAbsent(producto, new ProductSnapshot(producto.getStock(), producto.isDisponible()));
                pedido.addDetallePedido(detalleRequest.cantidad(), producto.getPrecio(), producto);
            }
        } catch (RuntimeException ex) {
            stockOriginal.forEach((producto, snapshot) -> {
                producto.setStock(snapshot.stock());
                producto.setDisponible(snapshot.disponible());
            });
            throw ex;
        }

        pedidos.add(pedido);
        usuario.addPedido(pedido);
        return pedido;
    }

    public Pedido actualizarPedido(Long id, Estado nuevoEstado, FormaPago nuevaFormaPago) {
        Pedido pedido = obtenerPedidoActivo(id);
        if (nuevoEstado != null) {
            pedido.setEstado(nuevoEstado);
        }
        if (nuevaFormaPago != null) {
            pedido.setFormaPago(nuevaFormaPago);
        }
        pedido.calcularTotal();
        return pedido;
    }

    public void eliminarPedido(Long id) {
        Pedido pedido = obtenerPedidoActivo(id);
        pedido.getDetalles().stream()
                .filter(detalle -> !detalle.isEliminado())
                .forEach(detalle -> detalle.getProducto().reponerStock(detalle.getCantidad()));
        pedido.marcarEliminado();
    }

    public record DetalleRequest(Long productoId, int cantidad) {}

    private record ProductSnapshot(int stock, boolean disponible) {}

    private void validarNombreCategoriaUnico(String nombre, Long categoriaIdActual) {
        boolean existe = categorias.stream()
                .anyMatch(categoria -> tieneMismoTexto(categoria.getNombre(), nombre)
                        && (categoriaIdActual == null || !categoria.getId().equals(categoriaIdActual)));
        if (existe) {
            throw new DuplicateEntityException("Ya existe una categoría con el nombre '" + nombre + "'.");
        }
    }

    private void validarPrecioYStock(double precio, int stock) {
        if (precio < 0) {
            throw new ValidationException("El precio no puede ser menor a cero.");
        }
        if (stock < 0) {
            throw new ValidationException("El stock no puede ser menor a cero.");
        }
    }

    private String validarMail(String mail, Long usuarioIdActual) {
        String mailNormalizado = validarTextoRequerido(mail, "El mail es obligatorio.").toLowerCase(Locale.ROOT);
        if (!EMAIL_PATTERN.matcher(mailNormalizado).matches()) {
            throw new ValidationException("El formato del mail no es válido.");
        }

        boolean existe = usuarios.stream()
                .anyMatch(usuario -> tieneMismoTexto(usuario.getMail(), mailNormalizado)
                        && (usuarioIdActual == null || !usuario.getId().equals(usuarioIdActual)));
        if (existe) {
            throw new DuplicateEntityException("Ya existe un usuario con el mail '" + mailNormalizado + "'.");
        }
        return mailNormalizado;
    }

    private String validarTextoRequerido(String texto, String mensajeError) {
        if (texto == null || texto.trim().isEmpty()) {
            throw new ValidationException(mensajeError);
        }
        return texto.trim();
    }

    private boolean tieneMismoTexto(String textoA, String textoB) {
        return Optional.ofNullable(textoA).orElse("").trim()
                .equalsIgnoreCase(Optional.ofNullable(textoB).orElse("").trim());
    }
}
