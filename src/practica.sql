String sql = "create table capitulo(id_serie int, n_temporada int, n_orden int, fecha_estreno date, titulo varchar(100), duracion int,"
		+ "PRIMARY KEY(id_serie, n_temporada,n_orden)," 
		+ "FOREIGN KEY (id_serie, n_temporada) REFERENCES temporada(id_serie,n_temporada) ON DELETE CASCADE ON UPDATE CASCADE);";
        String sql = "create table valora(id_serie int, n_temporada int, n_orden int, id_usuario int, fecha date, valor int,"
		+ "FOREIGN KEY (id_serie,n_temporada,n_orden) REFERENCES capitulo(id_serie,n_temporada,n_orden) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "FOREIGN KEY (id_usuario) REFERENCES usuario(id_usuario) ON DELETE CASCADE ON UPDATE CASCADE,"
		+ "PRIMARY KEY(id_serie,n_temporada, n_orden, id_usuario, fecha));";
        String sql = "Insert into valora (id_serie,n_temporada,n_orden,id_usuario,fecha,valor) Values(?,?,?,?,?,?);";
        ResultSet rs = st.executeQuery("SELECT nombre, apellido1, apellido2 FROM usuario " +
                                "LEFT JOIN comenta ON usuario.id_usuario = comenta.id_usuario" +
                                 " WHERE comenta.id_usuario IS NULL ORDER BY apellido1 ASC;");
                                 