import java.awt.*;
import java.text.NumberFormat;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

class Rental {
    String carModel, carNumber, customerName, contact;
    int days;
    double totalRent;

    Rental(String carModel, String carNumber, String customerName, String contact, int days, double totalRent) {
        this.carModel = carModel;
        this.carNumber = carNumber;
        this.customerName = customerName;
        this.contact = contact;
        this.days = days;
        this.totalRent = totalRent;
    }
}

public class CarRentalSystem extends JFrame {

    private JComboBox<String> cbCarModel, cbCarNumber;
    private JTextField tfCustomerName, tfContact, tfDays, tfSearch;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Rental> rentalList = new ArrayList<>();
    private JLabel lblRentPerDay;

    private final Map<String, Double> carRentMap = new HashMap<>();
    private final Map<String, List<String>> availableCars = new HashMap<>();

    public CarRentalSystem() {
        setTitle("Car Rental Management System");
        setSize(950, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Rent per day setup
        carRentMap.put("Toyota Fortuner", 2000.0);
        carRentMap.put("Honda Civic", 1840.0);
        carRentMap.put("BMW X5", 3020.0);
        carRentMap.put("Hyundai Verna", 1500.0);
        carRentMap.put("KIA Seltos", 2500.0);

        // Available car numbers
        availableCars.put("Toyota Fortuner", new ArrayList<>(Arrays.asList("KA-01-AA-1234", "KA-01-AA-5678")));
        availableCars.put("Honda Civic", new ArrayList<>(Arrays.asList("KA-02-BB-1111", "KA-02-BB-2222")));
        availableCars.put("BMW X5", new ArrayList<>(Collections.singletonList("KA-03-CC-3333")));
        availableCars.put("Hyundai Verna", new ArrayList<>(Arrays.asList("KA-04-DD-4444", "KA-04-DD-5555")));
        availableCars.put("KIA Seltos", new ArrayList<>(Collections.singletonList("KA-05-EE-6666")));

        // Title
        JLabel title = new JLabel("Car Rental Management System", JLabel.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 24));
        title.setForeground(new Color(25, 89, 174));
        title.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        add(title, BorderLayout.NORTH);

        // Form Panel
        JPanel form = new JPanel(new GridLayout(7, 2, 12, 10));
        form.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        form.setBackground(new Color(235, 245, 255));

        cbCarModel = new JComboBox<>(carRentMap.keySet().toArray(new String[0]));
        cbCarNumber = new JComboBox<>();
        tfCustomerName = new JTextField();
        tfContact = new JTextField();
        tfDays = new JTextField();
        tfSearch = new JTextField();
        lblRentPerDay = new JLabel();

        cbCarModel.addActionListener(e -> {
            updateCarNumbers();
            updateRentPerDay();
        });

        form.add(new JLabel("Car Model:"));
        form.add(cbCarModel);
        form.add(new JLabel("Rent Per Day:"));
        form.add(lblRentPerDay);
        form.add(new JLabel("Car Number:"));
        form.add(cbCarNumber);
        form.add(new JLabel("Customer Name:"));
        form.add(tfCustomerName);
        form.add(new JLabel("Contact No.:"));
        form.add(tfContact);
        form.add(new JLabel("Rental Days:"));
        form.add(tfDays);

        JButton btnAdd = new JButton("Add Rental");
        JButton btnSearch = new JButton("Search by Customer");
        form.add(btnAdd);
        form.add(btnSearch);

        add(form, BorderLayout.WEST);

        // Table Setup
        model = new DefaultTableModel(
                new String[] { "Car Model", "Car No.", "Customer", "Contact", "Days", "Total Rent" }, 0);
        table = new JTable(model);
        table.setRowHeight(28);
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 14));
        JScrollPane pane = new JScrollPane(table);
        pane.setBorder(BorderFactory.createTitledBorder("Rented Cars"));
        add(pane, BorderLayout.CENTER);

        // Bottom Panel (Search + Buttons)
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottom.setBackground(new Color(235, 245, 255));

        JLabel lblSearch = new JLabel("Search:");
        lblSearch.setFont(new Font("SansSerif", Font.PLAIN, 14));
        bottom.add(lblSearch);

        tfSearch.setPreferredSize(new Dimension(300, 30));
        bottom.add(tfSearch);

        JButton btnDelete = new JButton("Delete Selected");
        JButton btnShowAll = new JButton("Show All");
        bottom.add(btnShowAll);
        bottom.add(btnDelete);

        add(bottom, BorderLayout.SOUTH);

        // Button Events
        btnAdd.addActionListener(e -> addRental());
        btnSearch.addActionListener(e -> searchRental());
        btnDelete.addActionListener(e -> deleteRental());
        btnShowAll.addActionListener(e -> showAllRentals());

        updateCarNumbers();
        updateRentPerDay();
    }

    private void updateCarNumbers() {
        cbCarNumber.removeAllItems();
        String selectedModel = (String) cbCarModel.getSelectedItem();
        List<String> numbers = availableCars.getOrDefault(selectedModel, new ArrayList<>());

        if (numbers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No cars available for model: " + selectedModel, "Unavailable",
                    JOptionPane.WARNING_MESSAGE);
        }

        for (String number : numbers) {
            cbCarNumber.addItem(number);
        }
    }

    private void updateRentPerDay() {
        String selectedModel = (String) cbCarModel.getSelectedItem();
        if (selectedModel != null) {
            double rent = carRentMap.get(selectedModel);
            lblRentPerDay.setText("₹" + String.format("%,.2f", rent));
        }
    }

    private void addRental() {
        String carModel = (String) cbCarModel.getSelectedItem();
        String carNumber = (String) cbCarNumber.getSelectedItem();
        String customer = tfCustomerName.getText().trim();
        String contact = tfContact.getText().trim();
        String daysStr = tfDays.getText().trim();

        if (carModel == null || carNumber == null || customer.isEmpty() || contact.isEmpty() || daysStr.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields and ensure a car is available.");
            return;
        }

        if (!contact.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "Enter a valid 10-digit contact number.");
            return;
        }

        int days;
        try {
            days = Integer.parseInt(daysStr);
            if (days <= 0)
                throw new NumberFormatException();
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Enter a valid number of rental days.");
            return;
        }

        double rentPerDay = carRentMap.getOrDefault(carModel, 0.0);
        double totalRent = rentPerDay * days;

        rentalList.add(new Rental(carModel, carNumber, customer, contact, days, totalRent));
        availableCars.get(carModel).remove(carNumber);
        updateCarNumbers();
        updateRentPerDay();
        showAllRentals();

        tfCustomerName.setText("");
        tfContact.setText("");
        tfDays.setText("");
    }

    private void showAllRentals() {
        model.setRowCount(0);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        for (Rental r : rentalList) {
            model.addRow(new Object[] { r.carModel, r.carNumber, r.customerName, r.contact, r.days,
                    formatter.format(r.totalRent) });
        }
    }

    private void searchRental() {
        String search = tfSearch.getText().trim().toLowerCase();
        model.setRowCount(0);
        NumberFormat formatter = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
        for (Rental r : rentalList) {
            if (r.customerName.toLowerCase().contains(search)) {
                model.addRow(new Object[] { r.carModel, r.carNumber, r.customerName, r.contact, r.days,
                        formatter.format(r.totalRent) });
            }
        }
    }

    private void deleteRental() {
        int row = table.getSelectedRow();
        if (row == -1) {
            JOptionPane.showMessageDialog(this, "Select a rental to delete.");
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete this rental?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION);
        if (confirm != JOptionPane.YES_OPTION)
            return;

        String carModel = model.getValueAt(row, 0).toString();
        String carNumber = model.getValueAt(row, 1).toString();

        rentalList.removeIf(r -> r.carModel.equals(carModel) && r.carNumber.equals(carNumber));
        availableCars.computeIfAbsent(carModel, k -> new ArrayList<>()).add(carNumber);
        updateCarNumbers();
        updateRentPerDay();
        showAllRentals();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new CarRentalSystem().setVisible(true));
    }
}
