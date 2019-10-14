/*
 * LibraryModel.java
 * Author:
 * Created on:
 */



import javax.swing.*;
import java.sql.*;
import java.time.LocalDate;

public class LibraryModel {

	private Connection con = null;

	// For use in creating dialogs and making them modal
	private JFrame dialogParent;

	public LibraryModel(JFrame parent, String userid, String password) {
		this.dialogParent = parent;

		try{
			Class.forName("org.postgresql.Driver");
		}
		catch (ClassNotFoundException cnfe){
			System.out.println("Can not find"+
					"the driver class: "+
					"\nEither I have not installed it"+
					"properly or \n postgresql.jar "+
					" file is not in my");
		}

		try {
			con = DriverManager.getConnection("jdbc:postgresql://db.ecs.vuw.ac.nz/" + userid + "_jdbc",userid,password);
		} catch (SQLException e) {
			e.printStackTrace();
			System.err.println("Error:" + e.getMessage());
		}
		System.out.println("You have connected to the database");
	}



	public String bookLookup(int isbn) {
		StringBuilder output = new StringBuilder("Book Lookup:\n"+"\n");
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		try{
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM book WHERE isbn = "+ isbn+";");

			s2 = con.createStatement();
			rs2 = s2.executeQuery("SELECT name,surname FROM book NATURAL JOIN book_author NATURAL JOIN author WHERE isbn ="+ isbn+";");

		

			while (rs.next()){
				output.append("\t" +"ISBN: "+ rs.getInt(1) + "\n"+ // ISBN
						"     Book Title: " + rs.getString(2).trim() + "\n"); // Book title
				output.append("\tEdition: " + rs.getInt(3)
				+ " - Number of copies: " + rs.getInt(4)
				+ " - Copies left: " + rs.getInt(5) + "\n" + "\n");
			}
			output.append("Names of Authors: "+"\n" +"\n");
			while (rs2.next()){
				output.append("\t" +"First Name:"+rs2.getString(1) +"Last Name:"+ rs2.getString(2)+"\n");
			}
		}
		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}
		return output.toString();

	}

	public String showCatalogue() {
		StringBuilder output = new StringBuilder("Show Catalogue:\n" + "\n");
		Statement s = null;
		ResultSet rs = null;

		try{
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM book");


			while (rs.next()){
				output.append("\t" +"ISBN: "+ rs.getInt(1) + "\n"+ // ISBN
						"     Book Title: " + rs.getString(2).trim() + "\n"); // Book title
				output.append("\tEdition: " + rs.getInt(3)
				+ " - Number of copies: " + rs.getInt(4)
				+ " - Copies left: " + rs.getInt(5) + "\n" + "\n");
			}
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}
		return output.toString();
	}

	public String showLoanedBooks() {


		StringBuilder output = new StringBuilder("Show loaned books:\n" + "\n");
		Statement s = null;
		ResultSet rs = null;

		try{
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM cust_book NATURAL JOIN book");

			

			while (rs.next()){
				output.append("\t" + "ISBN: "+rs.getInt(1) // ISBN
				+ "\n " +"Date Due: "+ rs.getDate(2) + "\n" ); // Due Date
				output.append("\tcustomerID: " + rs.getInt(3));

			}
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}


		return output.toString();
	}

	public String showAuthor(int authorID) {
		String message = "";
		StringBuilder output = new StringBuilder("Author Lookup:\n");
		Statement s = null;
		ResultSet rs = null;

		try {

			s = con.createStatement();
			rs = s.executeQuery("SELECT name,surname FROM author WHERE authorID ="+authorID+";");
			

			while (rs.next()){
				output.append("\t" +"FirstName: "+ rs.getString(1) // name
				+"LastName:"+ rs.getString(2) + "\n" ); //surname

			}
			message = output.toString();
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}
		
		if(message.length() == 15) {
			message = "There are no authors with that id, please try again";
		}
		return message;
	}

	public String showAllAuthors() {

		StringBuilder output = new StringBuilder("Show all Authors:\n");
		Statement s = null;
		ResultSet rs = null;

		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT name,surname FROM author;");

			while (rs.next()){
				output.append("\t" + rs.getString(1) // name
				+ rs.getString(2) + "\n" ); //surname

			}
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}
		return output.toString();
	}

	public String showCustomer(int customerID) {
		StringBuilder output = new StringBuilder("Customer Lookup:\n");
		Statement s = null;
		ResultSet rs = null;
			
		try {

			s = con.createStatement();
			rs = s.executeQuery("SELECT f_name,l_name FROM customer WHERE customerid ="+customerID+";");
			
	
			
			while (rs.next()){
				output.append("\t" +"FirstName: "+ rs.getString(1) // name
				+ "LastName: "+rs.getString(2) + "\n" ); //surname

			}
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}

		return output.toString();
	}

	public String showAllCustomers() {
		StringBuilder output = new StringBuilder("Show all Customers:\n");
		Statement s = null;
		ResultSet rs = null;

		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT f_name,l_name FROM customer;");

			while (rs.next()){
				output.append("\t" + rs.getString(1) // name
				+ rs.getString(2) + "\n" ); //surname
			}
		}

		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}
		return output.toString();
	}

	public String borrowBook(int isbn, int customerID,
			int day, int month, int year) {
		StringBuilder output = new StringBuilder("");
		String message = "the customer was unable to rent the book";
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement s3 = null;
		ResultSet rs3 = null;

		try {

			s3 = con.createStatement();
			s = con.createStatement();
			rs  = s.executeQuery("SELECT numLeft FROM book  WHERE isbn = "+ isbn+"AND numLeft>0;");
			s2 =con.createStatement();
			if(rs.next()!=false) {

				rs2 = s2.executeQuery("SELECT customerid FROM cust_book WHERE isbn= "+ isbn+"AND customerid = "+customerID+";");
				if(rs2.next() !=false)message = "This custmer alreay has this book";
				else {
					message = "There are books left and we are updating your account";
					updateBook(isbn,customerID,day,month,year);
				}
			}
			else message = "There are no books";

			rs3 = s3.executeQuery("SELECT * FROM cust_book WHERE customerid = "+customerID+";");

			while (rs3.next()) {
				output.append("\t"+"\n" +"ISBN: "+rs3.getInt(1)+"\n"+" Date Due: " +rs3.getDate(2)+"\n"+" CustomerID: "+ rs3.getInt(3));
			}



		}
		catch (SQLException sqlex){
			System.out.println("An exception"+
					"while executing a query, probably"+
					"means my SQL is invalid");
		}

		return message +" \n" +"Here are the books that this customer has: " +output.toString();
	}
	public void updateBook(int isbn, int customerID,
			int day, int month, int year) {

		StringBuilder output = new StringBuilder("Show borrowed book:\n");
		Statement s = null;
		Statement s2 = null;
		int rs = 0;
		int rs2 =0;
		try {
			Date date = Date.valueOf(LocalDate.of(year, month, day));

			s = con.createStatement();
			s2 = con.createStatement();

			rs = s.executeUpdate("INSERT INTO cust_book VALUES('"+isbn+"','"+date+"','"+customerID+"');");

			rs2 = s2.executeUpdate("UPDATE book SET numleft = numleft-1 WHERE isbn ="+isbn+" ;");


		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}


	}

	public String returnBook(int isbn, int customerid) {
		StringBuilder output = new StringBuilder("");
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		ResultSet rs2 = null;
		Statement s3 = null;
		ResultSet rs3 = null;
		String message = "";

		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM cust_book where isbn ="+isbn+" AND customerid = "+customerid+";");

			if(rs.next()==false)message = "You do not currently have this book to return.";
			else {
				updateRemove(isbn, customerid);
				message =  "The book has been returned";
			}
		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}
		return message;
	}

	public void updateRemove(int isbn, int customerid) {

		Statement s = null;
		Statement s2 = null;
		int rs = 0;
		int rs2 = 0;
		try {
			s = con.createStatement();
			rs = s.executeUpdate("UPDATE book SET numleft = numleft+1 WHERE isbn ="+isbn+";");
			s2 = con.createStatement();
			rs2 = s2.executeUpdate("DELETE FROM cust_book WHERE isbn ="+isbn+" AND customerid = "+customerid+";");

		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}
	}

	public void closeDBConnection() {
	}

	public String deleteCus(int customerID) {

		String message = "";
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		int rs2 = 0;

		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM cust_book WHERE customerid = "+customerID+";");

			if(rs.next()!=false) {
				message  ="You cannout delete the customer because they still have books to return.";
			}
			else {
				s2 = con.createStatement();
				rs2 = s2.executeUpdate("DELETE  FROM customer WHERE customerid = "+customerID+";");
				message = "The Customer has been deleted from the database";
			}
		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}


		return message;
	}

	public String deleteAuthor(int authorID) {
		String message = "";
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		Statement s3 = null;
		int rs3 = 0;
		int rs2 = 0;

		try {
			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM author WHERE authorid = "+authorID+";");

			if(rs.next()==false) {
				message  ="This author does not exist and cannot be deleted.";
			}

			else {
				s2 = con.createStatement();
				s3 = con.createStatement();
				rs2 = s2.executeUpdate("DELETE FROM author WHERE authorid = "+authorID+";");

				message = "This author has been removed from the database";
			}
		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}

		return message;
	}

	public String deleteBook(int isbn) {
		String message = "";
		Statement s = null;
		Statement s2 = null;
		ResultSet rs = null;
		int rs2 = 0;


		try {

			s = con.createStatement();
			rs = s.executeQuery("SELECT * FROM book WHERE isbn = "+isbn+";");
			if(rs.next()== false) {
				message = "There are no books with that isbn";
			}
			else {

				if( rs.getInt(4) != rs.getInt(5)) message = "That book currently has one of for rent, please get it back before deleleting it";
				else {

					s2 = con.createStatement();
					rs2 = s2.executeUpdate("DELETE FROM book WHERE isbn ="+isbn+";");

					message = "The book has been deleted from the database";

				}
			}


		}
		catch (SQLException sqlex){
			sqlex.printStackTrace();
		}
		return message;
	}
}
