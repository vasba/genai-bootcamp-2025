INSERT INTO words (romanian, english) VALUES
('carte', 'book'),
('casa', 'house'),
('masă', 'table'),
('scaun', 'chair'),
('fereastră', 'window');

INSERT INTO groups (name, description, words_count) VALUES
('Basic Nouns', 'Common everyday nouns', 5),
('Household Items', 'Items commonly found in a house', 4);

INSERT INTO study_activities (name, url) VALUES
('Flashcards', '/study/flashcards'),
('Multiple Choice', '/study/multiple-choice'),
('Word Match', '/study/word-match');

INSERT or IGNORE INTO word_groups (word_id, group_id) VALUES
(1, 1), (2, 1), (3, 1), (4, 1), (5, 1),
(2, 2), (3, 2), (4, 2), (5, 2);