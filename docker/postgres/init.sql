-- TABLE: processed_logs

CREATE TABLE processed_logs (
                                id BIGSERIAL PRIMARY KEY,
                                timestamp TIMESTAMPTZ NOT NULL,
                                level VARCHAR(255) NOT NULL,
                                message VARCHAR(1000) NOT NULL,
                                source VARCHAR(255),
                                thread VARCHAR(255),
                                logger VARCHAR(255),
                                processed_timestamp TIMESTAMPTZ NOT NULL,
                                category VARCHAR(100),
                                status VARCHAR(50)
);

CREATE TABLE log_tags (
                          log_id BIGINT REFERENCES processed_logs(id) ON DELETE CASCADE,
                          tag_key VARCHAR(255) NOT NULL,
                          tag_value VARCHAR(255),
                          PRIMARY KEY (log_id, tag_key)
);
