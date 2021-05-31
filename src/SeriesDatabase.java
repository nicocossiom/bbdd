
//package series.mysqlbbddd.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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
		String username = "root";
		String passwd = "ottoterco1";
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
		openConnection();
		String sql = "create table capitulo(id_serie int, n_temporada int, n_orden int, fecha_estreno date, titulo varchar(100), duracion int,"
		+ "PRIMARY KEY(id_serie, n_temporada,n_orden)," 
		+ "FOREIGN KEY (id_serie, n_temporada) REFERENCES temporada(id_serie,n_temporada) ON DELETE CASCADE ON UPDATE CASCADE);";
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
		openConnection();
		String sql = "create table valora(id_serie int, n_temporada int, n_orden int, id_usuario int, fecha date, valor int,"
		+ "FOREIGN KEY (id_serie,n_temporada,n_orden) REFERENCES capitulo(id_serie,n_temporada,n_orden) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "PRIMARY KEY(id_serie,n_temporada, n_orden, id_usuario, fecha));";
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
		openConnection();
		ArrayList<Capitulo> csv = readData(fileName, "capitulo");
		String sql = "Insert into capitulo (id_serie,n_temporada,n_orden,fecha_estreno,titulo,duracion) Values(?,?,?,?,?,?);";
		try {
			con.setAutoCommit(false);
			PreparedStatement st = con.prepareStatement(sql);
			for(Capitulo cap : csv){
				st.setInt(1, cap.id_serie);
				st.setInt(2, cap.n_temporada);
				st.setInt(3, cap.n_orden);
				st.setDate(4,cap.fecha_estreno);
				st.setString(5, cap.titulo);
				st.setInt(6, cap.duracion);	
				st.executeUpdate();
			}
			con.commit();
			con.setAutoCommit(true);
			st.close();
		} catch (SQLException e) {
			try {
				con.rollback();
				con.setAutoCommit(true);
				if(st!=null){ 
					st.close();	
				}
			} catch (SQLException e1) {
			}
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
			return -1;
		}
		return csv.size();
	}
	
	public int loadValoraciones(String fileName) {
		openConnection();
		ArrayList<Valoracion> csv = readData(fileName, "Valoracion");
		String sql = "Insert into valora (id_serie,n_temporada,n_orden,id_usuario,fecha,valor) Values(?,?,?,?,?,?);";
		try {
			con.setAutoCommit(false);
			PreparedStatement st = con.prepareStatement(sql);
			for(Valoracion cap : csv){
				System.out.println(cap.id_serie + cap.id_serie + cap.n_temporada + cap.n_orden+ cap.id_usuario+cap.valor);
				st.setInt(1, cap.id_serie);
				st.setInt(2, cap.n_temporada);
				st.setInt(3, cap.n_orden);
				st.setInt(4,cap.id_usuario);
				st.setDate(5, cap.fecha);
				st.setInt(6, cap.valor);
				st.executeUpdate();
			}
			con.commit();
			con.setAutoCommit(true);
			st.close();
		} catch (SQLException e) {
			System.out.println("Error message: " + e.getMessage());
			System.out.println("Error code: " + e.getErrorCode());
			System.out.println("SQL state: " + e.getSQLState());
			e.printStackTrace();
			return -1;
		}

		return csv.size();
	}

	public String catalogo() {
		openConnection();
		String result = "{";
		String sql = " ";
		Boolean stop = false;
		Map<String, ArrayList<Integer>> map = new HashMap<String, ArrayList<Integer>>();
		try{ 
			for (int i=1;!stop ; i++){
				sql = "Select serie.titulo from (capitulo join serie on serie.id_serie = capitulo.id_serie) where capitulo.n_temporada=" + i + " AND capitulo.id_serie=" + i; 
				ResultSet resultado = st.executeQuery(sql);
				ArrayList<Integer> nums = new ArrayList<Integer>();
				nums.add(resultado.getRow());
				map.put(resultado.getString(i), nums);
			} 
		}catch (SQLException e) {
			stop = true;
		}
		for(Map.Entry<String, ArrayList<Integer>> entry : map.entrySet()){
			result += entry.getKey() + ": [";
			for(Integer v : entry.getValue()){
				result += v + ", ";
			}
			result+= "]} \n";
		}
		return result;
	}

	public String noHanComentado() {
		String result = "[";
		try {
			openConnection();
			Statement st = con.createStatement();
			ResultSet rs = st.executeQuery("SELECT nombre, apellido1, apellido2 FROM usuario " +
                                "LEFT JOIN comenta ON usuario.id_usuario = comenta.id_usuario" +
                                 " WHERE comenta.id_usuario IS NULL ORDER BY apellido1 ASC;");
			while(rs.next()){
				result += result + rs.getString("nombre") + " " + rs.getString("apellido1") + " " + rs.getString("apellido2") + ", ";
			}
		}
		catch(SQLException e){
			return null;
		}
		return result;
	}

	public double mediaGenero(String genero) {
		return 0.0;
	}

	public double duracionMedia(String idioma) {
		return 0.0;
	}

	public boolean setFoto(String filename) {
		PreparedStatement pst = null;
		ResultSet rs = null;
		File f = null;
		FileInputStream fis = null;

		try{
			pst = con.prepareStatement("UPDATE usuatio SET foto = ? WHERE id_usuario = ?;");
			f = new File ("HomerSimpson.jpg");
			fis = new FileInputStream(f);
		}
		catch (SQLException | FileNotFoundException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private <E> ArrayList<E> readData(String file, String tipo){
		File f = new File(file);
		ArrayList<E> result=null;
		Scanner fileScanner=null;
		try {
			fileScanner = new Scanner(f);
			fileScanner.nextLine();
				if (tipo.equals("Valoracion")){
					result = (ArrayList<E>) new ArrayList<Valoracion>();
					while(fileScanner.hasNextLine()){ 
						String[] att = fileScanner.nextLine().split(";");
						Date tiempo = Date.valueOf(att[4]);
						System.out.println(att[3]);
						Valoracion v = new Valoracion(Integer.valueOf(att[0]), Integer.valueOf(att[1]), Integer.valueOf(att[2]), Integer.valueOf(att[3]), tiempo, Integer.valueOf(att[5]));
						((ArrayList<SeriesDatabase.Valoracion>) result).add(v);
					}
				}
				else{
					result = (ArrayList<E>) new ArrayList<Capitulo>();
					while(fileScanner.hasNextLine()){ 
						String[] att = fileScanner.nextLine().split(";");
						System.out.println(att[3]);
						Date tiempo = Date.valueOf(att[3]);
						Capitulo c = new Capitulo(Integer.valueOf(att[0]), Integer.valueOf(att[1]), Integer.valueOf(att[2]), tiempo, att[4], Integer.valueOf(att[5]));
						((ArrayList<SeriesDatabase.Capitulo>) result).add(c);
					}
				}
			fileScanner.close();
		} catch (FileNotFoundException e) {
			System.out.println("No se puede leer el archivo");
		}
		return result;
	}		
	
	static private class Capitulo{
		int id_serie;
		int n_temporada;
		int n_orden;
		Date fecha_estreno;
		String titulo;
		int duracion;

		public Capitulo(int id_serie,int n_temporada,int n_orden, Date fecha_estreno, String titulo,int duracion){
			this.id_serie = id_serie;
			this.n_temporada = n_temporada;
			this.n_orden = n_orden;
			this.fecha_estreno = fecha_estreno;
			this.titulo = titulo;
			this.duracion = duracion;
		}
	}	
	
	static private class Valoracion{
		Integer id_serie; 
		Integer n_temporada;
		Integer n_orden;
		Integer id_usuario;
		Date fecha;
		Integer valor;
		
		public Valoracion(int id_serie, int n_temporada, int n_orden, int id_usuario, Date fecha, int valor){
		this.id_serie = id_serie;
		this.n_temporada = n_temporada;
		this.n_orden = n_orden;
		this.id_usuario = id_usuario;
		this.fecha = fecha;
		this.valor = valor;
		}
	}



	}
