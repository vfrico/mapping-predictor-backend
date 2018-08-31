USE `mappings_annotations`;

INSERT INTO `users_role` (`role`, `code`) VALUES
  ('ADMIN', 100),
  ('ANNOTATOR', 2),
  ('MAPPER', 1),
  ('NO_ROLE', 0);

INSERT INTO `vote_type` VALUES ('CORRECT_BUT_SPECIFY'),('CORRECT_MAPPING'),('UNKNOWN'),('WRONG_MAPPING'),('WRONG_ONTOLOGY');

INSERT INTO `users` (`idusers`, `username`, `email`, `password_md5`, `creation_date`, `jwt`, `role`) VALUES
  (1, 'default', 'default@example.com', 'default', NOW(), '', 'ADMIN'),
  (2, 'sampleNo', 'default@example.com', 'sample', NOW(), '', 'NO_ROLE'),
  (3, 'sampleMapper', 'default@example.com', 'sample', NOW(), '', 'MAPPER'),
  (4, 'sampleAnnotator', 'default@example.com', 'sample', NOW(), '', 'ANNOTATOR'),
  (5, 'sampleAdmin', 'default@example.com', 'sample', NOW(), '', 'ADMIN');

