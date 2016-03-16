ALTER TABLE `mail` ENGINE = InnoDB;
ALTER TABLE `mail_attachments` ENGINE = InnoDB;
ALTER TABLE `mail_attachments` ADD FOREIGN KEY ( `message_id` ) REFERENCES `mail` ( `message_id` ) ON DELETE CASCADE ;

INSERT INTO character_mail SELECT sender_id, message_id, true FROM mail;
INSERT INTO character_mail SELECT receiver_id, message_id, false FROM mail;
