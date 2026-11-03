CREATE TABLE exercicio (
    id             BIGSERIAL PRIMARY KEY,
    nome           VARCHAR(100) NOT NULL UNIQUE,
    grupo_muscular VARCHAR(50)  NOT NULL
);

-- Evita exposição pela API automática do Supabase
ALTER TABLE exercicio ENABLE ROW LEVEL SECURITY;