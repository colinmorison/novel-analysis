-- 小说表
CREATE TABLE IF NOT EXISTS novel (
    id              BIGSERIAL PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    author          VARCHAR(100),
    file_name       VARCHAR(255) NOT NULL,
    minio_path      VARCHAR(500) NOT NULL,
    file_size       BIGINT,
    parse_status    VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    parse_error     TEXT,
    uploaded_at     TIMESTAMPTZ DEFAULT NOW(),
    parsed_at       TIMESTAMPTZ
);

-- 章节表
CREATE TABLE IF NOT EXISTS novel_chapter (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    chapter_no      INT NOT NULL,
    title           VARCHAR(255),
    start_pos       BIGINT,
    end_pos         BIGINT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 知识片段表（pgvector）
CREATE TABLE IF NOT EXISTS knowledge_chunk (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    chapter_id      BIGINT REFERENCES novel_chapter(id),
    chapter_no      INT,
    chapter_title   VARCHAR(255),
    chunk_no        INT NOT NULL,
    content         TEXT NOT NULL,
    embedding       VECTOR(1024),
    token_count     INT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_novel ON knowledge_chunk(novel_id);
CREATE INDEX IF NOT EXISTS idx_knowledge_chunk_embedding ON knowledge_chunk USING ivfflat (embedding vector_cosine_ops);

-- 角色表
CREATE TABLE IF NOT EXISTS novel_character (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    name            VARCHAR(100) NOT NULL,
    aliases         TEXT,
    profile         TEXT,
    appear_chunk_ids TEXT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 事件表
CREATE TABLE IF NOT EXISTS novel_event (
    id              BIGSERIAL PRIMARY KEY,
    novel_id        BIGINT NOT NULL REFERENCES novel(id),
    title           VARCHAR(255) NOT NULL,
    summary         TEXT,
    characters      TEXT,
    chapter_no      INT,
    order_in_chapter INT,
    created_at      TIMESTAMPTZ DEFAULT NOW()
);

-- 会话表
CREATE TABLE IF NOT EXISTS chat_session (
    session_id      VARCHAR(64) PRIMARY KEY,
    novel_id        BIGINT REFERENCES novel(id),
    messages        JSONB,
    updated_at      TIMESTAMPTZ DEFAULT NOW()
);
