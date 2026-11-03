CREATE TABLE item_dia_treino (
    id                BIGSERIAL PRIMARY KEY,
    dia_treino_id     BIGINT  NOT NULL REFERENCES dia_treino (id) ON DELETE CASCADE,
    exercicio_id      BIGINT  NOT NULL REFERENCES exercicio (id),
    series            INTEGER NOT NULL CHECK (series > 0),
    repeticoes        INTEGER NOT NULL CHECK (repeticoes > 0),
    descanso_segundos INTEGER NOT NULL CHECK (descanso_segundos BETWEEN 10 AND 600),
    ordem             INTEGER NOT NULL CHECK (ordem > 0),
    CONSTRAINT uk_item_dia_exercicio UNIQUE (dia_treino_id, exercicio_id)
);

CREATE INDEX idx_item_dia_treino_dia ON item_dia_treino (dia_treino_id);
CREATE INDEX idx_item_dia_treino_exercicio ON item_dia_treino (exercicio_id);

ALTER TABLE item_dia_treino ENABLE ROW LEVEL SECURITY;