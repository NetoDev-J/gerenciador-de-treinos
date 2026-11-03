CREATE TABLE dia_treino (
    id             BIGSERIAL PRIMARY KEY,
    divisao_id     BIGINT       NOT NULL REFERENCES divisao (id) ON DELETE CASCADE,
    nome           VARCHAR(80)  NOT NULL,
    grupo_muscular VARCHAR(100) NOT NULL,
    dia_semana     VARCHAR(10)  NOT NULL
        CHECK (dia_semana IN ('SEGUNDA','TERCA','QUARTA','QUINTA','SEXTA','SABADO','DOMINGO')),
    CONSTRAINT uk_dia_treino_divisao_dia UNIQUE (divisao_id, dia_semana)
);

CREATE INDEX idx_dia_treino_divisao ON dia_treino (divisao_id);

ALTER TABLE dia_treino ENABLE ROW LEVEL SECURITY;