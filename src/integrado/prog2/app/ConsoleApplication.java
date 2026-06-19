package integrado.prog2.app;

import integrado.prog2.entities.Categoria;
import integrado.prog2.entities.Pedido;
import integrado.prog2.entities.Producto;
import integrado.prog2.entities.Usuario;
import integrado.prog2.enums.Estado;
import integrado.prog2.enums.FormaPago;
import integrado.prog2.enums.Rol;
import integrado.prog2.exception.BusinessException;
import integrado.prog2.service.FoodStoreService;
import integrado.prog2.util.InputReader;

import java.util.ArrayList;
import java.util.List;

public class ConsoleApplication {
    private final FoodStoreService service;
    private final InputReader input;

    public ConsoleApplication(FoodStoreService service, InputReader input) {
        this.service = service;
        this.input = input;
    }

    public void start() {
        boolean running = true;
        while (running) {
            System.out.println("\n=== SISTEMA DE PEDIDOS (FOOD STORE) ===");
            System.out.println("1. Categorías");
            System.out.println("2. Productos");
            System.out.println("3. Usuarios");
            System.out.println("4. Pedidos");
            System.out.println("0. Salir");

            int option = input.readMenuOption("Seleccione: ", 0, 4);
            try {
                switch (option) {
                    case 1 -> menuCategorias();
                    case 2 -> menuProductos();
                    case 3 -> menuUsuarios();
                    case 4 -> menuPedidos();
                    case 0 -> running = false;
                    default -> System.out.println("Opción inválida.");
                }
            } catch (BusinessException ex) {
                System.out.println("[ERROR] " + ex.getMessage());
            } catch (IllegalArgumentException ex) {
                System.out.println("[ERROR] " + mensajeAmigable(ex));
            }
        }
        System.out.println("¡Gracias por usar Food Store!");
    }

    private void menuCategorias() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Categorías ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            int option = input.readMenuOption("Seleccione: ", 0, 4);
            switch (option) {
                case 1 -> listarCategorias();
                case 2 -> crearCategoria();
                case 3 -> editarCategoria();
                case 4 -> eliminarCategoria();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuProductos() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Productos ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            int option = input.readMenuOption("Seleccione: ", 0, 4);
            switch (option) {
                case 1 -> listarProductos();
                case 2 -> crearProducto();
                case 3 -> editarProducto();
                case 4 -> eliminarProducto();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuUsuarios() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Usuarios ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Editar");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            int option = input.readMenuOption("Seleccione: ", 0, 4);
            switch (option) {
                case 1 -> listarUsuarios();
                case 2 -> crearUsuario();
                case 3 -> editarUsuario();
                case 4 -> eliminarUsuario();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void menuPedidos() {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Pedidos ---");
            System.out.println("1. Listar");
            System.out.println("2. Crear");
            System.out.println("3. Actualizar estado / forma de pago");
            System.out.println("4. Eliminar");
            System.out.println("0. Volver");

            int option = input.readMenuOption("Seleccione: ", 0, 4);
            switch (option) {
                case 1 -> listarPedidos();
                case 2 -> crearPedido();
                case 3 -> actualizarPedido();
                case 4 -> eliminarPedido();
                case 0 -> back = true;
                default -> System.out.println("Opción inválida.");
            }
        }
    }

    private void listarCategorias() {
        List<Categoria> categorias = service.listarCategorias();
        if (categorias.isEmpty()) {
            System.out.println("No hay categorías cargadas.");
            return;
        }
        categorias.forEach(System.out::println);
    }

    private void crearCategoria() {
        String nombre = input.readRequiredString("Nombre: ");
        String descripcion = input.readRequiredString("Descripción: ");
        Categoria categoria = service.crearCategoria(nombre, descripcion);
        System.out.println("Categoría creada con id " + categoria.getId());
    }

    private void editarCategoria() {
        validarCategoriasDisponibles();
        listarCategorias();
        Long id = input.readLong("Id de la categoría a editar: ");
        Categoria categoria = service.obtenerCategoriaActiva(id);
        String nombre = input.readOptionalString("Nombre [" + categoria.getNombre() + "]: ");
        String descripcion = input.readOptionalString("Descripción [" + categoria.getDescripcion() + "]: ");

        Categoria actualizada = service.actualizarCategoria(
                id,
                nombre != null ? nombre : categoria.getNombre(),
                descripcion != null ? descripcion : categoria.getDescripcion()
        );
        System.out.println("Categoría actualizada: " + actualizada);
    }

    private void eliminarCategoria() {
        validarCategoriasDisponibles();
        listarCategorias();
        Long id = input.readLong("Id de la categoría a eliminar: ");
        if (input.readBoolean("¿Confirmás la baja lógica? (S/N): ")) {
            service.eliminarCategoria(id);
            System.out.println("Categoría eliminada lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listarProductos() {
        if (service.listarProductos().isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        boolean filtrar = input.readBoolean("¿Querés filtrar por categoría? (S/N): ");
        List<Producto> productos = filtrar
                ? service.listarProductosPorCategoria(input.readLong("Id de la categoría: "))
                : service.listarProductos();

        if (productos.isEmpty()) {
            System.out.println("No hay productos para el criterio seleccionado.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private void crearProducto() {
        validarCategoriasDisponibles();
        listarCategorias();
        String nombre = input.readRequiredString("Nombre: ");
        String descripcion = input.readRequiredString("Descripción: ");
        double precio = input.readDouble("Precio: ");
        int stock = input.readInt("Stock: ");
        String imagen = input.readRequiredString("Imagen/URL: ");
        boolean disponible = input.readBoolean("¿Está disponible? (S/N): ");
        Long categoriaId = input.readLong("Id de la categoría: ");

        Producto producto = service.crearProducto(nombre, descripcion, precio, stock, imagen, disponible, categoriaId);
        System.out.println("Producto creado con id " + producto.getId());
    }

    private void editarProducto() {
        validarProductosDisponibles();
        listarProductosSinFiltro();
        Long id = input.readLong("Id del producto a editar: ");
        Producto producto = service.obtenerProductoActivo(id);
        listarCategorias();

        String nombre = input.readOptionalString("Nombre [" + producto.getNombre() + "]: ");
        String descripcion = input.readOptionalString("Descripción [" + producto.getDescripcion() + "]: ");
        String precioRaw = input.readOptionalString("Precio [" + producto.getPrecio() + "]: ");
        String stockRaw = input.readOptionalString("Stock [" + producto.getStock() + "]: ");
        String imagen = input.readOptionalString("Imagen/URL [" + producto.getImagen() + "]: ");
        String disponibleRaw = input.readOptionalString("Disponible (S/N) [" + (producto.isDisponible() ? "S" : "N") + "]: ");
        String categoriaRaw = input.readOptionalString("Id categoría [" + producto.getCategoria().getId() + "]: ");

        Producto actualizado = service.actualizarProducto(
                id,
                nombre != null ? nombre : producto.getNombre(),
                descripcion != null ? descripcion : producto.getDescripcion(),
                precioRaw != null ? parseDouble(precioRaw, "precio") : producto.getPrecio(),
                stockRaw != null ? parseInt(stockRaw, "stock") : producto.getStock(),
                imagen != null ? imagen : producto.getImagen(),
                disponibleRaw != null ? parseBoolean(disponibleRaw) : producto.isDisponible(),
                categoriaRaw != null ? parseLong(categoriaRaw, "categoría") : producto.getCategoria().getId()
        );
        System.out.println("Producto actualizado: " + actualizado);
    }

    private void eliminarProducto() {
        validarProductosDisponibles();
        listarProductosSinFiltro();
        Long id = input.readLong("Id del producto a eliminar: ");
        if (input.readBoolean("¿Confirmás la baja lógica? (S/N): ")) {
            service.eliminarProducto(id);
            System.out.println("Producto eliminado lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listarUsuarios() {
        List<Usuario> usuarios = service.listarUsuarios();
        if (usuarios.isEmpty()) {
            System.out.println("No hay usuarios cargados.");
            return;
        }
        usuarios.forEach(System.out::println);
    }

    private void crearUsuario() {
        String nombre = input.readRequiredString("Nombre: ");
        String apellido = input.readRequiredString("Apellido: ");
        String mail = input.readRequiredString("Mail: ");
        String celular = input.readRequiredString("Celular: ");
        String contrasena = input.readRequiredString("Contraseña: ");
        Rol rol = input.readEnum("Rol:", Rol.values());

        Usuario usuario = service.crearUsuario(nombre, apellido, mail, celular, contrasena, rol);
        System.out.println("Usuario creado con id " + usuario.getId());
    }

    private void editarUsuario() {
        validarUsuariosDisponibles();
        listarUsuarios();
        Long id = input.readLong("Id del usuario a editar: ");
        Usuario usuario = service.obtenerUsuarioActivo(id);

        String nombre = input.readOptionalString("Nombre [" + usuario.getNombre() + "]: ");
        String apellido = input.readOptionalString("Apellido [" + usuario.getApellido() + "]: ");
        String mail = input.readOptionalString("Mail [" + usuario.getMail() + "]: ");
        String celular = input.readOptionalString("Celular [" + usuario.getCelular() + "]: ");
        String contrasena = input.readOptionalString("Contraseña [oculta]: ");
        String rolRaw = input.readOptionalString("Rol (ADMIN/USUARIO) [" + usuario.getRol() + "]: ");

        Usuario actualizado = service.actualizarUsuario(
                id,
                nombre != null ? nombre : usuario.getNombre(),
                apellido != null ? apellido : usuario.getApellido(),
                mail != null ? mail : usuario.getMail(),
                celular != null ? celular : usuario.getCelular(),
                contrasena != null ? contrasena : usuario.getContrasena(),
                rolRaw != null ? parseEnum(rolRaw, Rol.class, "rol") : usuario.getRol()
        );
        System.out.println("Usuario actualizado: " + actualizado);
    }

    private void eliminarUsuario() {
        validarUsuariosDisponibles();
        listarUsuarios();
        Long id = input.readLong("Id del usuario a eliminar: ");
        if (input.readBoolean("¿Confirmás la baja lógica? (S/N): ")) {
            service.eliminarUsuario(id);
            System.out.println("Usuario eliminado lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listarPedidos() {
        if (service.listarPedidos().isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        boolean filtrar = input.readBoolean("¿Querés filtrar por usuario? (S/N): ");
        List<Pedido> pedidos = filtrar
                ? service.listarPedidosPorUsuario(input.readLong("Id del usuario: "))
                : service.listarPedidos();

        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos para el criterio seleccionado.");
            return;
        }
        for (Pedido pedido : pedidos) {
            System.out.println(pedido);
            pedido.getDetalles().stream()
                    .filter(detalle -> !detalle.isEliminado())
                    .forEach(detalle -> System.out.println("   - " + detalle));
        }
    }

    private void crearPedido() {
        validarUsuariosDisponibles();
        validarProductosDisponibles();
        listarUsuarios();
        Long usuarioId = input.readLong("Id del usuario: ");
        FormaPago formaPago = input.readEnum("Forma de pago:", FormaPago.values());

        List<FoodStoreService.DetalleRequest> detalles = new ArrayList<>();
        boolean agregarOtro;
        do {
            listarProductosSinFiltro();
            Long productoId = input.readLong("Id del producto: ");
            int cantidad = input.readInt("Cantidad: ");
            detalles.add(new FoodStoreService.DetalleRequest(productoId, cantidad));
            agregarOtro = input.readBoolean("¿Querés agregar otro detalle? (S/N): ");
        } while (agregarOtro);

        Pedido pedido = service.crearPedido(usuarioId, formaPago, detalles);
        System.out.println("Pedido creado con id " + pedido.getId() + " y total " + pedido.getTotal());
    }

    private void actualizarPedido() {
        validarPedidosDisponibles();
        listarPedidosSinFiltro();
        Long id = input.readLong("Id del pedido a actualizar: ");
        Pedido pedido = service.obtenerPedidoActivo(id);

        String estadoRaw = input.readOptionalString("Estado (PENDIENTE/CONFIRMADO/TERMINADO/CANCELADO) [" + pedido.getEstado() + "]: ");
        String formaPagoRaw = input.readOptionalString("Forma de pago (TARJETA/TRANSFERENCIA/EFECTIVO) [" + pedido.getFormaPago() + "]: ");

        Pedido actualizado = service.actualizarPedido(
                id,
                estadoRaw != null ? parseEnum(estadoRaw, Estado.class, "estado") : pedido.getEstado(),
                formaPagoRaw != null ? parseEnum(formaPagoRaw, FormaPago.class, "forma de pago") : pedido.getFormaPago()
        );
        System.out.println("Pedido actualizado: " + actualizado);
    }

    private void eliminarPedido() {
        validarPedidosDisponibles();
        listarPedidosSinFiltro();
        Long id = input.readLong("Id del pedido a eliminar: ");
        if (input.readBoolean("¿Confirmás la baja lógica? (S/N): ")) {
            service.eliminarPedido(id);
            System.out.println("Pedido eliminado lógicamente.");
        } else {
            System.out.println("Operación cancelada.");
        }
    }

    private void listarProductosSinFiltro() {
        List<Producto> productos = service.listarProductos();
        if (productos.isEmpty()) {
            System.out.println("No hay productos cargados.");
            return;
        }
        productos.forEach(System.out::println);
    }

    private void listarPedidosSinFiltro() {
        List<Pedido> pedidos = service.listarPedidos();
        if (pedidos.isEmpty()) {
            System.out.println("No hay pedidos cargados.");
            return;
        }
        pedidos.forEach(System.out::println);
    }

    private void validarCategoriasDisponibles() {
        if (service.listarCategorias().isEmpty()) {
            throw new BusinessException("Primero tenés que crear al menos una categoría.");
        }
    }

    private void validarUsuariosDisponibles() {
        if (service.listarUsuarios().isEmpty()) {
            throw new BusinessException("Primero tenés que crear al menos un usuario.");
        }
    }

    private void validarProductosDisponibles() {
        if (service.listarProductos().isEmpty()) {
            throw new BusinessException("Primero tenés que crear al menos un producto.");
        }
    }

    private void validarPedidosDisponibles() {
        if (service.listarPedidos().isEmpty()) {
            throw new BusinessException("Todavía no hay pedidos cargados.");
        }
    }

    private boolean parseBoolean(String value) {
        String normalized = value.trim().toLowerCase();
        if (normalized.equals("s") || normalized.equals("si") || normalized.equals("sí")
                || normalized.equals("y") || normalized.equals("yes") || normalized.equals("true")) {
            return true;
        }
        if (normalized.equals("n") || normalized.equals("no") || normalized.equals("false")) {
            return false;
        }
        throw new BusinessException("Valor booleano inválido. Usá S/N.");
    }

    private double parseDouble(String raw, String campo) {
        try {
            return Double.parseDouble(raw.replace(',', '.'));
        } catch (NumberFormatException ex) {
            throw new BusinessException("El " + campo + " debe ser un número decimal válido.");
        }
    }

    private int parseInt(String raw, String campo) {
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException ex) {
            throw new BusinessException("El " + campo + " debe ser un número entero válido.");
        }
    }

    private long parseLong(String raw, String campo) {
        try {
            return Long.parseLong(raw);
        } catch (NumberFormatException ex) {
            throw new BusinessException("El id de " + campo + " debe ser un número entero válido.");
        }
    }

    private <E extends Enum<E>> E parseEnum(String raw, Class<E> enumType, String campo) {
        try {
            return Enum.valueOf(enumType, raw.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new BusinessException("Valor inválido para " + campo + ".");
        }
    }

    private String mensajeAmigable(IllegalArgumentException ex) {
        if (ex.getMessage() == null || ex.getMessage().isBlank()) {
            return "Entrada inválida.";
        }
        return ex.getMessage();
    }
}
