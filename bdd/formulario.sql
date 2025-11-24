-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 05, 2025 at 02:27 AM
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
--
-- Table structure for table `seccion`
--

CREATE TABLE `seccion` (
  `id_seccion` int(11) NOT NULL,
  `nombre` varchar(255) DEFAULT NULL,
  `numero` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
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
  `tipo_respuesta` enum('TEXTO', 'NUMERO', 'FECHA', 'OPCION_MULTIPLE') NOT NULL DEFAULT 'OPCION_MULTIPLE',
  `estudio` tinyint(1) NOT NULL,
  `id_seccion` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

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
-- Table structure for table `usuario_seq`
--

CREATE TABLE `usuario_seq` (
  `next_val` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `usuario_sujeto`
--

CREATE TABLE `usuario_sujeto` (
  `id_usuario` int(11) NOT NULL,
  `id_sujeto` varchar(4) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `fecha` timestamp NOT NULL DEFAULT current_timestamp(),
  `accion` varchar(255) DEFAULT NULL,
  `sujeto_estudio_id_sujeto` varchar(255) NOT NULL,
  `sujeto_estudio_tipo` varchar(255) NOT NULL,
  `usuario_id_usuario` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `sujetoestudio`
--
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
-- Table structure for table `antecedentes`
--

CREATE TABLE `antecedentes` (
  `idantecedentes` int(11) NOT NULL,
  `id_sujeto` varchar(4) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `id_dato` int(11) NOT NULL,
  `valor_string` varchar(255) DEFAULT NULL,
  `valor_num` float DEFAULT NULL,
  `id_sujeto_fk` varchar(255) DEFAULT NULL,
  `tipo_fk` varchar(255) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;



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



--
-- Dumping data for table `datosolicitado`
--

INSERT INTO `datosolicitado` (`id_dato`, `nombre`, `nombre_stata`, `leyenda`, `aplicable_a`,`tipo_respuesta`, `estudio`, `id_seccion`) VALUES
(2, 'Sexo', 'sexo', 'Sexo del paciente', 'AMBOS','OPCION_MULTIPLE', 1, 1),
(3, 'Zona', 'zona', 'Zona de residencia', 'AMBOS','OPCION_MULTIPLE', 1, 1),
(4, 'Años viviendo en la residencia actual', 'anos_residencia', 'Tiempo de residencia en el domicilio actual', 'AMBOS', 'NUMERO',1, 1),
(5, 'Antecedentes familiares de cáncer gástrico', 'antecedentes_gastrico', 'Presencia de antecedentes familiares de cáncer gástrico', 'CONTROL', 'OPCION_MULTIPLE', 1, 2),
(6, 'Antecedentes familiares de otro tipo de cancer', 'antecedentes_otro', 'Presencia de antecedentes familiares de otros tipos de cáncer', 'CASO','OPCION_MULTIPLE', 1, 2),
(7, 'Edad', 'edad', 'Edad del sujeto en años', 'AMBOS', 'NUMERO',1, 1),
(8, 'Peso', 'peso', 'Peso del sujeto en kg', 'AMBOS','NUMERO', 1, 1),
(9, 'Altura', 'altura', 'Altura del sujeto en cm', 'AMBOS','NUMERO', 1, 1);

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
  `nombre` varchar(255) NOT NULL,
  `valor` int(1) NOT NULL,
  `id_dato` int(11) NOT NULL,
  `requiere_texto` tinyint(1) NOT NULL DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `opcion`
--

INSERT INTO `opcion` (`id_opcion`, `nombre`, `valor`, `id_dato`, `requiere_texto`) VALUES
(1, 'Hombre', 0, 2,0),
(2, 'Mujer', 1, 2,0),
(3, 'Urbana', 0, 3,0),
(4, 'Rural', 1, 3,0),
(5, 'No', 0, 5,0),
(6, 'Si', 1, 5,1),
(7, 'No', 0, 6,0),
(8, 'Si', 1, 6,1);

-- --------------------------------------------------------


--
-- Dumping data for table `seccion`
--

INSERT INTO `seccion` (`id_seccion`, `nombre`, `numero`) VALUES
(1, 'Preguntas generales', 1),
(2, 'Antecedentes médicos', 2);

-- --------------------------------------------------------


--
-- Dumping data for table `sujetoestudio`
--

INSERT INTO `sujetoestudio` (`id_sujeto`, `tipo`, `nombre`, `direccion`, `ocupacion`, `telefono`, `correo`, `nacionalidad`, `email`) VALUES
('0001', 'CA', 'test caso 1', 'aaaaaaaaaaa', NULL, NULL, NULL, 'aaaaa', NULL),
('0001', 'CO', 'test control 1', 'aaaaaaaaa', NULL, 'aaaaa', NULL, 'aaaaaaa', NULL),
('0002', 'CA', 'test caso 2', 'aaaaaaaaaa', NULL, 'aaaaaaaaa', NULL, NULL, NULL),
('0002', 'CO', 'test control 2', NULL, NULL, NULL, 'aaaaaaaaaaaaa', NULL, NULL);


--
-- Dumping data for table `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `nombre`, `contraseña`, `correo`, `estado`, `rol`) VALUES
(1, 'admin test', '$2y$10$CCSBXN3IxnfySxfcVfWdgO9EYhRKFpBuW9bWwAUykdAcV5WL48gUS', 'correo@ubiobio.cl', 'SUSPENDIDO', 'ADMINISTRADOR'),
(2, 'Recolector test', '$2y$10$35ThpCNDehBpwCh2K9wcx.9g/6qcybBSjJ4vf98CLsB8qIubzTdbq', 'correo@gmail.com', 'INICIADO', 'RECOLECTOR_DE_DATOS');

-- --------------------------------------------------------


--
-- Dumping data for table `usuario_seq`
--

INSERT INTO `usuario_seq` (`next_val`) VALUES
(951);



--
-- Dumping data for table `usuario_sujeto`
--

INSERT INTO `usuario_sujeto` (`id_usuario`, `id_sujeto`, `tipo`, `fecha`, `accion`, `sujeto_estudio_id_sujeto`, `sujeto_estudio_tipo`, `usuario_id_usuario`) VALUES
(1, '0002', 'CO', '2025-11-03 03:00:00', 'accion test', '', '', 0);

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
  MODIFY `idantecedentes` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

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
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=854;

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
  ADD CONSTRAINT `us_sujeto_fk` FOREIGN KEY (`id_sujeto`,`tipo`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`),
  ADD CONSTRAINT `us_usuario_fk` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
