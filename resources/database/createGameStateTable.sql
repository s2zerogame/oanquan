CREATE TABLE `ai_oaq`.`gamestate` (
  `id` INT NOT NULL,
  `house_1` INT NOT NULL,
  `house_2` INT NOT NULL,
  `house_3` INT NOT NULL,
  `house_4` INT NOT NULL,
  `house_5` INT NOT NULL,
  `house_7` INT NOT NULL,
  `house_8` INT NOT NULL,
  `house_9` INT NOT NULL,
  `house_10` INT NOT NULL,
  `house_11` INT NOT NULL,
  `q0_danSo` INT NOT NULL,
  `q0_coQuan` INT NOT NULL,
  `q6_danSo` INT NOT NULL,
  `q6_coQuan` INT NOT NULL,
  PRIMARY KEY (`id`));
  ALTER TABLE `ai_oaq`.`gamestate` 
CHANGE COLUMN `id` `id` INT(11) NOT NULL AUTO_INCREMENT ;
