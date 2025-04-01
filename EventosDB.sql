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
('Juan Pérez', 'juanperez@email.com', '123456', 2),
('Admin Eventos', 'admin@email.com', 'admin123', 1);

INSERT INTO Evento (titulo, descripcion, fecha, latitud, longitud, id_creador, categoria)
VALUES 
('Concierto de RocK', 'Banda en vivo en Mairena del Aljarafe', '2025-02-15 20:00:00', 37.345429359235794, -6.058024120614675, 1, 'Música'),
('Charla de Tecnología', 'Conferencia sobre IA', '2025-03-20 18:00:00', 37.28355159663391, -5.922192290935783, 2, 'Tecnología');



