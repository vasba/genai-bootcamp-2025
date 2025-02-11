CREATE TABLE IF NOT EXISTS words (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    romanian TEXT NOT NULL,
    english TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_words_romanian ON words(romanian);
CREATE INDEX IF NOT EXISTS idx_words_english ON words(english);

CREATE TABLE IF NOT EXISTS groups (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    words_count INTEGER DEFAULT 0
);

CREATE INDEX IF NOT EXISTS idx_groups_name ON groups(name);

CREATE TABLE IF NOT EXISTS word_groups (
    word_id INTEGER,
    group_id INTEGER,
    FOREIGN KEY (word_id) REFERENCES words(id),
    FOREIGN KEY (group_id) REFERENCES groups(id),
    PRIMARY KEY (word_id, group_id)
);

CREATE INDEX IF NOT EXISTS idx_word_groups_word_id ON word_groups(word_id);
CREATE INDEX IF NOT EXISTS idx_word_groups_group_id ON word_groups(group_id);

CREATE TABLE IF NOT EXISTS study_activities (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    name TEXT NOT NULL,
    url TEXT NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_study_activities_name ON study_activities(name);

CREATE TABLE IF NOT EXISTS study_sessions (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    group_id INTEGER,
    study_activity_id INTEGER,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (group_id) REFERENCES groups(id),
    FOREIGN KEY (study_activity_id) REFERENCES study_activities(id)
);

CREATE INDEX IF NOT EXISTS idx_study_sessions_group_id ON study_sessions(group_id);
CREATE INDEX IF NOT EXISTS idx_study_sessions_activity_id ON study_sessions(study_activity_id);
CREATE INDEX IF NOT EXISTS idx_study_sessions_created_at ON study_sessions(created_at DESC);

CREATE TABLE IF NOT EXISTS word_review_items (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    word_id INTEGER,
    study_session_id INTEGER,
    correct BOOLEAN NOT NULL,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (word_id) REFERENCES words(id),
    FOREIGN KEY (study_session_id) REFERENCES study_sessions(id)
);

CREATE INDEX IF NOT EXISTS idx_word_review_items_session ON word_review_items(study_session_id, created_at DESC);
CREATE INDEX IF NOT EXISTS idx_word_review_items_word ON word_review_items(word_id);
CREATE INDEX IF NOT EXISTS idx_word_review_items_correct ON word_review_items(correct);