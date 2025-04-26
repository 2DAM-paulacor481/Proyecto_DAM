CREATE TABLE Rol (
    id_rol INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(20) NOT NULL
);

CREATE TABLE Usuario (
    id_usuario INT PRIMARY KEY AUTO_INCREMENT,
    nombre VARCHAR(50) NOT NULL,
    correo VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    id_rol INT NOT NULL,
    FOREIGN KEY (id_rol) REFERENCES Rol(id_rol) ON DELETE CASCADE
);

CREATE TABLE Evento (
    id_evento INT PRIMARY KEY AUTO_INCREMENT,
    titulo VARCHAR(100) NOT NULL,
    descripcion TEXT NOT NULL,
    fecha DATETIME NOT NULL,
    latitud DECIMAL(9,6) NOT NULL,
    longitud DECIMAL(9,6) NOT NULL,
    id_creador INT NOT NULL,
    categoria VARCHAR(50),
    FOREIGN KEY (id_creador) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

CREATE TABLE Participacion (
    id_participacion INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    id_evento INT NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE,
    FOREIGN KEY (id_evento) REFERENCES Evento(id_evento) ON DELETE CASCADE
);

CREATE TABLE Comentario (
    id_comentario INT PRIMARY KEY AUTO_INCREMENT,
    id_evento INT NOT NULL,
    id_usuario INT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (id_evento) REFERENCES Evento(id_evento) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);


CREATE TABLE Notificacion (
    id_notificacion INT PRIMARY KEY AUTO_INCREMENT,
    id_usuario INT NOT NULL,
    mensaje TEXT NOT NULL,
    fecha DATETIME DEFAULT CURRENT_TIMESTAMP,
    leido BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (id_usuario) REFERENCES Usuario(id_usuario) ON DELETE CASCADE
);

/*INSERTAR DATOS*/
INSERT INTO Rol VALUES (1, 'ADMINISTRADOR');
INSERT INTO Rol VALUES (2, 'USUARIO');

INSERT INTO Usuario (nombre, correo, password, id_rol) 
VALUES 
('Admin Eventos', 'admin@gmail.com', 'admin123', 1),
('Juan Pérez', 'juanperez@gmail.com', '123456', 2),
('Laura Gómez', 'laura.gomez@gmail.com', 'contra1234', 2);


INSERT INTO Evento (titulo, descripcion, fecha, latitud, longitud, id_creador, categoria)
VALUES 
('Concierto de RocK', 'Banda en vivo en Mairena del Aljarafe', '2025-02-15 20:00:00', 37.345429359235794, -6.058024120614675, 1, 'Música'),
('Charla de Tecnología', 'Conferencia sobre IA', '2025-03-20 18:00:00', 37.28355159663391, -5.922192290935783, 2, 'Tecnología'),
('Taller de Fotografía', 'Aprende técnicas de fotografía urbana', '2025-04-10 17:00:00', 37.388, -5.982, 3, 'Arte'),
('Feria Gastronómica', 'Comida internacional en el parque central', '2025-05-05 12:00:00', 37.379, -5.984, 4, 'Gastronomía'),
('Maratón 10K', 'Carrera popular por la ciudad', '2025-06-01 08:00:00', 37.389, -5.984, 5, 'Deporte'),
('Festival de Cine', 'Proyecciones al aire libre', '2025-07-15 21:00:00', 37.390, -5.990, 2, 'Cine');

INSERT INTO Participacion (id_usuario, id_evento) 
VALUES
(2,3),
(3,1);


