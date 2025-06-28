package gui_admin.gui_util;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import java.awt.*;
import java.util.List;

public class CustomTablePanel extends JPanel {

    private JTable table;
    private DefaultTableModel model;
    private JScrollPane scrollPane;

    // Define colors for alternating rows
    private static final Color EVEN_ROW_COLOR = new Color(240, 240, 240); // Light gray
    private static final Color ODD_ROW_COLOR = Color.WHITE;

    // Define color for the header
    private static final Color HEADER_BACKGROUND_COLOR = new Color(70, 130, 180); // Steel Blue
    private static final Color HEADER_FOREGROUND_COLOR = Color.WHITE;

    public CustomTablePanel(List<List<Object>> tableData, List<String> columnNames) {
        this.setLayout(new BorderLayout());

        String[] columnsArray = columnNames.toArray(new String[0]);

        model = new DefaultTableModel(columnsArray, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };

        updateTableData(tableData);

        table = new JTable(model);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        // Apply custom header renderer
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setDefaultRenderer(new HeaderRenderer((DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer()));
        tableHeader.setReorderingAllowed(false); // Prevent column reordering
        tableHeader.setResizingAllowed(true); // Allow column resizing

        // Apply custom cell renderer for alternating row colors
        table.setDefaultRenderer(Object.class, new CustomCellRenderer());

        // Set row height for better visual appearance
        table.setRowHeight(25);

        scrollPane = new JScrollPane(table);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    /**
     * Updates the data displayed in the table.
     * @param newData The new list of data to display.
     */
    public void updateTableData(List<List<Object>> newData) {
        model.setRowCount(0); // Remove all existing rows
        for (List<Object> row : newData) {
            model.addRow(row.toArray()); // Add new rows
        }
    }

    /**
     * Returns the index of the currently selected row in the table.
     * @return The index of the selected row, or -1 if no row is selected.
     */
    public int getSelectedRow() {
        return table.getSelectedRow();
    }

    /**
     * Returns the value of a specific cell in the table.
     * @param row The row index.
     * @param col The column index.
     * @return The value of the cell.
     */
    public Object getValueAt(int row, int col) {
        if (row >= 0 && row < model.getRowCount() && col >= 0 && col < model.getColumnCount()) {
            return model.getValueAt(row, col);
        }
        return null; // Returns null if the row or column is invalid
    }

    public JTable getTable() {
        return table;
    }

    /**
     * Custom renderer for the table header to set background and foreground colors.
     */
    private static class HeaderRenderer extends DefaultTableCellRenderer {
        private final DefaultTableCellRenderer originalRenderer;

        public HeaderRenderer(DefaultTableCellRenderer originalRenderer) {
            this.originalRenderer = originalRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component renderer = originalRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            renderer.setBackground(HEADER_BACKGROUND_COLOR);
            renderer.setForeground(HEADER_FOREGROUND_COLOR);
            setFont(renderer.getFont().deriveFont(Font.BOLD)); // Make header text bold
            setHorizontalAlignment(JLabel.CENTER); // Center align header text
            return renderer;
        }
    }

    /**
     * Custom renderer for table cells to provide alternating row colors.
     */
    private static class CustomCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component cellComponent = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);

            // Set alternating row colors
            if (!isSelected) { // Don't override selection color
                if (row % 2 == 0) {
                    cellComponent.setBackground(EVEN_ROW_COLOR);
                } else {
                    cellComponent.setBackground(ODD_ROW_COLOR);
                }
            }
            // Set text alignment for all cells (e.g., center or left)
            setHorizontalAlignment(JLabel.LEFT); // You can change this to JLabel.CENTER or JLabel.RIGHT
            return cellComponent;
        }
    }
}