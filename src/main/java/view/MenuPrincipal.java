package view;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;

/**
 * Menu principal de la aplicacion. Permite abrir las dos funcionalidades con
 * interfaz grafica: solicitar un alquiler (UC3) y consultar vehiculos
 * disponibles (UC5).
 */
public class MenuPrincipal extends JFrame {

    public MenuPrincipal() {
        super("Gestion de Alquiler de Vehiculos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(420, 240);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(javax.swing.BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titulo = new JLabel("Sistema de Alquiler de Vehiculos", SwingConstants.CENTER);
        titulo.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton btnSolicitar = new JButton("Solicitar Alquiler (UC3)");
        JButton btnConsultar = new JButton("Consultar Vehiculos Disponibles (UC5)");
        btnSolicitar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnConsultar.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnSolicitar.setMaximumSize(new Dimension(320, 36));
        btnConsultar.setMaximumSize(new Dimension(320, 36));

        btnSolicitar.addActionListener(e -> new VentanaSolicitarAlquiler().setVisible(true));
        btnConsultar.addActionListener(e -> new VentanaConsultarDisponibles().setVisible(true));

        panel.add(titulo);
        panel.add(javax.swing.Box.createVerticalStrut(20));
        panel.add(btnSolicitar);
        panel.add(javax.swing.Box.createVerticalStrut(10));
        panel.add(btnConsultar);

        JPanel contenedor = new JPanel(new FlowLayout(FlowLayout.CENTER));
        contenedor.add(panel);
        add(contenedor);
    }
}
