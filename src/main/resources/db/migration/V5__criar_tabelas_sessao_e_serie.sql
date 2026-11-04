CREATE TABLE sessao_treino (
    id            BIGSERIAL PRIMARY KEY,
    dia_treino_id BIGINT NOT NULL REFERENCES dia_treino (id),
    data_treino   DATE   NOT NULL,
    observacoes   VARCHAR(500),
    CONSTRAINT uk_sessao_dia_data UNIQUE (dia_treino_id, data_treino)
);

CREATE INDEX idx_sessao_data ON sessao_treino (data_treino);

CREATE TABLE serie_realizada (
    id           BIGSERIAL PRIMARY KEY,
    sessao_id    BIGINT       NOT NULL REFERENCES sessao_treino (id) ON DELETE CASCADE,
    exercicio_id BIGINT       NOT NULL REFERENCES exercicio (id),
    numero_serie INTEGER      NOT NULL CHECK (numero_serie > 0),
    repeticoes   INTEGER      NOT NULL CHECK (repeticoes > 0),
    carga        NUMERIC(6,2) NOT NULL CHECK (carga >= 0),
    CONSTRAINT uk_serie_sessao_exercicio_numero UNIQUE (sessao_id, exercicio_id, numero_serie)
);

CREATE INDEX idx_serie_sessao ON serie_realizada (sessao_id);
CREATE INDEX idx_serie_exercicio ON serie_realizada (exercicio_id);

ALTER TABLE sessao_treino ENABLE ROW LEVEL SECURITY;
ALTER TABLE serie_realizada ENABLE ROW LEVEL SECURITY;