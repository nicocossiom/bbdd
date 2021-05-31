
//package series.mysqlbbddd.src;

import java.io.File;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.Scanner;


public class SeriesDatabase {

	public SeriesDatabase() {

	}
	private static Connection con = null;
	private Statement st= null;


	public boolean openConnection() {
		String DBNAME = "series";
		String url = "jdbc:mysql://localhost:3306/" + DBNAME;
		String driver = "com.mysql.jdbc.Driver";
		String username = "series_user";
		String passwd = "series_pass";
		try {
			con = DriverManager.getConnection(url, username, passwd);
		} catch (SQLException e) {
			System.out.println("Cannot connect the database!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
			return false;
		}
		System.out.println("Connected to " + DBNAME + " database!");
		return true;
	}

	public boolean closeConnection() {
		try {
			con.close();
		} catch (SQLException e) {
			System.out.println("Cannot close connection to database!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
		}
		catch(NullPointerException e){	
			System.out.println("Error: Can't disconnect if not connected first");
			return false;
		}
		System.out.println("Disconnected from database!");
		return true;
	}

	public boolean createTableCapitulo() {
		String sql = "create table capitulo(n_orden int, titulo varchar(100), duraction int, fecha_estreno date,"
		+ "PRIMARY KEY(n_orden));";
		try {
			st = con.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Couldn't create table capitulo!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
			return false;
		}
		System.out.println("Table capitulo created correctly");
		return true;
	}

	public boolean createTableValora() {
		String sql = "create table valora(valor int, fecha date, id_usuario int, n_orden int,"
		+ "FOREIGN KEY (n_orden) REFERENCES capitulo(n_orden) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "PRIMARY KEY(id_usuario, n_orden));";
		try {
			st = con.createStatement();
			st.executeUpdate(sql);
		} catch (SQLException e) {
			System.out.println("Couldn't create table capitulo!");
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
			return false;
		}
		System.out.println("Table valora created correctly");
		return true;
	}

	

	public int loadCapitulos(String fileName) {
		ArrayList<Capitulo> csv = readData(fileName);
		String sql = "Insert into capitulos (id_serie,n_temporada,n_orden,id_usuario,fecha,valor) Values(?,?,?,?,?,?)";
		try {
			con.setAutoCommit(false);
			PreparedStatement st = con.prepareStatement(sql);
			for(String[] line : csv){
				// st.setInt(1,line[0].);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return 0;
	}
	
	
	public int loadValoraciones(String fileName) {
		ArrayList<Valoracion> csv = readData(fileName);
		int numInserciones = csv.size();
		int contador = 0;
		while (contador<=numInserciones){
			String sql = " ";  
		}
		return 0;
	}

	public String catalogo() {
		return null;
	}

	public String noHanComentado() {
		return null;
	}

	public double mediaGenero(String genero) {
		return 0.0;
	}

	public double duracionMedia(String idioma) {
		return 0.0;
	}

	public boolean setFoto(String filename) {
		return false;
	}

	private ArrayList<String[]> readData(String file){
		File f = new File(file);
		ArrayList<String[]> result = new ArrayList<String[]>();
		Scanner fileScanner=null;
		try {
			fileScanner = new Scanner(f);
			while(fileScanner.hasNextLine()){ 
				String[] attributes = fileScanner.nextLine().split(";");
				result.add(attributes);
			}
		} catch (FileNotFoundException e) {
			System.out.println("No se puede leer el archivo");
		}
		fileScanner.close();
		return result;
	}		
	
	static private class Capitulo(){
		int id_serie;
		int n_temporada;
		int n_orden;
		String fecha_estreno;
		String titulo;
		int duracion;

		public Capitulo(int id_serie,int n_temporada,int n_orden, String fecha_estreno, String titulo,int duracion){
			this.id_serie = id_serie;
			this.n_temporada = n_temporada;
			this.fecha_estreno = fecha_estreno;
			this.titulo = titulo;
			this.duracion = duracion;
		}
	}	
	
	private class Valoracion (){
		int id_serie; 
		int n_temporada;
		int n_orden;
		int id_usuario;
		String fecha;
		int valor;
		
		public valoracion(int id_serie, int n_temporada, int n_orden, int id_usuario, String fecha, int valor){
		this.id_serie = id_serie;
		this.n_temporada = n_temporada;
		this.n_orden = n_orden;
		this.id_usuario = id_usuario;
		this.fecha = fecha;
		this.valor = valor;
		}
	}



	public static void main(String[] args) {
		SeriesDatabase series = new SeriesDatabase();
		// System.out.println(series.openConnection());
		System.out.println(series.closeConnection());
		// series.createTableCapitulo();
		// series.createTableValora();
	}

}
