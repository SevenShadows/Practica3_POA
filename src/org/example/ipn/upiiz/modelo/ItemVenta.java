package org.example.ipn.upiiz.modelo;

public class ItemVenta {
    // Atributo de agregación y cantidad
    private Producto producto;
    private int cantidad;

    // Constructor sobrecargado (cantidad por defecto = 1)
    public ItemVenta(Producto producto) {
        this(producto, 1);
    }

    // Constructor sobrecargado con cantidad específica
    public ItemVenta(Producto producto, int cantidad) {
        this.producto = producto;
        this.cantidad = Math.max(1, cantidad);
    }

    // Métodos de atributos (Getters y Setters)
    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public int getCantidad() {
        return cantidad;
    }

    public void setCantidad(int cantidad) {
        this.cantidad = Math.max(1, cantidad);
    }

    // Métodos de comportamiento
    public void incrementarCantidad(int extra) {
        if (extra > 0) {
            this.cantidad += extra;
        }
    }

    public double calcularImporte() {
        return producto.getPrecio() * cantidad;
    }
}