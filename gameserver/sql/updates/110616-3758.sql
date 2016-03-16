--
alter table clan_data drop column auction_bid_at;

ALTER TABLE clanhall ADD COLUMN auction_length INT NOT NULL;
ALTER TABLE clanhall ADD COLUMN auction_desc TEXT NULL;
ALTER TABLE clanhall ADD COLUMN auction_min_bid BIGINT NOT NULL;