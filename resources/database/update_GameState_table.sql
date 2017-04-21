ALTER TABLE `ai_oaq`.`gamestate` 
ADD COLUMN `p1_soDan` INT NOT NULL AFTER `q6_coQuan`,
ADD COLUMN `p1_soQuan` INT NOT NULL AFTER `p1_soDan`,
ADD COLUMN `p2_soDan` INT NOT NULL AFTER `p1_soQuan`,
ADD COLUMN `p2_soQuan` INT NOT NULL AFTER `p2_soDan`;