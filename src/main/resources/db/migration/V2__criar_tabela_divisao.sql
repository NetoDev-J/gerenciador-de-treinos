CREATE TABLE divisao (
    id    BIGSERIAL PRIMARY KEY,
    nome  VARCHAR(80) NOT NULL UNIQUE,
    ativa BOOLEAN     NOT NULL DEFAULT FALSE
);

-- Garante no próprio banco que existe no máximo uma divisão ativa
CREATE UNIQUE INDEX uk_divisao_unica_ativa ON divisao (ativa) WHERE ativa;

ALTER TABLE divisao ENABLE ROW LEVEL SECURITY;