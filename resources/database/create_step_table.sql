CREATE TABLE `ai_oaq`.`step` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `id_game_state` INT NOT NULL,
  `chose` INT NOT NULL,
  `direc` INT NOT NULL,
  `win` INT NULL,
  `total_use` INT NULL,
  PRIMARY KEY (`id`))
ENGINE = InnoDB
DEFAULT CHARACTER SET = utf32;
