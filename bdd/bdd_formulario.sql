-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: localhost
-- Generation Time: Nov 21, 2025 at 02:02 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `bdd_formulario`
--

-- --------------------------------------------------------

--
-- Table structure for table `antecedentes`
--

CREATE TABLE `antecedentes` (
  `idantecedentes` int(11) NOT NULL,
  `id_sujeto` varchar(4) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `id_dato` int(11) NOT NULL,
  `valor_string` varchar(255) DEFAULT NULL,
  `valor_num` float NOT NULL,
  `id_variable` int(11) NOT NULL,
  `id_sujeto_fk` varchar(255) DEFAULT NULL,
  `tipo_fk` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `antecedentes`
--

INSERT INTO `antecedentes` (`idantecedentes`, `id_sujeto`, `tipo`, `id_dato`, `valor_string`, `valor_num`, `id_variable`, `id_sujeto_fk`, `tipo_fk`) VALUES
(1, '0002', 'CO', 9, '', 125, 0, NULL, NULL),
(2, '0002', 'CA', 7, NULL, 9, 0, NULL, NULL);

--
-- Triggers `antecedentes`
--
DELIMITER $$
CREATE TRIGGER `ante_delete-audittrigger` AFTER DELETE ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('DELETE en tabla antecedentes, previo id ', CONVERT(OLD.idantecedentes,char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NULL, 0, OLD.id_sujeto, OLD.tipo, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `ante_insert-audittrigger` AFTER INSERT ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('INSERT en tabla antecedentes, id ', CONVERT(NEW.idantecedentes,char));

    INSERT INTO usuario_sujeto (id_usuario, id_sujeto, tipo, fecha, accion)
    VALUES (0, NEW.id_sujeto, NEW.tipo, NULL, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `ante_update-audittrigger` AFTER UPDATE ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('UPDATE en tabla antecedentes, id ', CONVERT(NEW.idantecedentes,char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NULL, 0, NEW.id_sujeto, NEW.tipo, v_action);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `criterio`
--

CREATE TABLE `criterio` (
  `id_criterio` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `nombre_stata` varchar(255) DEFAULT NULL,
  `tipo_calculo` enum('GENERAL','PARTICULAR') NOT NULL,
  `leyenda` varchar(255) DEFAULT NULL,
  `expresion` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `criterio`
--

INSERT INTO `criterio` (`id_criterio`, `nombre`, `nombre_stata`, `tipo_calculo`, `leyenda`, `expresion`) VALUES
(1, 'Promedio edad', 'promedio_edad', 'GENERAL', 'Entrega 0 si sujeto está bajo del promedio y 1 si sobre o igual al promedio', '<>'),
(2, 'Cantidad de 0', NULL, 'GENERAL', 'Cuenta el numero de 0 en una fila', '<>');

-- --------------------------------------------------------

--
-- Table structure for table `criterio_datosolicitado`
--

CREATE TABLE `criterio_datosolicitado` (
  `id_criterio` int(11) NOT NULL,
  `id_dato` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `criterio_datosolicitado`
--

INSERT INTO `criterio_datosolicitado` (`id_criterio`, `id_dato`) VALUES
(1, 7),
(2, 3),
(2, 2);

-- --------------------------------------------------------

--
-- Table structure for table `datosolicitado`
--

CREATE TABLE `datosolicitado` (
  `id_dato` int(11) NOT NULL,
  `nombre` varchar(255) NOT NULL,
  `nombre_stata` varchar(255) DEFAULT NULL,
  `leyenda` varchar(255) DEFAULT NULL,
  `aplicable_a` enum('CASO','CONTROL','AMBOS') NOT NULL DEFAULT 'AMBOS',
  `estudio` tinyint(1) NOT NULL,
  `id_seccion` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `datosolicitado`
--

INSERT INTO `datosolicitado` (`id_dato`, `nombre`, `nombre_stata`, `leyenda`, `aplicable_a`, `estudio`, `id_seccion`) VALUES
(2, 'Sexo', 'sexo', 'Sexo del paciente', 'AMBOS', 1, 1),
(3, 'Zona', 'zona', 'Zona de residencia', 'AMBOS', 1, 1),
(4, 'Años viviendo en la residencia actual', 'anos_residencia', 'Tiempo de residencia en el domicilio actual', 'AMBOS', 1, 1),
(5, 'Antecedentes familiares de cáncer gástrico', 'antecedentes_gastrico', 'Presencia de antecedentes familiares de cáncer gástrico', 'CONTROL', 1, 2),
(6, 'Antecedentes familiares de otro tipo de cancer', 'antecedentes_otro', 'Presencia de antecedentes familiares de otros tipos de cáncer', 'CASO', 1, 2),
(7, 'Edad', 'edad', 'Edad del sujeto en años', 'AMBOS', 1, 1),
(8, 'Peso', 'peso', 'Peso del sujeto en kg', 'AMBOS', 1, 1),
(9, 'Altura', 'altura', 'Altura del sujeto en cm', 'AMBOS', 1, 1);

-- --------------------------------------------------------

--
-- Table structure for table `dato_criterio`
--

CREATE TABLE `dato_criterio` (
  `id_dato` int(11) NOT NULL,
  `id_criterio` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `opcion`
--

CREATE TABLE `opcion` (
  `id_opcion` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `valor` int(1) NOT NULL,
  `id_dato` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `opcion`
--

INSERT INTO `opcion` (`id_opcion`, `nombre`, `valor`, `id_dato`) VALUES
(1, 'Hombre', 0, 2),
(2, 'Mujer', 1, 2),
(3, 'Urbana', 0, 3),
(4, 'Rural', 1, 3);

-- --------------------------------------------------------

--
-- Table structure for table `seccion`
--

CREATE TABLE `seccion` (
  `id_seccion` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `seccion`
--

INSERT INTO `seccion` (`id_seccion`, `nombre`, `numero`) VALUES
(1, 'Preguntas generales', 1),
(2, 'Antecedentes médicos', 2);

-- --------------------------------------------------------

--
-- Table structure for table `sujetoestudio`
--

CREATE TABLE `sujetoestudio` (
  `id_sujeto` varchar(4) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `ocupacion` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `correo` text DEFAULT NULL,
  `nacionalidad` varchar(255) DEFAULT NULL,
  `email` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sujetoestudio`
--

INSERT INTO `sujetoestudio` (`id_sujeto`, `tipo`, `nombre`, `direccion`, `ocupacion`, `telefono`, `correo`, `nacionalidad`, `email`) VALUES
('0001', 'CA', 'test caso 1', 'aaaaaaaaaaa', NULL, NULL, NULL, 'aaaaa', NULL),
('0001', 'CO', 'test control 1', 'aaaaaaaaa', NULL, 'aaaaa', NULL, 'aaaaaaa', NULL),
('0002', 'CA', 'test caso 2', 'aaaaaaaaaa', NULL, 'aaaaaaaaa', NULL, NULL, NULL),
('0002', 'CO', 'test control 2', NULL, NULL, NULL, 'aaaaaaaaaaaaa', NULL, NULL),
('0003', 'CA', 'testing triggers', NULL, NULL, NULL, NULL, NULL, NULL),
('0003', 'CO', 'sujeto testing crud', 'casa', NULL, NULL, NULL, NULL, NULL),
('0004', 'CO', 'juan control 1', 'casa 543135', 'existe', '1', NULL, 'Chile', 'jc1@gmail.com'),
('0005', 'CO', 'juan control 2', 'casa 543135', 'existe', '1', NULL, 'Chile', 'jc2@gmail.com'),
('0006', 'CO', 'juan control 3', 'casa 543135', 'existe', '1', NULL, 'Chile', 'jc3@gmail.com'),
('0007', 'CO', 'juan control 4', 'casa 543135', 'existe', '1', NULL, 'Chile', 'jc4@gmail.com'),
('0008', 'CO', 'juan control', 'casa 543135', 'existe', '1', NULL, 'Chile', 'jc@gmail.com');

--
-- Triggers `sujetoestudio`
--
DELIMITER $$
CREATE TRIGGER `suje_delete-audittrigger` AFTER DELETE ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('DELETE en tabla sujetoestudio, previo id ', CONVERT(OLD.tipo, char), CONVERT(OLD.id_sujeto, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NULL, 0, OLD.id_sujeto, OLD.tipo, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `suje_insert-audittrigger` AFTER INSERT ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('INSERT en tabla sujetoestudio,  id ', CONVERT(NEW.tipo, char), CONVERT(NEW.id_sujeto, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NULL, 0, NEW.id_sujeto, NEW.tipo, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `suje_update-audittrigger` AFTER UPDATE ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('UPDATE en tabla sujetoestudio, id ', CONVERT(NEW.tipo, char), CONVERT(NEW.id_sujeto, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NULL, 0, NEW.id_sujeto, NEW.tipo, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `sujeto_crearid` BEFORE INSERT ON `sujetoestudio` FOR EACH ROW BEGIN
	SET NEW.id_sujeto = (
        SELECT LPAD(IFNULL(COUNT(id_sujeto), 0) + 1, 4, '0')
        FROM sujetoestudio
        WHERE tipo = NEW.tipo
    );
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `usuario`
--

CREATE TABLE `usuario` (
  `id_usuario` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `contraseña` varchar(255) DEFAULT NULL,
  `correo` varchar(255) DEFAULT NULL,
  `estado` enum('INICIADO','ACTIVO','SUSPENDIDO','DADO_DE_BAJA') NOT NULL,
  `rol` enum('ADMINISTRADOR','RECOLECTOR_DE_DATOS','ANALISTA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `contraseña`, `correo`, `estado`, `rol`) VALUES
(1, 'admin test', '$2y$10$CCSBXN3IxnfySxfcVfWdgO9EYhRKFpBuW9bWwAUykdAcV5WL48gUS', 'correo@ubiobio.cl', 'SUSPENDIDO', 'ADMINISTRADOR'),
(2, 'Recolector test', '$2y$10$35ThpCNDehBpwCh2K9wcx.9g/6qcybBSjJ4vf98CLsB8qIubzTdbq', 'correo@gmail.com', 'INICIADO', 'RECOLECTOR_DE_DATOS'),
(202, 'juanito perez', '$2a$04$wTeCPgmWrrDlfSPQNaWzvOzAGxkdOo.iivKgKKzDTm1tmE7k7ceQm', 'recolector@correo.cl', 'ACTIVO', 'RECOLECTOR_DE_DATOS'),
(252, 'Usuariotest', '$2a$10$Gr8u6UFfcv1QVqmE9SOhGOdxVxZP8S1jpnuFZ8Dkv4FlLDe/rFRmi', 'correo@test.cl', 'INICIADO', 'RECOLECTOR_DE_DATOS'),
(1205, 'test analista', '123123', 'analista@correo.cl', 'ACTIVO', 'ANALISTA'),
(1206, 'admin test?', '$2a$04$NM3HeJbccz.RdZtfnkMkk.vRV0qeU3eylNzNzy2sbZw0Jt9MuK9a6', 'admin@correo.cl', 'ACTIVO', 'ADMINISTRADOR'),
(1207, 'admin', '$2a$04$kok8/LCyeRN3TfI0dLGRnufSjH8JQoZ7CqXEwDH3CZfBKrEiTRQAC', 'aaa@correo.cl', 'ACTIVO', 'ADMINISTRADOR');

-- --------------------------------------------------------

--
-- Table structure for table `usuario_seq`
--

CREATE TABLE `usuario_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuario_seq`
--

INSERT INTO `usuario_seq` (`next_val`) VALUES
(1258);

-- --------------------------------------------------------

--
-- Table structure for table `usuario_sujeto`
--

CREATE TABLE `usuario_sujeto` (
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `id_usuario` int(11) NOT NULL,
  `id_sujeto` varchar(255) NOT NULL,
  `tipo` varchar(255) NOT NULL,
  `accion` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuario_sujeto`
--

INSERT INTO `usuario_sujeto` (`fecha`, `id_usuario`, `id_sujeto`, `tipo`, `accion`) VALUES
('2025-11-20 22:39:42', 0, '0004', 'CO', 'UPDATE en tabla sujetoestudio, id CO0004'),
('2025-11-20 22:39:59', 0, '0004', 'CO', 'UPDATE en tabla sujetoestudio, id CO0004'),
('2025-11-20 22:40:05', 0, '0005', 'CO', 'INSERT en tabla sujetoestudio,  id CO0005'),
('2025-11-20 22:41:12', 0, '0005', 'CO', 'UPDATE en tabla sujetoestudio, id CO0005'),
('2025-11-20 22:41:19', 0, '0006', 'CO', 'INSERT en tabla sujetoestudio,  id CO0006'),
('2025-11-20 22:52:16', 0, '0006', 'CO', 'UPDATE en tabla sujetoestudio, id CO0006'),
('2025-11-20 23:10:39', 0, '0007', 'CO', 'INSERT en tabla sujetoestudio,  id CO0007'),
('2025-11-20 23:23:08', 0, '0007', 'CO', 'UPDATE en tabla sujetoestudio, id CO0007'),
('2025-11-20 23:23:18', 0, '0008', 'CO', 'INSERT en tabla sujetoestudio,  id CO0008');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD PRIMARY KEY (`idantecedentes`),
  ADD KEY `antec_sujeto_fk` (`id_sujeto`,`tipo`),
  ADD KEY `antec_dato` (`id_dato`),
  ADD KEY `FK6eibdqtyduacdiky85wxey6np` (`id_sujeto_fk`,`tipo_fk`);

--
-- Indexes for table `criterio`
--
ALTER TABLE `criterio`
  ADD PRIMARY KEY (`id_criterio`),
  ADD UNIQUE KEY `nombre` (`nombre`);

--
-- Indexes for table `criterio_datosolicitado`
--
ALTER TABLE `criterio_datosolicitado`
  ADD KEY `cd_criterio_fk` (`id_criterio`),
  ADD KEY `cd_dato_fk` (`id_dato`);

--
-- Indexes for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  ADD PRIMARY KEY (`id_dato`),
  ADD UNIQUE KEY `nombre` (`nombre`),
  ADD KEY `datosolicitado_fk` (`id_seccion`);

--
-- Indexes for table `dato_criterio`
--
ALTER TABLE `dato_criterio`
  ADD PRIMARY KEY (`id_dato`,`id_criterio`),
  ADD KEY `FK8mnb3ephw9g4mx2bd95kmbi1c` (`id_criterio`);

--
-- Indexes for table `opcion`
--
ALTER TABLE `opcion`
  ADD PRIMARY KEY (`id_opcion`),
  ADD KEY `opcion_fk` (`id_dato`);

--
-- Indexes for table `seccion`
--
ALTER TABLE `seccion`
  ADD PRIMARY KEY (`id_seccion`);

--
-- Indexes for table `sujetoestudio`
--
ALTER TABLE `sujetoestudio`
  ADD PRIMARY KEY (`id_sujeto`,`tipo`),
  ADD UNIQUE KEY `nombre` (`nombre`),
  ADD UNIQUE KEY `UK1ssmimuvpqhi2cff1diy3t17t` (`nombre`);

--
-- Indexes for table `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`),
  ADD UNIQUE KEY `correo` (`correo`);

--
-- Indexes for table `usuario_sujeto`
--
ALTER TABLE `usuario_sujeto`
  ADD KEY `us_usuario_fk` (`id_usuario`),
  ADD KEY `us_sujeto_fk` (`id_sujeto`,`tipo`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `antecedentes`
--
ALTER TABLE `antecedentes`
  MODIFY `idantecedentes` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `criterio`
--
ALTER TABLE `criterio`
  MODIFY `id_criterio` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  MODIFY `id_dato` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `opcion`
--
ALTER TABLE `opcion`
  MODIFY `id_opcion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=5;

--
-- AUTO_INCREMENT for table `seccion`
--
ALTER TABLE `seccion`
  MODIFY `id_seccion` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=1208;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD CONSTRAINT `FK6eibdqtyduacdiky85wxey6np` FOREIGN KEY (`id_sujeto_fk`,`tipo_fk`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`),
  ADD CONSTRAINT `antec_dato_fk` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`),
  ADD CONSTRAINT `antec_sujeto_fk` FOREIGN KEY (`id_sujeto`,`tipo`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`);

--
-- Constraints for table `criterio_datosolicitado`
--
ALTER TABLE `criterio_datosolicitado`
  ADD CONSTRAINT `cd_criterio_fk` FOREIGN KEY (`id_criterio`) REFERENCES `criterio` (`id_criterio`),
  ADD CONSTRAINT `cd_dato_fk` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`);

--
-- Constraints for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  ADD CONSTRAINT `datosolicitado_fk` FOREIGN KEY (`id_seccion`) REFERENCES `seccion` (`id_seccion`);

--
-- Constraints for table `dato_criterio`
--
ALTER TABLE `dato_criterio`
  ADD CONSTRAINT `FK81xdky0vnbjtvg99r516gfint` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`),
  ADD CONSTRAINT `FK8mnb3ephw9g4mx2bd95kmbi1c` FOREIGN KEY (`id_criterio`) REFERENCES `criterio` (`id_criterio`);

--
-- Constraints for table `opcion`
--
ALTER TABLE `opcion`
  ADD CONSTRAINT `opcion_fk` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`);

--
-- Constraints for table `usuario_sujeto`
--
ALTER TABLE `usuario_sujeto`
  ADD CONSTRAINT `FKh7wrd122riicrk8b1axewvgoj` FOREIGN KEY (`id_sujeto`,`tipo`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
