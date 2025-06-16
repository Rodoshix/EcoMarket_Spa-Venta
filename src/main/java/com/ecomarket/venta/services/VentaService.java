package com.ecomarket.venta.services;

import com.ecomarket.venta.dto.ActualizarStockDTO;
import com.ecomarket.venta.dto.NotificacionDTO;
import com.ecomarket.venta.dto.PedidoRequestDTO;
import com.ecomarket.venta.dto.ProductoDTO;
import com.ecomarket.venta.dto.UsuarioDTO;
import com.ecomarket.venta.model.DetalleVenta;
import com.ecomarket.venta.model.Venta;
import com.ecomarket.venta.repository.DetalleVentaRepository;
import com.ecomarket.venta.repository.VentaRepository;

import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Value;

import java.io.ByteArrayOutputStream;
import java.time.LocalDateTime;
import java.util.List;


@Service
@Transactional
public class VentaService {

    @Autowired
    private VentaRepository ventaRepository;

    @Autowired
    private DetalleVentaRepository detalleVentaRepository;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${ecomarket.notificaciones.url}")
    private String urlNotificacion;

    // Registrar una venta con validación de usuario y stock
    public Venta registrarVenta(Venta venta, String jwtToken) {
        verificarUsuario(venta.getEmailUsuario(), jwtToken);
        venta.setFechaHora(LocalDateTime.now());

        // Validar y completar cada detalle
        for (DetalleVenta detalle : venta.getDetalles()) {
            String url = "http://localhost:8082/api/productos/" + detalle.getIdProducto();
            ProductoDTO producto = restTemplate.getForObject(url, ProductoDTO.class);

            if (producto == null) {
                throw new RuntimeException("Producto no encontrado (ID: " + detalle.getIdProducto() + ")");
            }

            if (detalle.getCantidad() > producto.getCantidadEnStock()) {
                throw new RuntimeException("Stock insuficiente para el producto: " + producto.getNombre());
            }

            detalle.setVenta(venta);
            detalle.setNombreProducto(producto.getNombre());
            detalle.setPrecioUnitario(producto.getPrecioUnitario());
        }

        // Guardar la venta
        Venta ventaGuardada = ventaRepository.save(venta);

        // Guardar detalles y descontar stock
        for (DetalleVenta detalle : venta.getDetalles()) {
            detalle.setVenta(ventaGuardada);
            detalleVentaRepository.save(detalle);

            // Descontar stock
            actualizarStock(detalle.getIdProducto(), detalle.getCantidad());
        }

        ventaGuardada.setDetalles(venta.getDetalles());

        // Crear pedido en pedidos-logistica
        crearPedido(
            ventaGuardada.getId(),
            venta.getEmailUsuario(),
            venta.getDireccionDespacho()
        );

        // Notificar por correo al usuario
        NotificacionDTO notificacion = new NotificacionDTO();
        notificacion.setDestinatario(venta.getEmailUsuario());
        notificacion.setAsunto("EcoMarket - Confirmación de Compra");
        notificacion.setCuerpo("Gracias por tu compra. El total fue de $" + venta.getTotal() + ". Tu pedido está en preparación.");

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<NotificacionDTO> request = new HttpEntity<>(notificacion, headers);

        try {
            restTemplate.postForEntity(urlNotificacion, request, String.class);
            System.out.println("Notificación enviada a: " + venta.getEmailUsuario());
        } catch (Exception e) {
            System.err.println("Error al enviar notificación: " + e.getMessage());
        }
        return ventaGuardada;
    }

    // Listar todas las ventas
    public List<Venta> listarVentas() {
        return ventaRepository.findAll();
    }

    // Listar ventas por email (con validación del token)
    public List<Venta> obtenerVentasPorUsuario(String emailUsuario, String jwtToken) {
        verificarUsuario(emailUsuario, jwtToken);
        return ventaRepository.findByEmailUsuario(emailUsuario);
    }

    public void cancelarVenta(Long idVenta, String jwtToken) {
        // Validar si la venta existe
        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));
    
        // Validar que el usuario que la cancela es el mismo que la hizo
        verificarUsuario(venta.getEmailUsuario(), jwtToken);
    
        // Primero eliminar los detalles (o usar CascadeType.REMOVE)
        detalleVentaRepository.deleteAll(venta.getDetalles());
    
        // Luego eliminar la venta
        ventaRepository.delete(venta);
    }

    // Validación del token con usuarios-auth
    public void verificarUsuario(String emailUsuario, String jwtToken) {
        String url = "http://localhost:8081/api/usuarios/buscar?email=" + emailUsuario;

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtToken);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        ResponseEntity<UsuarioDTO> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            UsuarioDTO.class
        );

        UsuarioDTO usuario = response.getBody();

        if (usuario == null || !usuario.getEmail().equalsIgnoreCase(emailUsuario)) {
            throw new RuntimeException("Token inválido o no coincide con el usuario");
        }
    }

    public byte[] generarBoleta(Long idVenta, String jwtToken) {
        Venta venta = ventaRepository.findById(idVenta)
            .orElseThrow(() -> new RuntimeException("Venta no encontrada"));

        verificarUsuario(venta.getEmailUsuario(), jwtToken);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Document document = new Document();
        PdfWriter.getInstance(document, baos);
        document.open();

        document.add(new Paragraph("EcoMarket SPA - Boleta"));
        document.add(new Paragraph("Cliente: " + venta.getEmailUsuario()));
        document.add(new Paragraph("Fecha: " + venta.getFechaHora()));
        document.add(new Paragraph("Método de pago: " + venta.getMetodoPago()));
        document.add(new Paragraph(" "));

        PdfPTable table = new PdfPTable(4);
        table.addCell("Producto");
        table.addCell("Cantidad");
        table.addCell("Precio Unitario");
        table.addCell("Subtotal");

        for (DetalleVenta detalle : venta.getDetalles()) {
            table.addCell(detalle.getNombreProducto());
            table.addCell(String.valueOf(detalle.getCantidad()));
            table.addCell("$" + detalle.getPrecioUnitario());
            double subtotal = detalle.getCantidad() * detalle.getPrecioUnitario();
            table.addCell("$" + subtotal);
        }

        document.add(table);
        document.add(new Paragraph(" "));
        document.add(new Paragraph("Total: $" + venta.getTotal()));
        document.close();

        return baos.toByteArray();
    }

    public void actualizarStock(Long idProducto, int cantidadVendida) {
        ActualizarStockDTO dto = new ActualizarStockDTO();
        dto.setIdProducto(idProducto);
        dto.setCantidadVendida(cantidadVendida);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<ActualizarStockDTO> entity = new HttpEntity<>(dto, headers);

        restTemplate.exchange(
            "http://localhost:8082/api/productos/stock",
            HttpMethod.POST,
            entity,
            String.class
        );
    }

    public void crearPedido(Long idVenta, String emailCliente, String direccionDespacho) {
    PedidoRequestDTO dto = new PedidoRequestDTO();
    dto.setIdVenta(idVenta);
    dto.setEmailCliente(emailCliente);
    dto.setDireccionDespacho(direccionDespacho);

    HttpHeaders headers = new HttpHeaders();
    headers.setContentType(MediaType.APPLICATION_JSON);

    HttpEntity<PedidoRequestDTO> entity = new HttpEntity<>(dto, headers);

    restTemplate.exchange(
        "http://localhost:8085/api/pedidos",
        HttpMethod.POST,
        entity,
        String.class
        );
    }
}
