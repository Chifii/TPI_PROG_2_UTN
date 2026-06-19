# Food Store — Programación 2

## Descripción
Desarrollamos **Food Store**, una aplicación de consola en **Java 21** para gestionar categorías, productos, usuarios y pedidos.

El trabajo fue realizado siguiendo la consigna de **Programación 2**, por lo que el sistema:
- funciona completamente por consola,
- almacena la información en memoria usando colecciones,
- aplica Programación Orientada a Objetos,
- utiliza bajas lógicas,
- valida entradas y reglas del dominio,
- organiza el código por responsabilidades.

## Funcionalidades principales
El sistema permite realizar operaciones CRUD sobre:
- **Categorías**
- **Productos**
- **Usuarios**
- **Pedidos**

Además, incorpora:
- validación de datos obligatorios,
- control de stock,
- unicidad de mail en usuarios,
- cálculo automático del total del pedido,
- manejo de excepciones propias para errores del dominio.

## Estructura del proyecto
- `src/integrado/prog2/entities/` → entidades del dominio
- `src/integrado/prog2/service/` → lógica de negocio
- `src/integrado/prog2/app/` → menús y flujo de consola
- `src/integrado/prog2/util/` → lectura y validación de entradas
- `src/integrado/prog2/exception/` → excepciones personalizadas
- `src/integrado/prog2/enums/` → enumeraciones del sistema
- `src/integrado/prog2/interfaces/` → interfaces compartidas

## Requisitos
- **Java 21**
- Terminal o consola para ejecutar la aplicación

## Consideraciones
- El sistema **no usa base de datos** en esta materia.
- Los datos se mantienen solo durante la ejecución del programa.
- Las bajas se resuelven con `eliminado = true`, para conservar el historial en memoria.

## Integrantes
- `Franco Vencato`
- `Esteban Ferreyra`
