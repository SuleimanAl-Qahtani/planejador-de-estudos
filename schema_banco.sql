-- ============================================================================
-- SCRIPT DE CRIAÇÃO DO BANCO DE DADOS - PLANEJADOR DE ESTUDOS (A3 UNIFACS)
-- SGBD: SQLite (arquivo planejador_estudos.db)
-- Obs.: o programa já roda este mesmo script sozinho em ConexaoSQLite.inicializarBanco()
-- ============================================================================

PRAGMA foreign_keys = ON;

CREATE TABLE IF NOT EXISTS disciplinas (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    nome VARCHAR(100) NOT NULL,
    codigo VARCHAR(30) NOT NULL UNIQUE,
    professor VARCHAR(100),
    meta_horas_semanais REAL NOT NULL CHECK (meta_horas_semanais > 0),
    cor VARCHAR(20) NOT NULL CHECK (cor IN ('AZUL','VERDE','AMARELO','VERMELHO','ROXO','CIANO'))
);

CREATE TABLE IF NOT EXISTS atividades (
    id INTEGER PRIMARY KEY AUTOINCREMENT,
    disciplina_id INTEGER NOT NULL,
    tipo_atividade VARCHAR(20) NOT NULL CHECK (tipo_atividade IN ('TAREFA','AVALIACAO')),
    titulo VARCHAR(120) NOT NULL,
    descricao TEXT,
    data_atividade VARCHAR(10) NOT NULL,   -- AAAA-MM-DD
    hora_inicio VARCHAR(5) NOT NULL,       -- HH:MM
    hora_fim VARCHAR(5) NOT NULL,          -- HH:MM
    prioridade VARCHAR(10) NOT NULL CHECK (prioridade IN ('BAIXA','MEDIA','ALTA')),
    status VARCHAR(15) NOT NULL CHECK (status IN ('PENDENTE','CONCLUIDA')),
    -- Só para TAREFA
    topico_estudo VARCHAR(150),
    revisao_necessaria INTEGER DEFAULT 0 CHECK (revisao_necessaria IN (0,1)),
    -- Só para AVALIACAO
    tipo_avaliacao VARCHAR(50),
    peso_nota_maxima REAL,
    nota_obtida REAL DEFAULT 0.0,
    FOREIGN KEY (disciplina_id) REFERENCES disciplinas(id) ON DELETE CASCADE
);
