-- phpMyAdmin SQL Dump
-- version 5.2.3
-- https://www.phpmyadmin.net/
--
-- Host: mysqldb:3306
-- Generation Time: Dec 12, 2025 at 06:07 PM
-- Server version: 8.0.44
-- PHP Version: 8.3.26

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
  `idantecedentes` int NOT NULL,
  `valor_num` float DEFAULT NULL,
  `valor_string` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_dato` int NOT NULL,
  `id_sujeto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `antecedentes`
--
DELIMITER $$
CREATE TRIGGER `ante_delete-audittrigger` AFTER DELETE ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('DELETE en tabla antecedentes, previo id ', CONVERT(OLD.idantecedentes,char), ' de Usuario ', CONVERT(OLD.tipo, char), CONVERT(OLD.id_sujeto, char), ' en pregunta ', CONVERT(OLD.id_dato, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NOW(), NULL, NULL, NULL, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `ante_insert-audittrigger` AFTER INSERT ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('INSERT en tabla antecedentes, id ', CONVERT(NEW.idantecedentes,char));

    INSERT INTO usuario_sujeto (id_usuario, id_sujeto, tipo, fecha, accion)
    VALUES (NULL, NEW.id_sujeto, NEW.tipo, NOW(), v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `ante_update-audittrigger` AFTER UPDATE ON `antecedentes` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('UPDATE en tabla antecedentes, id ', CONVERT(NEW.idantecedentes,char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NOW(), NULL, NULL, NULL, v_action);
END
$$
DELIMITER ;

-- --------------------------------------------------------

--
-- Table structure for table `criterio`
--

CREATE TABLE `criterio` (
  `id_criterio` int NOT NULL,
  `expresion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `leyenda` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `nombre_stata` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo_calculo` enum('GENERAL','PARTICULAR') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `datosolicitado`
--

CREATE TABLE `datosolicitado` (
  `id_dato` int NOT NULL,
  `aplicable_a` enum('AMBOS','CASO','CONTROL') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `estudio` bit(1) DEFAULT NULL,
  `leyenda` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre_stata` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_seccion` int NOT NULL,
  `tipo_respuesta` enum('FECHA','NUMERO','OPCION_MULTIPLE','TEXTO') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `valor_max` int DEFAULT NULL,
  `valor_min` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `dato_criterio`
--

CREATE TABLE `dato_criterio` (
  `id_dato` int NOT NULL,
  `id_criterio` int NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `opcion`
--

CREATE TABLE `opcion` (
  `id_opcion` int NOT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `valor` int NOT NULL,
  `id_dato` int NOT NULL,
  `requiere_texto` bit(1) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `seccion`
--

CREATE TABLE `seccion` (
  `id_seccion` int NOT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `numero` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

-- --------------------------------------------------------

--
-- Table structure for table `sujetoestudio`
--

CREATE TABLE `sujetoestudio` (
  `id_sujeto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `tipo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci NOT NULL,
  `direccion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `email` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nacionalidad` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `ocupacion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `telefono` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Triggers `sujetoestudio`
--
DELIMITER $$
CREATE TRIGGER `suje_delete-audittrigger` AFTER DELETE ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('DELETE en tabla sujetoestudio, previo id ', CONVERT(OLD.tipo, char), CONVERT(OLD.id_sujeto, char), ', nombre ', CONVERT(OLD.nombre, char), ' email ', CONVERT(OLD.email, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NOW(), NULL, NULL, NULL, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `suje_insert-audittrigger` AFTER INSERT ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('INSERT en tabla sujetoestudio,  id ', CONVERT(NEW.tipo, char), CONVERT(NEW.id_sujeto, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NOW(), NULL, NEW.id_sujeto, NEW.tipo, v_action);
END
$$
DELIMITER ;
DELIMITER $$
CREATE TRIGGER `suje_update-audittrigger` AFTER UPDATE ON `sujetoestudio` FOR EACH ROW BEGIN
	DECLARE v_action VARCHAR(255);
    SET v_action = CONCAT('UPDATE en tabla sujetoestudio, id ', CONVERT(NEW.tipo, char), CONVERT(NEW.id_sujeto, char));

    INSERT INTO usuario_sujeto (fecha, id_usuario, id_sujeto, tipo, accion)
    VALUES (NOW(), NULL, NEW.id_sujeto, NEW.tipo, v_action);
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
  `id_usuario` int NOT NULL,
  `contraseña` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `correo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `estado` enum('ACTIVO','DADO_DE_BAJA','INICIADO','SUSPENDIDO') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `nombre` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `rol` enum('ADMINISTRADOR','ANALISTA','RECOLECTOR_DE_DATOS') CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `token_expiracion` datetime(6) DEFAULT NULL,
  `token_registro` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `usuario`
--

INSERT INTO `usuario` (`id_usuario`, `contraseña`, `correo`, `estado`, `nombre`, `rol`, `token_expiracion`, `token_registro`) VALUES
(1, '$2a$04$Cdch6bR8C4P4sn7Mmt.p7ORu6a.82F5qVCrQJaHCtWK0QXp22ZODW', 'admin@correo.cl', 'ACTIVO', 'admin', 'ADMINISTRADOR', NULL, NULL);

-- --------------------------------------------------------

--
-- Table structure for table `usuario_sujeto`
--

CREATE TABLE `usuario_sujeto` (
  `id_cambio` int NOT NULL,
  `accion` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `fecha` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `id_sujeto` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `tipo` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci DEFAULT NULL,
  `id_usuario` int DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Indexes for dumped tables
--

--
-- Indexes for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD PRIMARY KEY (`idantecedentes`),
  ADD KEY `FKbiemogk36tpxfnehp8dhmwp1` (`id_dato`),
  ADD KEY `FKsa6sgllp05pqw7x80wtm3sgr9` (`id_sujeto`,`tipo`);

--
-- Indexes for table `criterio`
--
ALTER TABLE `criterio`
  ADD PRIMARY KEY (`id_criterio`),
  ADD UNIQUE KEY `UKbtfwrta8jct9vgf75eq0otxm1` (`nombre`);

--
-- Indexes for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  ADD PRIMARY KEY (`id_dato`),
  ADD KEY `FKm957a7bn79qcaorbud4h2pr3i` (`id_seccion`);

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
  ADD KEY `FK3p3sb7crsmoxytkkbiufy1ar1` (`id_dato`);

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
  ADD UNIQUE KEY `UK1ssmimuvpqhi2cff1diy3t17t` (`nombre`);

--
-- Indexes for table `usuario`
--
ALTER TABLE `usuario`
  ADD PRIMARY KEY (`id_usuario`);

--
-- Indexes for table `usuario_sujeto`
--
ALTER TABLE `usuario_sujeto`
  ADD PRIMARY KEY (`id_cambio`),
  ADD KEY `FK2goorrcpylgu9iswsosjla0v2` (`id_usuario`),
  ADD KEY `FKh7wrd122riicrk8b1axewvgoj` (`id_sujeto`,`tipo`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `antecedentes`
--
ALTER TABLE `antecedentes`
  MODIFY `idantecedentes` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `criterio`
--
ALTER TABLE `criterio`
  MODIFY `id_criterio` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  MODIFY `id_dato` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `opcion`
--
ALTER TABLE `opcion`
  MODIFY `id_opcion` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `seccion`
--
ALTER TABLE `seccion`
  MODIFY `id_seccion` int NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `usuario`
--
ALTER TABLE `usuario`
  MODIFY `id_usuario` int NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `usuario_sujeto`
--
ALTER TABLE `usuario_sujeto`
  MODIFY `id_cambio` int NOT NULL AUTO_INCREMENT;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `antecedentes`
--
ALTER TABLE `antecedentes`
  ADD CONSTRAINT `FKbiemogk36tpxfnehp8dhmwp1` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`),
  ADD CONSTRAINT `FKsa6sgllp05pqw7x80wtm3sgr9` FOREIGN KEY (`id_sujeto`,`tipo`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`);

--
-- Constraints for table `datosolicitado`
--
ALTER TABLE `datosolicitado`
  ADD CONSTRAINT `FKm957a7bn79qcaorbud4h2pr3i` FOREIGN KEY (`id_seccion`) REFERENCES `seccion` (`id_seccion`);

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
  ADD CONSTRAINT `FK3p3sb7crsmoxytkkbiufy1ar1` FOREIGN KEY (`id_dato`) REFERENCES `datosolicitado` (`id_dato`);

--
-- Constraints for table `usuario_sujeto`
--
ALTER TABLE `usuario_sujeto`
  ADD CONSTRAINT `FK2goorrcpylgu9iswsosjla0v2` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE CASCADE,
  ADD CONSTRAINT `FKh7wrd122riicrk8b1axewvgoj` FOREIGN KEY (`id_sujeto`,`tipo`) REFERENCES `sujetoestudio` (`id_sujeto`, `tipo`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
