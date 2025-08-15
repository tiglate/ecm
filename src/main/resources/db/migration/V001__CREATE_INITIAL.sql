/*
DROP TABLE tb_credential;
DROP TABLE tb_credential_type;
DROP TABLE tb_application;
DROP TABLE tb_environment;
DROP TABLE tb_cipher_envelope;
*/

-- -----------------------------------------------------
-- Table `tb_cipher_envelope`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tb_cipher_envelope` (
  `id_cipher_envelope` BIGINT NOT NULL AUTO_INCREMENT,
  `version` CHAR(8) NOT NULL,
  `kdf` VARCHAR(16) NOT NULL,
  `iterations` INT NULL,
  `salt` VARBINARY(64) NULL,
  `iv` VARBINARY(32) NOT NULL,
  `ciphertext` LONGBLOB NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id_cipher_envelope`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tb_environment`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tb_environment` (
  `id_environment` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_environment`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `uq_environment_name` ON `tb_environment` (`name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `tb_application`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tb_application` (
  `id_application` BIGINT NOT NULL AUTO_INCREMENT,
  `code` VARCHAR(45) NULL,
  `name` VARCHAR(255) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  `updated_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id_application`))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `tb_credential_type`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tb_credential_type` (
  `id_credential_type` BIGINT NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(45) NOT NULL,
  PRIMARY KEY (`id_credential_type`))
ENGINE = InnoDB;

CREATE UNIQUE INDEX `uq_credential_type_name` ON `tb_credential_type` (`name` ASC) VISIBLE;


-- -----------------------------------------------------
-- Table `tb_credential`
-- -----------------------------------------------------
CREATE TABLE IF NOT EXISTS `tb_credential` (
  `id_credential` BIGINT NOT NULL AUTO_INCREMENT,
  `id_credential_next` BIGINT NULL,
  `id_cipher_envelope` BIGINT NOT NULL,
  `id_environment` BIGINT NOT NULL,
  `id_application` BIGINT NOT NULL,
  `id_credential_type` BIGINT NOT NULL,
  `username` VARCHAR(255) NOT NULL,
  `version` INT NOT NULL DEFAULT 1,
  `enabled` BIT NOT NULL DEFAULT 1,
  `url` VARCHAR(500) NULL,
  `notes` TEXT NULL,
  `created_by` VARCHAR(45) NOT NULL,
  `created_at` TIMESTAMP(6) NOT NULL DEFAULT CURRENT_TIMESTAMP(6),
  PRIMARY KEY (`id_credential`),
  CONSTRAINT `fk_credential_cipher_envelope`
    FOREIGN KEY (`id_cipher_envelope`)
    REFERENCES `tb_cipher_envelope` (`id_cipher_envelope`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_credential_environment`
    FOREIGN KEY (`id_environment`)
    REFERENCES `tb_environment` (`id_environment`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_credential_application`
    FOREIGN KEY (`id_application`)
    REFERENCES `tb_application` (`id_application`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_credential_credential_type`
    FOREIGN KEY (`id_credential_type`)
    REFERENCES `tb_credential_type` (`id_credential_type`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `fk_credential_self`
    FOREIGN KEY (`id_credential_next`)
    REFERENCES `tb_credential` (`id_credential`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

CREATE INDEX `fk_credential_cipher_envelope_idx` ON `tb_credential` (`id_cipher_envelope` ASC) VISIBLE;

CREATE INDEX `fk_credential_environment_idx` ON `tb_credential` (`id_environment` ASC) VISIBLE;

CREATE INDEX `fk_credential_application_idx` ON `tb_credential` (`id_application` ASC) VISIBLE;

CREATE UNIQUE INDEX `uq_credential` ON `tb_credential` (`id_environment` ASC, `id_application` ASC, `id_credential_type` ASC, `username` ASC, `version` ASC, `enabled` ASC) VISIBLE;

CREATE INDEX `fk_credential_credential_type_idx` ON `tb_credential` (`id_credential_type` ASC) VISIBLE;

CREATE INDEX `fk_credential_self` ON `tb_credential` (`id_credential_next` ASC) VISIBLE;