-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Nov 03, 2025 at 09:57 PM
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
  `id_sujeto` int(11) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `id_dato` int(11) NOT NULL,
  `valor_string` text NOT NULL,
  `valor_num` float NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `criterio`
--

CREATE TABLE `criterio` (
  `id_criterio` int(11) NOT NULL,
  `nombre` text NOT NULL,
  `nombre_stata` text DEFAULT NULL,
  `tipo_calculo` enum('GENERAL','PARTICULAR') NOT NULL,
  `leyenda` text DEFAULT NULL,
  `expresion` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `criterio_datosolicitado`
--

CREATE TABLE `criterio_datosolicitado` (
  `id_criterio` int(11) NOT NULL,
  `id_dato` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `datosolicitado`
--

CREATE TABLE `datosolicitado` (
  `id_dato` int(11) NOT NULL,
  `nombre` text NOT NULL,
  `nombre_stata` text DEFAULT NULL,
  `leyenda` text DEFAULT NULL,
  `aplicable_a` enum('CASO','CONTROL','AMBOS') NOT NULL DEFAULT 'AMBOS',
  `estudio` tinyint(1) NOT NULL,
  `id_seccion` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `opcion`
--

CREATE TABLE `opcion` (
  `id_opcion` int(11) NOT NULL,
  `nombre` text NOT NULL,
  `valor` int(1) NOT NULL,
  `id_dato` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `seccion`
--

CREATE TABLE `seccion` (
  `id_seccion` int(11) NOT NULL,
  `nombre` text NOT NULL,
  `numero` int(11) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sujetoestudio`
--

CREATE TABLE `sujetoestudio` (
  `id_sujeto` int(11) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `nombre` text NOT NULL,
  `direccion` text DEFAULT NULL,
  `ocupacion` text DEFAULT NULL,
  `telefono` text DEFAULT NULL,
  `correo` text DEFAULT NULL,
  `nacionalidad` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `sujetoestudio`
--

INSERT INTO `sujetoestudio` (`id_sujeto`, `tipo`, `nombre`, `direccion`, `ocupacion`, `telefono`, `correo`, `nacionalidad`) VALUES
(1, 'CA', 'test caso 1', 'casa A', 'aaa', '142253', 'correo', 'Chile'),
(1, 'CO', 'test control 1', 'casa B', 'fiajfje', '643345', 'correo', 'Chile');

--
-- Triggers `sujetoestudio`
--
DELIMITER $$
CREATE TRIGGER `sujeto_crearid` BEFORE INSERT ON `sujetoestudio` FOR EACH ROW BEGIN
    SET NEW.id_sujeto = (
       SELECT IFNULL(MAX(id_sujeto), 0) + 1
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
  `nombre` text NOT NULL,
  `contraseña` text NOT NULL,
  `correo` text NOT NULL,
  `estado` enum('INICIADO','ACTIVO','SUSPENDIDO','DADO DE BAJA') NOT NULL,
  `rol` enum('ADMINISTRADOR','RECOLECTOR DE DATOS','ANALISTA') NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `usuario_sujeto`
--

CREATE TABLE `usuario_sujeto` (
  `id_usuario` int(11) NOT NULL,
  `id_sujeto` int(11) NOT NULL,
  `tipo` varchar(2) NOT NULL,
  `fecha` date NOT NULL DEFAULT current_timestamp(),
  `accion` text NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD KEY `antec_sujeto_fk` (`id_sujeto`,`tipo`),
  ADD KEY `antec_dato` (`id_dato`);

--
-- Indexes for table `criterio`
--
ALTER TABLE `criterio`
  ADD PRIMARY KEY (`id_criterio`),
  ADD UNIQUE KEY `nombre` (`nombre`) USING HASH;

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
  ADD UNIQUE KEY `nombre` (`nombre`) USING HASH,
  ADD KEY `datosolicitado_fk` (`id_seccion`);

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
  ADD UNIQUE KEY `nombre` (`nombre`) USING HASH;

--
-- Indexes for table `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`);

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
-- AUTO_INCREMENT for table `criterio`
--
ALTER TABLE `criterio`
  MODIFY `id_criterio` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  MODIFY `id_dato` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `opcion`
--
ALTER TABLE `opcion`
  MODIFY `id_opcion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `seccion`
--
ALTER TABLE `seccion`
  MODIFY `id_seccion` int(11) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int(11) NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD CONSTRAINT `antec_dato` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`),
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
