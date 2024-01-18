package mase;
import javax.swing.*;
import javax.swing.border.Border;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PiePlot;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.RingPlot;
import org.jfree.data.general.DefaultPieDataset;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.category.DefaultCategoryDataset;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.*;

import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.CategoryLabelPositions;
import org.jfree.chart.axis.CategoryLabelWidthType;
import org.jfree.chart.axis.CategoryLabelWidthType.*;

import javax.swing.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.LinkedHashMap;
import java.util.Map;


@SuppressWarnings("serial")
public class JDBCMainWindowContent extends JInternalFrame implements ActionListener
{	
	String cmd = null;

	// DB Connectivity Attributes
	private Connection con = null;
	private Statement stmt = null;
	private ResultSet rs = null;

	private Container content;

	private JPanel detailsPanel;
	private JPanel exportButtonPanel;
	private JPanel exportConceptDataPanel;
	private JScrollPane dbContentsPanel;

	private Border lineBorder;
	
	private JLabel USERIDLabel=new JLabel("User ID:                 ");
	private JLabel UsernameLabel=new JLabel("Username:               ");
	private JLabel EmailLabel=new JLabel("Email:      ");
	private JLabel FirstNameLabel=new JLabel("First Name:        ");
	private JLabel LastNameLabel=new JLabel("Last Name:                 ");
	private JLabel addressLabel=new JLabel("Address:                 ");
	private JLabel houseNoLabel=new JLabel("House No:      ");
	private JLabel installLabel=new JLabel("Install Date:      ");
	private JLabel serviceLabel=new JLabel("Service Date:        ");
	private JLabel softwareLabel=new JLabel("Software Version:        ");

	
	private JTextField USERIDTF= new JTextField(10);
	private JTextField UsernameTF=new JTextField(10);
	private JTextField EmailTF=new JTextField(10);
	private JTextField FirstNameTF=new JTextField(10);
	private JTextField LastNameTF=new JTextField(10);
	private JTextField addressTF=new JTextField(10);
	private JTextField houseNoTF=new JTextField(10);
	private JTextField installTF=new JTextField(10);
	private JTextField serviceTF=new JTextField(10);
	private JTextField softwareTF=new JTextField(10);
	private JComboBox<String> tableSelectedComboBox;



	private static QueryTableModel TableModel = new QueryTableModel();
	//Add the models to JTabels
	private JTable TableofDBContents=new JTable(TableModel);
	//Buttons for inserting, and updating members
	//also a clear button to clear details panel
	private JButton updateButton = new JButton("Update");
	private JButton insertButton = new JButton("Insert");
	private JButton exportButton  = new JButton("Export");
	private JButton deleteButton  = new JButton("Delete");
	private JButton clearButton  = new JButton("Clear");
	private JButton readButton  = new JButton("Read");


	private JButton countInstallDate  = new JButton("Installation Date of Users");
	private JTextField countInstallDateTF  = new JTextField(12);
	private JButton countOfVersion  = new JButton("Number of Software Version");
	private JTextField countOfVersionTF  = new JTextField(12);
	private JButton JoinAllTables  = new JButton("Join All Tables");
	private JButton EnergyStatus  = new JButton("Energy Status");
	private JButton DeviceStatus  = new JButton("Device Status");
	private JButton Efficiency  = new JButton("Efficiency");
	private JButton PeakHours  = new JButton("PeakHours");
	private JButton PriceRange  = new JButton("PriceRange");
	private JButton ConsumptionRate  = new JButton("Consumption Rate");
	private JButton BatteryUsage  = new JButton("Battery Usage");
	
	public JDBCMainWindowContent( String aTitle)
	{	
		//setting up the GUI
		super(aTitle, false,false,false,false);
		setEnabled(true);

		initiate_db_conn();
		//add the 'main' panel to the Internal Frame
		content=getContentPane();
		content.setLayout(null);
		content.setBackground(Color.orange);
		lineBorder = BorderFactory.createEtchedBorder(15, Color.yellow, Color.black);
	
		//setup details panel and add the components to it
		detailsPanel=new JPanel();
		detailsPanel.setLayout(new GridLayout(11,2));
		detailsPanel.setBackground(Color.lightGray);
		detailsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "CRUD Actions"));

		detailsPanel.add(USERIDLabel);			
		detailsPanel.add(USERIDTF);
		detailsPanel.add(UsernameLabel);		
		detailsPanel.add(UsernameTF);
		detailsPanel.add(EmailLabel);	
		detailsPanel.add(EmailTF);
		detailsPanel.add(FirstNameLabel);		
		detailsPanel.add(FirstNameTF);
		detailsPanel.add(LastNameLabel);
		detailsPanel.add(LastNameTF);
		detailsPanel.add(addressLabel);
		detailsPanel.add(addressTF);
		detailsPanel.add(houseNoLabel);
		detailsPanel.add(houseNoTF);
		detailsPanel.add(installLabel);
		detailsPanel.add(installTF);
		detailsPanel.add(serviceLabel);
		detailsPanel.add(serviceTF);
		detailsPanel.add(softwareLabel);
		detailsPanel.add(softwareTF);



		//setup details panel and add the components to it
		exportButtonPanel=new JPanel();
		exportButtonPanel.setLayout(new GridLayout(3,2));
		exportButtonPanel.setBackground(Color.lightGray);
		exportButtonPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Data"));
		exportButtonPanel.add(countInstallDate);
		exportButtonPanel.add(countInstallDateTF);
		exportButtonPanel.add(countOfVersion);
		exportButtonPanel.add(countOfVersionTF);
		exportButtonPanel.add(JoinAllTables);
		exportButtonPanel.add(EnergyStatus);
		exportButtonPanel.setSize(500, 200);
		exportButtonPanel.setLocation(3, 300);
		content.add(exportButtonPanel);
		

		// Export Concept Data Panel
		exportConceptDataPanel = new JPanel();
		exportConceptDataPanel.setLayout(new GridLayout(3, 2)); // You can adjust the layout as needed
		exportConceptDataPanel.setBackground(Color.lightGray);
		exportConceptDataPanel.setBorder(BorderFactory.createTitledBorder(lineBorder, "Export Concept Data"));

		// Add components to exportConceptDataPanel as needed
		exportConceptDataPanel.add(DeviceStatus);
		exportConceptDataPanel.add(Efficiency);
		exportConceptDataPanel.add(PeakHours);
		exportConceptDataPanel.add(PriceRange);
		exportConceptDataPanel.add(ConsumptionRate);
		exportConceptDataPanel.add(BatteryUsage);
		
		exportConceptDataPanel.setSize(500, 200);
		exportConceptDataPanel.setLocation(3 + exportButtonPanel.getWidth() + 10, 300);
		content.add(exportConceptDataPanel);
		
		insertButton.setSize(100, 30);
		updateButton.setSize(100, 30);
		exportButton.setSize (100, 30);
		deleteButton.setSize (100, 30);
		clearButton.setSize (100, 30);
		readButton.setSize (100, 30);

		insertButton.setLocation(370, 10);
		updateButton.setLocation(370, 110);
		exportButton.setLocation (370, 160);
		deleteButton.setLocation (370, 60);
		clearButton.setLocation (370, 210);
		readButton.setLocation (370, 260);

		insertButton.addActionListener(this);
		updateButton.addActionListener(this);
		exportButton.addActionListener(this);
		deleteButton.addActionListener(this);
		clearButton.addActionListener(this);
		readButton.addActionListener(this);

		
		this.JoinAllTables.addActionListener(this);
		this.EnergyStatus.addActionListener(this);
		this.countInstallDate.addActionListener(this);
		this.countOfVersion.addActionListener(this);
		this.DeviceStatus.addActionListener(this);
		this.Efficiency.addActionListener(this);
		this.PeakHours.addActionListener(this);
		this.PriceRange.addActionListener(this);
		this.ConsumptionRate.addActionListener(this);
		this.BatteryUsage.addActionListener(this);
		
		
		content.add(insertButton);
		content.add(updateButton);
		content.add(exportButton);
		content.add(deleteButton);
		content.add(clearButton);
		content.add(readButton);


		TableofDBContents.setPreferredScrollableViewportSize(new Dimension(900, 300));

		dbContentsPanel=new JScrollPane(TableofDBContents,JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
		dbContentsPanel.setBackground(Color.lightGray);
		dbContentsPanel.setBorder(BorderFactory.createTitledBorder(lineBorder,"Database Content"));

		detailsPanel.setSize(360, 300);
		detailsPanel.setLocation(3,0);
		dbContentsPanel.setSize(700, 300);
		dbContentsPanel.setLocation(477, 0);

		content.add(detailsPanel);
		content.add(dbContentsPanel);

		setSize(982,645);
		setVisible(true);

		TableModel.refreshFromDB(stmt);
	}

	public void initiate_db_conn()
	{
		try
		{
			// Load the JConnector Driver
			Class.forName("com.mysql.jdbc.Driver");
			// Specify the DB Name
			String url="jdbc:mysql://localhost:3306/Smart?serverTimezone=UTC";
			// Connect to DB using DB URL, Username and password
			con = DriverManager.getConnection(url, "root", "ROOT");
			//Create a generic statement which is passed to the TestInternalFrame1
			stmt = con.createStatement();
		}
		catch(Exception e)
		{
			System.out.println("Error: Failed to connect to database\n"+e.getMessage());
		}
	}

	//event handling 
	public void actionPerformed(ActionEvent e)
	{
		Object target=e.getSource();
		if (target == clearButton)
		{
			 USERIDTF.setText("");
			 UsernameTF.setText("");
			 EmailTF.setText("");
			 FirstNameTF.setText("");
			 LastNameTF.setText("");
			 addressTF.setText("");
			 houseNoTF.setText("");
			 installTF.setText("");
			 serviceTF.setText("");
			 softwareTF.setText("");;

		}

		if (target == insertButton)
		{		 
	 		try
	 		{
 				String updateTemp ="INSERT INTO USERS VALUES ('"+
 		 				  USERIDTF.getText()+"','"+UsernameTF.getText()+"','"+EmailTF.getText()+"','"+FirstNameTF.getText()+"','"+LastNameTF.getText()+"','"
 		 				 +addressTF.getText()+"','"+houseNoTF.getText()+"','"+installTF.getText()+"','"+serviceTF.getText()+"','"+softwareTF.getText()+"');";
 				
 						
 				stmt.executeUpdate(updateTemp);
 			
	 		}
	 		catch (SQLException sqle)
	 		{
	 			System.err.println("Error with members insert:\n"+sqle.toString());
	 		}
	 		finally
	 		{
	 			TableModel.refreshFromDB(stmt);
			}
		}
		if (target == deleteButton)
		{
		 	
	 		try
	 		{
 				String updateTemp ="DELETE FROM USERS WHERE User_id = "+USERIDTF.getText()+";"; 
 				stmt.executeUpdate(updateTemp);
 			
	 		}
	 		catch (SQLException sqle)
	 		{
	 			System.err.println("Error with delete:\n"+sqle.toString());
	 		}
	 		finally
	 		{
	 			TableModel.refreshFromDB(stmt);
			}
		}
		if (target == updateButton)
		 {	 	
	 		try
	 		{ 			
 				String updateTemp ="UPDATE USERS SET Username = '"+UsernameTF.getText()+
 									
 									"', Email = "+
 									"'"+EmailTF.getText()+"'"+
 									", FirstName = "+"'"+FirstNameTF.getText()+"'"+
 									", LastName = "+"'"+LastNameTF.getText()+"'"+
 									", Address = "+"'"+addressTF.getText()+"'"+
 									", HouseNo = "+"'"+houseNoTF.getText()+"'"+
 									", Install_Date = "+"'"+installTF.getText()+"'"+
 									", Service_Date = "+"'"+serviceTF.getText()+"'"+
 									", Software_Version = "+"'"+softwareTF.getText()+"'"+
 									" where User_id = "+USERIDTF.getText();
 				
 	
 				
 				
 				System.out.println(updateTemp);
 				stmt.executeUpdate(updateTemp);
 				//these lines do nothing but the table updates when we access the db.
 				rs = stmt.executeQuery("SELECT * from USERS ");
 				rs.next();
 				rs.close();	
 			}
	 		catch (SQLException sqle){
	 			System.err.println("Error with members insert:\n"+sqle.toString());
	 		}
	 		finally{
	 			TableModel.refreshFromDB(stmt);
			}
		}
		
		if (target.equals(exportButton)){  		
		//set cmd to write out all the table data to the csv
			cmd="select  * from device";
			try {
				rs= stmt.executeQuery(cmd);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			writeToFile(rs);
		}
		
		if (target.equals(readButton)){  		
		//set cmd to write out all the table data to the csv
			cmd="select  * from energy";
			try {
				rs= stmt.executeQuery(cmd);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			writeToFile(rs);
		}

		/////////////////////////////////////////////////////////////////////////////////////
		//I have only added functionality of 4 of the button on the lower right of the template
		///////////////////////////////////////////////////////////////////////////////////		
		

		//Number of Installation Date cell button
		if(target == this.countInstallDate){

			String installDate = this.countInstallDateTF.getText();

			cmd = "select Install_Date, count(*) "+  "from users " + "where Install_Date = '"  +installDate+"';";

			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		} 
		
		//Number of SoftwareVersion cell button
		if(target == this.countOfVersion){

			String softwareVersion = this.countOfVersionTF.getText();

			cmd = "select Software_Version, count(*) "+  "from users " + "where Software_Version = '"  +softwareVersion+"';";

			System.out.println(cmd);
			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		
		//ListJoinAllTables all cell button
		if(target == this.JoinAllTables){

			cmd = "SELECT\r\n"
					+ "    u.User_id,\r\n"
					+ "    u.Username,\r\n"
					+ "    d.Device_Name,\r\n"
					+ "    e.Energy_consumption,\r\n"
					+ "    e.Peak_hours,\r\n"
					+ "    e.Efficiency,\r\n"
					+ "    e.Battery_life,\r\n"
					+ "    e.Voltage\r\n"
					+ "FROM\r\n"
					+ "    USERS u\r\n"
					+ "JOIN\r\n"
					+ "    DEVICE d ON u.User_id = d.User_id\r\n"
					+ "JOIN\r\n"
					+ "    Energy e ON u.User_id = e.user_id;\r\n"
					+ "";

			try{					
				rs= stmt.executeQuery(cmd); 	
				writeToFile(rs);
			}
			catch(Exception e1){e1.printStackTrace();}

		}
		
		
		
		if (target.equals(EnergyStatus)) {
		    cmd = "SELECT user_id, Energy_consumption FROM energy";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    barChart(rs, "Energy Statistics");
		}
		
		
		//UsersAndDevice cell button which joins the two table together
		if (target.equals(DeviceStatus)){  		
			cmd = "SELECT Status, COUNT(*) AS TotalDevices\r\n"
					+ "FROM DEVICE\r\n"
					+ "WHERE Status = 'Online' OR Status = 'Offline'\r\n"
					+ "GROUP BY Status;\r\n"
					+ "";
			try {
				rs= stmt.executeQuery(cmd);
			} catch (SQLException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} 
			pieGraph(rs, "Device Statistics");
		}
		

		if (target.equals(Efficiency)) {
		    cmd = "SELECT user_id, AVG(Efficiency) AS Avg_Efficiency, MAX(Efficiency) AS Max_Efficiency, MIN(Efficiency) AS Min_Efficiency\r\n"
		    		+ "FROM Energy\r\n"
		    		+ "GROUP BY user_id;\r\n"
		    		+ "";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    barChart1(rs, "Efficiency Statistics");
		}
		
		if (target.equals(PeakHours)) {
		    cmd = "SELECT user_id, MAX(Peak_hours) AS Max_PeakHours, MIN(Peak_hours) AS Min_PeakHours\r\n"
		    		+ "FROM Energy\r\n"
		    		+ "GROUP BY user_id;";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    lineChart(rs, "PeakHours Statistics");
		}

		
		if (target.equals(ConsumptionRate)) {
		    cmd = "SELECT\r\n"
		    		+ "    CASE\r\n"
		    		+ "        WHEN Energy_consumption >= 0 AND Energy_consumption < 50 THEN 'Low'\r\n"
		    		+ "        WHEN Energy_consumption >= 50 AND Energy_consumption < 100 THEN 'Moderate'\r\n"
		    		+ "        WHEN Energy_consumption >= 100 AND Energy_consumption < 150 THEN 'High'\r\n"
		    		+ "        ELSE 'Very High'\r\n"
		    		+ "    END AS Consumption_Category,\r\n"
		    		+ "    COUNT(*) AS Count\r\n"
		    		+ "FROM Energy\r\n"
		    		+ "GROUP BY Consumption_Category;\r\n"
		    		+ "";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    barChart3(rs, "Consumption  Rate");
		}
		
		if (target.equals(PriceRange)) {
		    cmd = "SELECT Device_Name, Price FROM DEVICE ORDER BY Price DESC;";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    barChart4(rs, "Device Prices");
		}
		
		
		if (target.equals(BatteryUsage)) {
		    String cmd = "SELECT user_id, Battery_life\r\n"
		    		+ "FROM Energy\r\n"
		    		+ "ORDER BY user_id;\r\n"
		    		+ "";
		    try {
		        rs = stmt.executeQuery(cmd);
		    } catch (SQLException e1) {
		        e1.printStackTrace();
		    }
		    createLineGraph(rs, "Battery Usage Statistic");
		}


		
	}//end of action performed
	///////////////////////////////////////////////////////////////////////////
	
	public void createLineGraph(ResultSet rs, String title) {
	    try {
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        while (rs.next()) {
	            String user_id = rs.getString("user_id");
	            double battery_life = rs.getDouble("Battery_life");
	            dataset.addValue(battery_life, "Battery Life", user_id);
	        }

	        JFreeChart chart = ChartFactory.createLineChart(
	            title,
	            "User ID",
	            "Battery Life",
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
public void barChart4(ResultSet rs, String title) {
    try {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        while (rs.next()) {
            String deviceName = rs.getString("Device_Name");
            double price = rs.getDouble("Price");
            dataset.addValue(price, "Device Prices", deviceName);
        }

        JFreeChart chart = ChartFactory.createBarChart(
            title,
            "Device Name",
            "Price",
            dataset,
            PlotOrientation.VERTICAL,
            true,
            true,
            false
        );

        // Configure the x-axis (CategoryAxis) for label wrapping
        CategoryPlot plot = chart.getCategoryPlot();
        CategoryAxis xAxis = plot.getDomainAxis();
        xAxis.setCategoryLabelPositions(CategoryLabelPositions.UP_45);
        xAxis.setMaximumCategoryLabelWidthRatio(0.5f);

        ChartFrame frame = new ChartFrame(title, chart);
        chart.setBackgroundPaint(Color.WHITE);
        frame.pack();
        frame.setVisible(true);
    } catch (Exception e) {
        e.printStackTrace();
    }
}

	
	public void barChart3(ResultSet rs, String title) {
	    try {
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        while (rs.next()) {
	            String category = rs.getString("Consumption_Category");
	            int count = rs.getInt("Count");
	            dataset.addValue(count, "Count", category);
	        }

	        JFreeChart chart = ChartFactory.createBarChart(
	            title,
	            "Consumption Category",
	            "Count",
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void lineChart(ResultSet rs, String title) {
	    try {
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        while (rs.next()) {
	            String user_id = rs.getString("user_id");
	            double maxPeakHours = rs.getDouble("Max_PeakHours");
	            double minPeakHours = rs.getDouble("Min_PeakHours");

	            dataset.addValue(maxPeakHours, "Maximum Peak Hours", user_id);
	            dataset.addValue(minPeakHours, "Minimum Peak Hours", user_id);
	        }

	        JFreeChart chart = ChartFactory.createLineChart(
	            title,
	            "User ID",
	            "Peak Hours",
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void barChart1(ResultSet rs, String title) {
	    try {
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        while (rs.next()) {
	            String user_id = rs.getString("user_id");
	            double avgEfficiency = rs.getDouble("Avg_Efficiency");
	            double maxEfficiency = rs.getDouble("Max_Efficiency");
	            double minEfficiency = rs.getDouble("Min_Efficiency");

	            dataset.addValue(avgEfficiency, "Average Efficiency", user_id);
	            dataset.addValue(maxEfficiency, "Maximum Efficiency", user_id);
	            dataset.addValue(minEfficiency, "Minimum Efficiency", user_id);
	        }

	        JFreeChart chart = ChartFactory.createBarChart(
	            title,
	            "User ID",
	            "Efficiency",
	            dataset,
	            PlotOrientation.VERTICAL,
	            true,
	            true,
	            false
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}



	public void barChart(ResultSet rs, String title) {
	    try {
	        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

	        while (rs.next()) {
	            String category = rs.getString("user_id"); 
	            double value = rs.getDouble("Energy_consumption"); 
	            dataset.addValue(value, "Energy Consumption", category);
	        }

	        JFreeChart chart = ChartFactory.createBarChart(
	            title,
	            "User ID",
	            "Energy Consumption",
	            dataset,
	            PlotOrientation.VERTICAL, // Plot orientation
	            true,  // Show legend
	            true,  // Use tooltips
	            false  // Generate URLs
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}


	public void pieGraph(ResultSet rs, String title) {
	    try {
	        DefaultPieDataset dataset = new DefaultPieDataset();

	        while (rs.next()) {
	            String category = rs.getString(1);
	            double value = rs.getDouble(2); // Retrieve the numeric value as a double
	            dataset.setValue(category, value); // Use the retrieved value as-is
	        }

	        JFreeChart chart = ChartFactory.createPieChart(
	            title,
	            dataset,
	            false,
	            true,
	            true
	        );

	        ChartFrame frame = new ChartFrame(title, chart);
	        chart.setBackgroundPaint(Color.WHITE);
	        frame.pack();
	        frame.setVisible(true);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	
	private void writeToFile(ResultSet rs){
		try{
			System.out.println("In writeToFile");
			FileWriter outputFile = new FileWriter("output.csv");
			PrintWriter printWriter = new PrintWriter(outputFile);
			ResultSetMetaData rsmd = rs.getMetaData();
			int numColumns = rsmd.getColumnCount();

			for(int i=0;i<numColumns;i++){
				printWriter.print(rsmd.getColumnLabel(i+1)+",");
			}
			printWriter.print("\n");
			while(rs.next()){
				for(int i=0;i<numColumns;i++){
					printWriter.print(rs.getString(i+1)+",");
				}
				printWriter.print("\n");
				printWriter.flush();
			}
			printWriter.close();
		}
		catch(Exception e){e.printStackTrace();}
	}
}
