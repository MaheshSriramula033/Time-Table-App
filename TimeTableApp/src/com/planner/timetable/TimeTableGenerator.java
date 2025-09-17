package com.planner.timetable;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;

import org.apache.pdfbox.pdmodel.*;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

public class TimeTableGenerator extends JFrame {
	private static final long serialVersionUID = 1L;

    private JTable table;
    private DefaultTableModel model;
    private JComboBox<String> cbDays, cbPeriod, cbSubject, cbTeacher;

    public TimeTableGenerator() {
        setTitle("Time Table Generator");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Heading
        JLabel heading = new JLabel("Weekly Time Table Generator", JLabel.CENTER);
        heading.setFont(new Font("Segoe UI", Font.BOLD, 22));
        heading.setForeground(new Color(0, 102, 204));
        add(heading, BorderLayout.NORTH);

        // Data
        String[] days = {"Monday", "Tuesday", "Wednesday", "Thursday", "Friday"};
        String[] periods = {"1", "2", "3", "4", "5", "6", "7"};
        String[] subjects = {"Math", "Science", "History", "English", "Computer"};
        String[] teachers = {"Mr. A", "Ms. B", "Mr. C", "Ms. D"};

        // Combo Panel
        JPanel comboPanel = new JPanel(new GridLayout(2, 4, 10, 10));
        comboPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        comboPanel.setBackground(new Color(240, 248, 255));

        cbDays = new JComboBox<>(days);
        cbPeriod = new JComboBox<>(periods);
        cbSubject = new JComboBox<>(subjects);
        cbTeacher = new JComboBox<>(teachers);
        JButton btnAssign = new JButton("Assign");

        comboPanel.add(new JLabel("Select Day:"));
        comboPanel.add(cbDays);
        comboPanel.add(new JLabel("Select Period:"));
        comboPanel.add(cbPeriod);
        comboPanel.add(new JLabel("Select Subject:"));
        comboPanel.add(cbSubject);
        comboPanel.add(new JLabel("Select Teacher:"));
        comboPanel.add(cbTeacher);
        add(comboPanel, BorderLayout.SOUTH);

        // Table
        String[] columnNames = {"Day", "Period 1", "Period 2", "Period 3", "Period 4", "Period 5", "Period 6", "Period 7"};
        String[][] data = {
                {"Monday", "", "", "", "", "", "", ""},
                {"Tuesday", "", "", "", "", "", "", ""},
                {"Wednesday", "", "", "", "", "", "", ""},
                {"Thursday", "", "", "", "", "", "", ""},
                {"Friday", "", "", "", "", "", "", ""}
        };

        model = new DefaultTableModel(data, columnNames);
        table = new JTable(model);
        table.setRowHeight(30);
        JScrollPane scrollPane = new JScrollPane(table);
        add(scrollPane, BorderLayout.CENTER);

        // Button panel
        JPanel btnPanel = new JPanel();
        btnPanel.add(btnAssign);

        JButton btnExportPDF = new JButton("Export to PDF");
        btnExportPDF.addActionListener(this::exportToPDF);
        btnPanel.add(btnExportPDF);

        add(btnPanel, BorderLayout.BEFORE_FIRST_LINE);

        // Assign action
        btnAssign.addActionListener(e -> assignSubject());
    }

    private void assignSubject() {
        int dayIndex = cbDays.getSelectedIndex();
        int periodIndex = cbPeriod.getSelectedIndex() + 1;

        String subject = (String) cbSubject.getSelectedItem();
        String teacher = (String) cbTeacher.getSelectedItem();

        String value = subject + " (" + teacher + ")";
        model.setValueAt(value, dayIndex, periodIndex);
    }

    private void exportToPDF(ActionEvent e) {
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);

        PDPageContentStream contentStream = null;

        try {
            contentStream = new PDPageContentStream(document, page);

            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.beginText();
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("Weekly Time Table");
            contentStream.endText();

            contentStream.setFont(PDType1Font.HELVETICA, 10);
            float y = 720;

            // Print column headers
            contentStream.beginText();
            contentStream.newLineAtOffset(50, y);
            for (int col = 0; col < model.getColumnCount(); col++) {
                contentStream.showText(pad(model.getColumnName(col), 18));
            }
            contentStream.endText();

            y -= 20;

            // Print table rows
            for (int row = 0; row < model.getRowCount(); row++) {
                contentStream.beginText();
                contentStream.newLineAtOffset(50, y);
                for (int col = 0; col < model.getColumnCount(); col++) {
                    String value = (String) model.getValueAt(row, col);
                    contentStream.showText(pad(value != null ? value : "", 18));
                }
                contentStream.endText();
                y -= 20;
            }

            contentStream.close();

            File file = new File("TimeTable.pdf");
            document.save(file);
            document.close();

            JOptionPane.showMessageDialog(this, "PDF exported to " + file.getAbsolutePath());

        } catch (IOException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to export PDF: " + ex.getMessage());
        } finally {
            try {
                if (contentStream != null) contentStream.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private String pad(String text, int length) {
        if (text == null) text = "";
        return String.format("%-" + length + "s", text.length() > length ? text.substring(0, length) : text);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new TimeTableGenerator().setVisible(true));
    }
}
