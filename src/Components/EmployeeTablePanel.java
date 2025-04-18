// package Components;

// import javax.swing.*;
// import javax.swing.table.DefaultTableModel;
// import java.awt.*;

// public class EmployeeTablePanel extends JPanel {
//     private JTable employeeTable;
    
//     public EmployeeTablePanel() {
//         setLayout(new BorderLayout());
//         employeeTable = new JTable();
//         add(new JScrollPane(employeeTable), BorderLayout.CENTER);
//     }
    
//     public void updateTable(List<Employee> employees) {
//         DefaultTableModel model = new DefaultTableModel(
//             new String[]{"ID", "Name", "Role", "RecordID"}, 0
//         ) {
//             @Override
//             public boolean isCellEditable(int row, int column) {
//                 return false;
//             }
//         };
        
//         for (Employee emp : employees) {
//             model.addRow(new Object[]{
//                 emp.getEmployeeId(),
//                 emp.getName(),
//                 emp.getRole(),
//                 emp.getRecordId()
//             });
//         }
        
//         employeeTable.setModel(model);
//     }
    
//     public JTable getTable() {
//         return employeeTable;
//     }
// }
