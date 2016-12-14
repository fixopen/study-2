--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.4
-- Dumped by pg_dump version 9.6.1

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SET check_function_bodies = false;
SET client_min_messages = warning;
SET row_security = off;

--
-- Name: plpgsql; Type: EXTENSION; Schema: -; Owner: 
--

CREATE EXTENSION IF NOT EXISTS plpgsql WITH SCHEMA pg_catalog;


--
-- Name: EXTENSION plpgsql; Type: COMMENT; Schema: -; Owner: 
--

COMMENT ON EXTENSION plpgsql IS 'PL/pgSQL procedural language';


SET search_path = public, pg_catalog;

SET default_tablespace = '';

SET default_with_oids = false;

--
-- Name: additionals; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE additionals (
    id bigint NOT NULL,
    table_name name NOT NULL,
    object_id bigint NOT NULL,
    name name NOT NULL,
    value text NOT NULL
);


ALTER TABLE additionals OWNER TO postgres;

--
-- Name: answer_records; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE answer_records (
    id bigint NOT NULL,
    user_id bigint,
    commit_time timestamp without time zone,
    problem_id bigint,
    answer bigint
);


ALTER TABLE answer_records OWNER TO postgres;


--
-- Name: audio_records; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE audio_records (
    id bigint NOT NULL,
    page_no name DEFAULT ''::name NOT NULL,
    unit_no character(2) DEFAULT ''::bpchar NOT NULL,
    start_time integer,
    end_time integer,
    "left" integer,
    top integer,
    "right" integer,
    bottom integer,
    chinese text,
    book_id bigint
);


ALTER TABLE audio_records OWNER TO postgres;

--
-- Name: books; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE books (
    id bigint NOT NULL,
    subject_no character(2) DEFAULT '04'::bpchar NOT NULL,
    grade_no character(2) DEFAULT '20'::bpchar NOT NULL,
    book_no character(2) DEFAULT ''::bpchar NOT NULL,
    name name DEFAULT ''::name
);


ALTER TABLE books OWNER TO postgres;

--
-- Name: cards; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE cards (
    id bigint NOT NULL,
    user_id bigint,
    no name DEFAULT ''::name NOT NULL,
    password name DEFAULT ''::name NOT NULL,
    active_time timestamp without time zone,
    end_time timestamp without time zone,
    duration interval,
    subject bigint,
    amount integer
);


ALTER TABLE cards OWNER TO postgres;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE comments (
    id bigint NOT NULL,
    client_id name,
    user_id bigint DEFAULT 0 NOT NULL,
    object_type name DEFAULT ''::name NOT NULL,
    object_id bigint DEFAULT 0 NOT NULL,
    content text DEFAULT ''::text NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE comments OWNER TO postgres;

--
-- Name: consumptions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE consumptions (
    id bigint NOT NULL,
    user_id bigint,
    "timestamp" timestamp with time zone,
    object_id bigint,
    object_type text,
    transaction_id bigint
);


ALTER TABLE consumptions OWNER TO postgres;

--
-- Name: devices; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE devices (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    platform name DEFAULT 'iOS'::name NOT NULL,
    platform_identity name DEFAULT ''::name NOT NULL,
    platform_notification_token name DEFAULT ''::name NOT NULL
);


ALTER TABLE devices OWNER TO postgres;

--
-- Name: image_texts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE image_texts (
    id bigint NOT NULL,
    mime_type name,
    size bigint,
    name name,
    ext name,
    store_path character varying(256),
    content character varying(256),
    main_color integer
);


ALTER TABLE image_texts OWNER TO postgres;

--
-- Name: medias; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE medias (
    id bigint NOT NULL,
    mime_type name DEFAULT ''::name NOT NULL,
    size bigint DEFAULT '-1'::integer NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    ext name DEFAULT ''::name NOT NULL,
    store_path character varying(256) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE medias OWNER TO postgres;

--
-- Name: images; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE images (
    main_color integer
)
INHERITS (medias);


ALTER TABLE images OWNER TO postgres;

--
-- Name: knowledge_point_content_maps; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE knowledge_point_content_maps (
    id bigint NOT NULL,
    knowledge_point_id bigint,
    volume_id bigint,
    type character varying(256),
    object_id bigint,
    "order" bigint,
    subject_id bigint,
    grade bigint
);


ALTER TABLE knowledge_point_content_maps OWNER TO postgres;

--
-- Name: knowledge_points; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE knowledge_points (
    id bigint NOT NULL,
    subject_id bigint,
    volume_id bigint,
    grade integer,
    title name,
    "order" integer,
    store_path character varying(256),
    video_url character varying(256),
    show_time timestamp without time zone
);


ALTER TABLE knowledge_points OWNER TO postgres;

--
-- Name: logs; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE logs (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    object_type name DEFAULT ''::name NOT NULL,
    object_id bigint DEFAULT 0 NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    action text
);


ALTER TABLE logs OWNER TO postgres;

--
-- Name: pinyin_texts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE pinyin_texts (
    id bigint NOT NULL,
    pinyin text,
    content text
);


ALTER TABLE pinyin_texts OWNER TO postgres;

--
-- Name: problem_options; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE problem_options (
    id bigint NOT NULL,
    name character varying(256),
    problem_id bigint,
    image_id bigint,
    "order" bigint
);


ALTER TABLE problem_options OWNER TO postgres;

--
-- Name: problem_standard_answers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE problem_standard_answers (
    id bigint NOT NULL,
    problem_id bigint,
    name bigint
);


ALTER TABLE problem_standard_answers OWNER TO postgres;

--
-- Name: problems; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE problems (
    id bigint NOT NULL,
    subject_id bigint,
    volume_id bigint,
    knowledge_point_id bigint,
    title text,
    store_path character varying(256),
    video_url character varying(256),
    video_image bigint,
    index bigint
);


ALTER TABLE problems OWNER TO postgres;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE properties (
    id bigint NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    value text DEFAULT ''::text NOT NULL
);


ALTER TABLE properties OWNER TO postgres;

--
-- Name: quotes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE quotes (
    source character varying(256),
    id bigint NOT NULL,
    content character varying(256)
);


ALTER TABLE quotes OWNER TO postgres;

--
-- Name: schedulers; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE schedulers (
    id bigint NOT NULL,
    year integer NOT NULL,
    week integer NOT NULL,
    state integer DEFAULT 0 NOT NULL,
    subject_id bigint,
    grade integer,
    title name,
    description text,
    teacher name,
    teacher_description text,
    duration bigint,
    day integer,
    cover text,
    cdn_link text,
    direct_link text,
    start_time timestamp without time zone,
    end_time timestamp without time zone,
    prepare text,
    outline text,
    generalization text
);


ALTER TABLE schedulers OWNER TO postgres;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE sessions (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    device_id bigint DEFAULT 0,
    identity name DEFAULT ''::name NOT NULL,
    last_operation_time timestamp without time zone DEFAULT now(),
    ip name DEFAULT '0.0.0.0'::name
);


ALTER TABLE sessions OWNER TO postgres;

--
-- Name: subjects; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE subjects (
    id bigint NOT NULL,
    name name
);


ALTER TABLE subjects OWNER TO postgres;

--
-- Name: tags; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE tags (
    id bigint NOT NULL,
    name name DEFAULT ''::name NOT NULL
);


ALTER TABLE tags OWNER TO postgres;

--
-- Name: texts; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE texts (
    id bigint NOT NULL,
    content text
);


ALTER TABLE texts OWNER TO postgres;

--
-- Name: transactions; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE transactions (
    id bigint NOT NULL,
    user_id bigint,
    "timestamp" timestamp with time zone,
    source_id bigint,
    source_type text,
    money bigint,
    object_id bigint,
    object_type text,
    count bigint
);


ALTER TABLE transactions OWNER TO postgres;

--
-- Name: users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE users (
    id bigint NOT NULL,
    login_name name DEFAULT ''::name,
    password name DEFAULT ''::name,
    is_administrator boolean DEFAULT false,
    name name DEFAULT ''::name NOT NULL,
    head character varying(256) DEFAULT ''::character varying,
    email character varying(256),
    telephone character varying(256),
    site character varying(256) DEFAULT ''::character varying,
    location character varying(256),
    description text,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone DEFAULT now() NOT NULL,
    sex bigint NOT NULL,
    amount real NOT NULL,
    school name,
    grade name,
    class name,
    timezone character varying,
    birthday timestamp without time zone
);


ALTER TABLE users OWNER TO postgres;

--
-- Name: validation_codes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE validation_codes (
    id bigint NOT NULL,
    phone_number character varying(16),
    valid_code character varying(10),
    "timestamp" timestamp with time zone
);


ALTER TABLE validation_codes OWNER TO postgres;

--
-- Name: videos; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE videos (
    duration interval DEFAULT '00:00:00'::interval,
    bit_rate real DEFAULT 0.0,
    cover bigint
)
INHERITS (medias);


ALTER TABLE videos OWNER TO postgres;

--
-- Name: volumes; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE volumes (
    id bigint NOT NULL,
    subject_id bigint,
    grade integer,
    title character varying(64),
    "order" bigint,
    bookcover text,
    knowledge_point_count bigint
);


ALTER TABLE volumes OWNER TO postgres;

--
-- Name: wechat_users; Type: TABLE; Schema: public; Owner: postgres
--

CREATE TABLE wechat_users (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    token character varying(8192) DEFAULT ''::character varying,
    refresh_token character varying(8192),
    expiry timestamp without time zone,
    ref_id name DEFAULT ''::name,
    open_id name,
    union_id character varying(256),
    nickname name,
    sex bigint,
    city name,
    province name,
    country name,
    head character varying(256),
    privilege name[],
    subscribe integer,
    language character varying,
    subscribe_time integer,
    remark character varying,
    group_id integer,
    info character varying
);


ALTER TABLE wechat_users OWNER TO postgres;

--
-- Name: images mime_type; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images ALTER COLUMN mime_type SET DEFAULT ''::name;


--
-- Name: images size; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images ALTER COLUMN size SET DEFAULT '-1'::integer;


--
-- Name: images name; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images ALTER COLUMN name SET DEFAULT ''::name;


--
-- Name: images ext; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images ALTER COLUMN ext SET DEFAULT ''::name;


--
-- Name: images store_path; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images ALTER COLUMN store_path SET DEFAULT ''::character varying;


--
-- Name: videos mime_type; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos ALTER COLUMN mime_type SET DEFAULT ''::name;


--
-- Name: videos size; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos ALTER COLUMN size SET DEFAULT '-1'::integer;


--
-- Name: videos name; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos ALTER COLUMN name SET DEFAULT ''::name;


--
-- Name: videos ext; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos ALTER COLUMN ext SET DEFAULT ''::name;


--
-- Name: videos store_path; Type: DEFAULT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos ALTER COLUMN store_path SET DEFAULT ''::character varying;


--
-- Name: answer_records Answer_records_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY answer_records
    ADD CONSTRAINT "Answer_records_pkey" PRIMARY KEY (id);


--
-- Name: additionals additionals_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY additionals
    ADD CONSTRAINT additionals_pkey PRIMARY KEY (id);


--
-- Name: books book_name__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY books
    ADD CONSTRAINT book_name__pk PRIMARY KEY (id);


--
-- Name: cards cards_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY cards
    ADD CONSTRAINT cards_pkey PRIMARY KEY (id);


--
-- Name: comments comment__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comment__pk PRIMARY KEY (id);


--
-- Name: consumptions consumptions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY consumptions
    ADD CONSTRAINT consumptions_pkey PRIMARY KEY (id);


--
-- Name: texts contents_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY texts
    ADD CONSTRAINT contents_pkey PRIMARY KEY (id);


--
-- Name: devices device__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY devices
    ADD CONSTRAINT device__pk PRIMARY KEY (id);


--
-- Name: audio_records english__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY audio_records
    ADD CONSTRAINT english__pk PRIMARY KEY (id);


--
-- Name: images image__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY images
    ADD CONSTRAINT image__pk PRIMARY KEY (id);


--
-- Name: image_texts image_texts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY image_texts
    ADD CONSTRAINT image_texts_pkey PRIMARY KEY (id);


--
-- Name: knowledge_point_content_maps knowledge_point_content_maps_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY knowledge_point_content_maps
    ADD CONSTRAINT knowledge_point_content_maps_pkey PRIMARY KEY (id);


--
-- Name: knowledge_points knowledge_points_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY knowledge_points
    ADD CONSTRAINT knowledge_points_pkey PRIMARY KEY (id);


--
-- Name: logs like__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY logs
    ADD CONSTRAINT like__pk PRIMARY KEY (id);


--
-- Name: medias media__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY medias
    ADD CONSTRAINT media__pk PRIMARY KEY (id);


--
-- Name: pinyin_texts pinyin_texts_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY pinyin_texts
    ADD CONSTRAINT pinyin_texts_pkey PRIMARY KEY (id);


--
-- Name: problem_options problems_options_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY problem_options
    ADD CONSTRAINT problems_options_pkey PRIMARY KEY (id);


--
-- Name: problems problems_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY problems
    ADD CONSTRAINT problems_pkey PRIMARY KEY (id);


--
-- Name: problem_standard_answers problems_standard_answers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY problem_standard_answers
    ADD CONSTRAINT problems_standard_answers_pkey PRIMARY KEY (id);


--
-- Name: properties property__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT property__pk PRIMARY KEY (id);


--
-- Name: quotes quotes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY quotes
    ADD CONSTRAINT quotes_pkey PRIMARY KEY (id);


--
-- Name: schedulers schedulers_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY schedulers
    ADD CONSTRAINT schedulers_pkey PRIMARY KEY (id);


--
-- Name: sessions session__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY sessions
    ADD CONSTRAINT session__pk PRIMARY KEY (id);


--
-- Name: subjects subjects_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY subjects
    ADD CONSTRAINT subjects_pkey PRIMARY KEY (id);


--
-- Name: tags tag__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT tag__pk PRIMARY KEY (id);


--
-- Name: transactions transactions_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY transactions
    ADD CONSTRAINT transactions_pkey PRIMARY KEY (id);


--
-- Name: users user__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user__pk PRIMARY KEY (id);


--
-- Name: validation_codes validation_codes_pkey; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY validation_codes
    ADD CONSTRAINT validation_codes_pkey PRIMARY KEY (id);


--
-- Name: videos video__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY videos
    ADD CONSTRAINT video__pk PRIMARY KEY (id);


--
-- Name: volumes volume__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY volumes
    ADD CONSTRAINT volume__pk PRIMARY KEY (id);


--
-- Name: wechat_users wechat_user__pk; Type: CONSTRAINT; Schema: public; Owner: postgres
--

ALTER TABLE ONLY wechat_users
    ADD CONSTRAINT wechat_user__pk PRIMARY KEY (id);


--
-- Name: comment_content__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX comment_content__idx ON comments USING gin (to_tsvector('english'::regconfig, content));


--
-- Name: property_name__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX property_name__idx ON properties USING btree (name);


--
-- Name: session_identity__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX session_identity__idx ON sessions USING btree (identity);


--
-- Name: tag_name__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX tag_name__idx ON tags USING btree (name);


--
-- Name: user_email__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_email__idx ON users USING btree (email);


--
-- Name: user_login_name__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_login_name__idx ON users USING btree (login_name);


--
-- Name: user_name__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE INDEX user_name__idx ON users USING btree (name);


--
-- Name: user_telephone__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX user_telephone__idx ON users USING btree (telephone);


--
-- Name: wechat_user_token__idx; Type: INDEX; Schema: public; Owner: postgres
--

CREATE UNIQUE INDEX wechat_user_token__idx ON wechat_users USING btree (token);


--
-- Name: public; Type: ACL; Schema: -; Owner: postgres
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM postgres;
GRANT ALL ON SCHEMA public TO postgres;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

