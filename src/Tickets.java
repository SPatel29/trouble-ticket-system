package src;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.sql.Timestamp;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;

@SuppressWarnings("serial")
public class Tickets extends JFrame implements ActionListener {

	// class level member objects
	Dao dao = new Dao(); // for CRUD operations
	Boolean chkIfAdmin = null;
	int userID;

	// Main menu object items
	private JMenu mnuFile = new JMenu("File");
	private JMenu mnuAdmin = new JMenu("Admin");
	private JMenu mnuTickets = new JMenu("Tickets");

	// Sub menu item objects for all Main menu item objects
	JMenuItem mnuItemExit;
	JMenuItem mnuItemUpdate;
	JMenuItem mnuItemDelete;
	JMenuItem mnuItemOpenTicket;
	JMenuItem mnuItemViewTicket;
	JMenuItem mnuItemCloseViewTicket;

	public Tickets(Boolean isAdmin, int id) {

		chkIfAdmin = isAdmin;
		userID = id;
		System.out.println("USERID " + userID);
		createMenu();
		prepareGUI();

	}

	private void createMenu() {

		/* Initialize sub menu items **************************************/

		// initialize sub menu item for File main menu
		mnuItemExit = new JMenuItem("Exit");
		// add to File main menu item
		mnuFile.add(mnuItemExit);

		// initialize first sub menu items for Admin main menu
		mnuItemUpdate = new JMenuItem("Update Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemUpdate);

		// initialize second sub menu items for Admin main menu
		mnuItemDelete = new JMenuItem("Delete Ticket");
		// add to Admin main menu item
		mnuAdmin.add(mnuItemDelete);

		// initialize first sub menu item for Tickets main menu
		mnuItemOpenTicket = new JMenuItem("Open Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemOpenTicket);

		// initialize second sub menu item for Tickets main menu
		mnuItemViewTicket = new JMenuItem("View Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemViewTicket);

		// initialize third sub menu item for Tickets main menu
		mnuItemCloseViewTicket = new JMenuItem("Close Ticket");
		// add to Ticket Main menu item
		mnuTickets.add(mnuItemCloseViewTicket);

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		mnuItemUpdate.addActionListener(this);
		mnuItemDelete.addActionListener(this);
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);
		mnuItemCloseViewTicket.addActionListener(this);
		/*
		 * continue implementing any other desired sub menu items (like
		 * for update and delete sub menus for example) with similar
		 * syntax & logic as shown above
		 */

	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar

		if (chkIfAdmin)
			bar.add(mnuAdmin);

		bar.add(mnuTickets);
		// add menu bar components to frame
		setJMenuBar(bar);

		addWindowListener(new WindowAdapter() {
			// define a window close operation
			public void windowClosing(WindowEvent wE) {
				System.exit(0);
			}
		});
		// set frame options
		setSize(400, 400);
		getContentPane().setBackground(Color.LIGHT_GRAY);
		setLocationRelativeTo(null);
		setVisible(true);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		// implement actions for sub menu items
		if (e.getSource() == mnuItemExit) {
			System.exit(0);
		} else if (e.getSource() == mnuItemOpenTicket) {

			try {
				if (chkIfAdmin) {
					userID = Integer
							.parseInt(JOptionPane.showInputDialog(null,
									"Enter ID of user who ticket should be issued to"));
				}
				String ticketName = JOptionPane.showInputDialog(null, "Enter ticket name");
				if (ticketName != null) {
					String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");
					if (ticketDesc != null) {
						// insert ticket information to database
						long startTime = System.currentTimeMillis();
						Timestamp currentTime = new Timestamp(startTime);
						int id = dao.insertRecords(userID, ticketName, ticketDesc, currentTime);

						// display results if successful or not to console / dialog box
						if (id != 0) {
							System.out.println("Ticket ID : " + id + " created successfully!!!");
							JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
						} else {
							System.out.println("Empty User Input Value detected. Cancelling ticket creation.");
							JOptionPane.showMessageDialog(null, "Ticket cannot be created with empty value");

						}
					} else {
						JOptionPane.showMessageDialog(null, "Ticket cannot be created with empty value");
						System.out.println("Empty User Input Value detected. Cancelling ticket creation.");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Ticket cannot be created with empty value");
					System.out.println("Empty User Input Value detected. Cancelling ticket creation.");
				}
				// get ticket information

			} catch (NumberFormatException ex) {
				JOptionPane.showMessageDialog(null, "Ticket cannot be created with empty value");
				System.out.println("Empty User Input Value detected. Cancelling ticket creation.");
			}

		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords(chkIfAdmin, userID)));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		} else if (e.getSource() == mnuItemCloseViewTicket) {
			// TODO:
			// recreate database for sp_closetickets

			String ticketID = JOptionPane.showInputDialog(null, "Enter ticket ID");
			if (ticketID != null) {
				int id = 0;
				// insert ticket information to database
				long startTime = System.currentTimeMillis();
				Timestamp currentTime = new Timestamp(startTime);
				id = dao.closeTickets(ticketID, currentTime);
				// display results if successful or not to console / dialog box

				if (id != 0) {
					System.out.println("Ticket ID : " + id + " closed successfully!!!");
					JOptionPane.showMessageDialog(null, "Ticket id: " + id + " closed");
				} else {
					JOptionPane.showMessageDialog(null, "Ticket id: " + ticketID + " does not exist!");
					System.out.println("User did not enter a valid ticket ID.");
				}
			} else {
				JOptionPane.showMessageDialog(null, "Ticket cannot be closed without ticket ID");
				System.out.println("User did not enter ticket ID. Closing input.");
			}

		}

		else if (e.getSource() == mnuItemDelete) {
			String ticketID = JOptionPane.showInputDialog(null, "Enter ticket ID");
			int id = 0;

			id = dao.deleteTicket(Integer.parseInt(ticketID));
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " deleted successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " deleted");
			} else
				System.out.println("Ticket cannot be deleted!!!");
		}

		else if (e.getSource() == mnuItemUpdate) {
			String ticketID = JOptionPane.showInputDialog(null, "Enter existing ticket ID");
			String userID = JOptionPane.showInputDialog(null, "Enter new user ID");
			String username = JOptionPane.showInputDialog(null, "Enter new username");

			int id = 0;

			id = dao.updateTicket(Integer.parseInt(ticketID), Integer.parseInt(userID), username);
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " updated successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " updated");
			} else
				System.out.println("Ticket cannot be updated!!!");
		}
		/*
		 * continue implementing any other desired sub menu items (like for update and
		 * delete sub menus for example) with similar syntax & logic as shown above
		 */

	}

}
