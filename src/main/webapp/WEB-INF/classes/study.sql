--
-- PostgreSQL database dump
--

-- Dumped from database version 9.5.4
-- Dumped by pg_dump version 9.5.4

SET statement_timeout = 0;
SET lock_timeout = 0;
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
-- Name: additionals; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE additionals (
    id bigint NOT NULL,
    table_name name DEFAULT ''::name NOT NULL,
    object_id bigint DEFAULT 0 NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    value text DEFAULT ''::text NOT NULL
);


ALTER TABLE additionals OWNER TO fixopen;

--
-- Name: cards; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE cards (
    id bigint NOT NULL,
    user_id bigint,
    no name DEFAULT ''::name NOT NULL,
    password name DEFAULT ''::name NOT NULL,
    active_time timestamp without time zone,
    end_time timestamp without time zone,
    duration interval,
    subject bigint
);


ALTER TABLE cards OWNER TO fixopen;

--
-- Name: comments; Type: TABLE; Schema: public; Owner: fixopen
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


ALTER TABLE comments OWNER TO fixopen;

--
-- Name: devices; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE devices (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    platform name DEFAULT 'iOS'::name NOT NULL,
    platform_identity name DEFAULT ''::name NOT NULL,
    platform_notification_token name DEFAULT ''::name NOT NULL
);


ALTER TABLE devices OWNER TO fixopen;

--
-- Name: medias; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE medias (
    id bigint NOT NULL,
    mime_type name DEFAULT ''::name NOT NULL,
    size bigint DEFAULT '-1'::integer NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    ext name DEFAULT ''::name NOT NULL,
    store_path character varying(256) DEFAULT ''::character varying NOT NULL
);


ALTER TABLE medias OWNER TO fixopen;

--
-- Name: images; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE images (
    main_color integer
)
INHERITS (medias);


ALTER TABLE images OWNER TO fixopen;

--
-- Name: knowledge_points; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE knowledge_points (
    id bigint NOT NULL,
    subject_id bigint,
    volume_id bigint,
    grade integer,
    title name,
    "order" integer,
    store_path character varying(256),
    video_url character varying(256)
);


ALTER TABLE knowledge_points OWNER TO fixopen;

--
-- Name: likes; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE likes (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    object_type name DEFAULT ''::name NOT NULL,
    object_id bigint DEFAULT 0 NOT NULL,
    create_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE likes OWNER TO fixopen;

--
-- Name: problems; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE problems (
    id bigint NOT NULL,
    subject_id bigint,
    volume_id bigint,
    knowledge_point_id bigint,
    title character varying,
    options name[],
    standard_answers integer[],
    "order" integer,
    store_path character varying(256),
    video_url character varying(256)
);


ALTER TABLE problems OWNER TO fixopen;

--
-- Name: properties; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE properties (
    id bigint NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    value text DEFAULT ''::text NOT NULL
);


ALTER TABLE properties OWNER TO fixopen;

--
-- Name: schedulers; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE schedulers (
    id bigint NOT NULL,
    year integer NOT NULL,
    week integer NOT NULL,
    state integer DEFAULT 0 NOT NULL,
    start_time time without time zone,
    end_time time without time zone,
    duration interval,
    subject_id bigint,
    grade integer,
    title name,
    description text,
    teacher name,
    teacher_description text
);


ALTER TABLE schedulers OWNER TO fixopen;

--
-- Name: sessions; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE sessions (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    device_id bigint DEFAULT 0 NOT NULL,
    identity name DEFAULT ''::name NOT NULL,
    last_operation_time timestamp without time zone DEFAULT now() NOT NULL,
    ip name DEFAULT '0.0.0.0'::name NOT NULL
);


ALTER TABLE sessions OWNER TO fixopen;

--
-- Name: subjects; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE subjects (
    id bigint NOT NULL,
    name name
);


ALTER TABLE subjects OWNER TO fixopen;

--
-- Name: tags; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE tags (
    id bigint NOT NULL,
    name name DEFAULT ''::name NOT NULL
);


ALTER TABLE tags OWNER TO fixopen;

--
-- Name: users; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE users (
    id bigint NOT NULL,
    login_name name DEFAULT ''::name NOT NULL,
    password name DEFAULT ''::name NOT NULL,
    is_administrator boolean DEFAULT false NOT NULL,
    name name DEFAULT ''::name NOT NULL,
    head character varying(256) DEFAULT ''::character varying NOT NULL,
    email character varying(256),
    telephone character varying(256),
    birthday timestamp without time zone,
    sex integer DEFAULT 0 NOT NULL,
    amount real DEFAULT 0.0 NOT NULL,
    school name,
    grade name,
    class name,
    site character varying(256) DEFAULT ''::character varying NOT NULL,
    location character varying(256),
    description text,
    timezone jsonb,
    create_time timestamp without time zone DEFAULT now() NOT NULL,
    update_time timestamp without time zone DEFAULT now() NOT NULL
);


ALTER TABLE users OWNER TO fixopen;

--
-- Name: videos; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE videos (
    duration interval DEFAULT '00:00:00'::interval NOT NULL,
    bit_rate real DEFAULT 0.0 NOT NULL
)
INHERITS (medias);


ALTER TABLE videos OWNER TO fixopen;

--
-- Name: volumes; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE volumes (
    id bigint NOT NULL,
    subject_id bigint,
    grade integer,
    title name
);


ALTER TABLE volumes OWNER TO fixopen;

--
-- Name: wechat_users; Type: TABLE; Schema: public; Owner: fixopen
--

CREATE TABLE wechat_users (
    id bigint NOT NULL,
    user_id bigint DEFAULT 0 NOT NULL,
    token character varying(8192) DEFAULT ''::character varying NOT NULL,
    refresh_token character varying(8192),
    expiry timestamp without time zone,
    ref_id name DEFAULT ''::name NOT NULL,
    open_id name,
    union_id character varying(256),
    nickname name,
    sex bigint,
    city name,
    province name,
    country name,
    head character varying(256),
    privilege name[],
    info jsonb DEFAULT '{}'::jsonb NOT NULL
);


ALTER TABLE wechat_users OWNER TO fixopen;

--
-- Name: mime_type; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images ALTER COLUMN mime_type SET DEFAULT ''::name;


--
-- Name: size; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images ALTER COLUMN size SET DEFAULT '-1'::integer;


--
-- Name: name; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images ALTER COLUMN name SET DEFAULT ''::name;


--
-- Name: ext; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images ALTER COLUMN ext SET DEFAULT ''::name;


--
-- Name: store_path; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images ALTER COLUMN store_path SET DEFAULT ''::character varying;


--
-- Name: mime_type; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos ALTER COLUMN mime_type SET DEFAULT ''::name;


--
-- Name: size; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos ALTER COLUMN size SET DEFAULT '-1'::integer;


--
-- Name: name; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos ALTER COLUMN name SET DEFAULT ''::name;


--
-- Name: ext; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos ALTER COLUMN ext SET DEFAULT ''::name;


--
-- Name: store_path; Type: DEFAULT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos ALTER COLUMN store_path SET DEFAULT ''::character varying;


--
-- Data for Name: additionals; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: cards; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: comments; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: devices; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: images; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: knowledge_points; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: likes; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: medias; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: problems; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: properties; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: schedulers; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: sessions; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: subjects; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: tags; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: users; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: videos; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: volumes; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Data for Name: wechat_users; Type: TABLE DATA; Schema: public; Owner: fixopen
--



--
-- Name: card__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY cards
    ADD CONSTRAINT card__pk PRIMARY KEY (id);


--
-- Name: comment__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY comments
    ADD CONSTRAINT comment__pk PRIMARY KEY (id);


--
-- Name: device__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY devices
    ADD CONSTRAINT device__pk PRIMARY KEY (id);


--
-- Name: image__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY images
    ADD CONSTRAINT image__pk PRIMARY KEY (id);


--
-- Name: knowledge_point__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY knowledge_points
    ADD CONSTRAINT knowledge_point__pk PRIMARY KEY (id);


--
-- Name: like__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY likes
    ADD CONSTRAINT like__pk PRIMARY KEY (id);


--
-- Name: media__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY medias
    ADD CONSTRAINT media__pk PRIMARY KEY (id);


--
-- Name: problem__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY problems
    ADD CONSTRAINT problem__pk PRIMARY KEY (id);


--
-- Name: property__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY properties
    ADD CONSTRAINT property__pk PRIMARY KEY (id);


--
-- Name: scheduler__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY schedulers
    ADD CONSTRAINT scheduler__pk PRIMARY KEY (id);


--
-- Name: session__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY sessions
    ADD CONSTRAINT session__pk PRIMARY KEY (id);


--
-- Name: subject__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY subjects
    ADD CONSTRAINT subject__pk PRIMARY KEY (id);


--
-- Name: tag__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY tags
    ADD CONSTRAINT tag__pk PRIMARY KEY (id);


--
-- Name: user__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY users
    ADD CONSTRAINT user__pk PRIMARY KEY (id);


--
-- Name: video__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY videos
    ADD CONSTRAINT video__pk PRIMARY KEY (id);


--
-- Name: volume__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY volumes
    ADD CONSTRAINT volume__pk PRIMARY KEY (id);


--
-- Name: wechat_user__pk; Type: CONSTRAINT; Schema: public; Owner: fixopen
--

ALTER TABLE ONLY wechat_users
    ADD CONSTRAINT wechat_user__pk PRIMARY KEY (id);


--
-- Name: comment_content__idx; Type: INDEX; Schema: public; Owner: fixopen
--

CREATE INDEX comment_content__idx ON comments USING gin (to_tsvector('english'::regconfig, content));


--
-- Name: property_name__idx; Type: INDEX; Schema: public; Owner: fixopen
--

CREATE UNIQUE INDEX property_name__idx ON properties USING btree (name);


--
-- Name: tag_name__idx; Type: INDEX; Schema: public; Owner: fixopen
--

CREATE INDEX tag_name__idx ON tags USING btree (name);


--
-- Name: wechat_user_token__idx; Type: INDEX; Schema: public; Owner: fixopen
--

CREATE UNIQUE INDEX wechat_user_token__idx ON wechat_users USING btree (token);


--
-- Name: public; Type: ACL; Schema: -; Owner: fixopen
--

REVOKE ALL ON SCHEMA public FROM PUBLIC;
REVOKE ALL ON SCHEMA public FROM fixopen;
GRANT ALL ON SCHEMA public TO fixopen;
GRANT ALL ON SCHEMA public TO PUBLIC;


--
-- PostgreSQL database dump complete
--

