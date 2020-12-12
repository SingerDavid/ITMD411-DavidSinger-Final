package Javaapplication;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

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

	public Tickets(Boolean isAdmin) {

		chkIfAdmin = isAdmin;
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

		// initialize any more desired sub menu items below

		/* Add action listeners for each desired menu item *************/
		mnuItemExit.addActionListener(this);
		
		//only shows if user is an admin
		if (chkIfAdmin == true) {
			mnuItemUpdate.addActionListener(this);
			mnuItemDelete.addActionListener(this);
		}
		mnuItemOpenTicket.addActionListener(this);
		mnuItemViewTicket.addActionListener(this);

	}

	private void prepareGUI() {

		// create JMenu bar
		JMenuBar bar = new JMenuBar();
		bar.add(mnuFile); // add main menu items in order, to JMenuBar
		
		//only show if user is an admin
		if (chkIfAdmin == true) {
			bar.add(mnuAdmin);
		}
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
			System.out.println("Exiting..");
		} else if (e.getSource() == mnuItemOpenTicket) {

			// get ticket information
			String ticketName = JOptionPane.showInputDialog(null, "Enter your name");
			String ticketDesc = JOptionPane.showInputDialog(null, "Enter a ticket description");
			String status = "Open";
			String timestamp = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(Calendar.getInstance().getTime());

			// insert ticket information to database

			int id = dao.insertRecords(ticketName, ticketDesc, status, timestamp);

			// display results if successful or not to console / dialog box
			if (id != 0) {
				System.out.println("Ticket ID : " + id + " created successfully!!!");
				JOptionPane.showMessageDialog(null, "Ticket id: " + id + " created");
			} else
				System.out.println("Ticket cannot be created!!! - line 137");
		}

		else if (e.getSource() == mnuItemViewTicket) {

			// retrieve all tickets details for viewing in JTable
			try {

				// Use JTable built in functionality to build a table model and
				// display the table model off your result set!!!
				JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
				jt.setBounds(30, 40, 200, 400);
				JScrollPane sp = new JScrollPane(jt);
				add(sp);
				setVisible(true); // refreshes or repaints frame on screen

			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		
		else if (e.getSource() == mnuItemUpdate) {
			
			//show JOption panel to get ticket information, to be updated.
			String ticketId = JOptionPane.showInputDialog(null, "Enter ticket ID, to be updated");
			String ticketDesc = JOptionPane.showInputDialog(null, "Ticket Description");
			String ticketStatus = JOptionPane.showInputDialog(null, "Update status");
			
			if (ticketId == null) {
				JOptionPane.showMessageDialog(null, "Ticket update failed, invalid ticket");
				System.out.println("Ticket update failed, invalid ticket - line 167");
			} else
				System.out.println("Processing update..");
			
				//parse ticketId (string to int: before entering database)
				int tid = Integer.parseInt(ticketId);
				
				//call to Doa.java to update database
				dao.updateRecords(ticketId, ticketDesc, ticketStatus);
				
				//display results if successful or not to console / dialog box
				if (tid != 0) {
					System.out.println("Ticket ID :" + tid + " updated successfully!!! - line 179");
					JOptionPane.showMessageDialog(null, "Ticket id: " + tid + " updated");
				}
				else {
					System.out.println("Ticked update failed (2nd level failure) - line 183");
				} //do I want brackets?? - come back after running
				try {

					// Use JTable built in functionality to build a table model and
					// display the table model off your result set!!!
					JTable jt = new JTable(ticketsJTable.buildTableModel(dao.readRecords()));
					jt.setBounds(30, 40, 200, 400);
					JScrollPane sp = new JScrollPane(jt);
					add(sp);
					setVisible(true); // refreshes or repaints frame on screen

				} catch (SQLException e1) {
					e1.printStackTrace();
				}			
		}
		else if (e.getSource() == mnuItemDelete) {
			//get ticket id to delete
			String ticketId = JOptionPane.showInputDialog(null, "Enter a ticket number to delete");
			
			if (ticketId == null) {
				JOptionPane.showMessageDialog(null, "Ticket delete failed, invalid ticket");
				System.out.println("Ticket delete failed, invalid ticket - line 205");
			} else
				System.out.println("Deleting ticket..");
			
				int tid = Integer.parseInt(ticketId);
			
				//configure built in JOption confirm (YES/NO)
				//component, string, int, JOption display message
				int confirm = JOptionPane.showConfirmDialog(null, "Confirm, do you wish to delete ticket ID: " + tid + "?", "Warning!", JOptionPane.YES_NO_OPTION);
			
				if (confirm == JOptionPane.YES_OPTION) {
					int delid = dao.deleteRecords(tid);
				
					if (delid !=0) {
						JOptionPane.showMessageDialog(null, "Ticket ID: " + delid + " successfully deleted");
						System.out.println("Ticket ID: " + delid + " successfully deleted");
					} else {
						System.out.println("Ticket was not deleted!");
					}
				} else {
					JOptionPane.showMessageDialog(null, "Ticket ID" + tid + " was not deleted!");
				}			
		}
		
	}

}
