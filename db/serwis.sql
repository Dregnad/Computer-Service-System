--
-- PostgreSQL database dump
--

\restrict hnK8YHroE9xR4qSpffZe6o4XcYExq48ZTyl0ze3lAu0RgKRYR3Xn1psXBeGn03E

-- Dumped from database version 14.9
-- Dumped by pg_dump version 18.1

-- Started on 2026-01-29 20:40:52

SET statement_timeout = 0;
SET lock_timeout = 0;
SET idle_in_transaction_session_timeout = 0;
SET transaction_timeout = 0;
SET client_encoding = 'UTF8';
SET standard_conforming_strings = on;
SELECT pg_catalog.set_config('search_path', '', false);
SET check_function_bodies = false;
SET xmloption = content;
SET client_min_messages = warning;
SET row_security = off;

--
-- TOC entry 40 (class 2615 OID 1344666)
-- Name: serwis; Type: SCHEMA; Schema: -; Owner: 2024_kiermasz_daniel
--

CREATE SCHEMA serwis;


ALTER SCHEMA serwis OWNER TO "2024_kiermasz_daniel";

SET default_tablespace = '';

SET default_table_access_method = heap;

--
-- TOC entry 477 (class 1259 OID 1344696)
-- Name: cennik_czesci; Type: TABLE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE TABLE serwis.cennik_czesci (
    id integer NOT NULL,
    typ_urzadzenia character varying(50),
    komponent character varying(50),
    cena_sugerowana numeric(10,2)
);


ALTER TABLE serwis.cennik_czesci OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 476 (class 1259 OID 1344695)
-- Name: cennik_czesci_id_seq; Type: SEQUENCE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE SEQUENCE serwis.cennik_czesci_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE serwis.cennik_czesci_id_seq OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 5449 (class 0 OID 0)
-- Dependencies: 476
-- Name: cennik_czesci_id_seq; Type: SEQUENCE OWNED BY; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER SEQUENCE serwis.cennik_czesci_id_seq OWNED BY serwis.cennik_czesci.id;


--
-- TOC entry 479 (class 1259 OID 1344703)
-- Name: magazyn; Type: TABLE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE TABLE serwis.magazyn (
    id integer NOT NULL,
    typ_urzadzenia character varying(50),
    klucz_czesci character varying(50),
    nazwa_wyswietlana character varying(100),
    czy_dostepna boolean DEFAULT true
);


ALTER TABLE serwis.magazyn OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 478 (class 1259 OID 1344702)
-- Name: magazyn_id_seq; Type: SEQUENCE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE SEQUENCE serwis.magazyn_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE serwis.magazyn_id_seq OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 5450 (class 0 OID 0)
-- Dependencies: 478
-- Name: magazyn_id_seq; Type: SEQUENCE OWNED BY; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER SEQUENCE serwis.magazyn_id_seq OWNED BY serwis.magazyn.id;


--
-- TOC entry 475 (class 1259 OID 1344676)
-- Name: zlecenia; Type: TABLE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE TABLE serwis.zlecenia (
    id integer NOT NULL,
    order_id character varying(50) NOT NULL,
    status character varying(20) DEFAULT 'NOWE'::character varying,
    klient_imie character varying(100),
    klient_nazwisko character varying(100),
    klient_email character varying(255),
    klient_telefon character varying(20),
    klient_adres text,
    typ_urzadzenia character varying(50),
    komponent character varying(100),
    opis_usterki text,
    notatki_technika text,
    cena_finalna numeric(10,2),
    czy_oferta_zaakceptowana boolean,
    czy_czesci_zamowione boolean DEFAULT false,
    opis_naprawy text,
    czy_naprawa_udana boolean,
    numer_listu_przewozowego character varying(100),
    czy_wyslano boolean DEFAULT false,
    data_utworzenia timestamp without time zone DEFAULT CURRENT_TIMESTAMP,
    data_aktualizacji timestamp without time zone DEFAULT CURRENT_TIMESTAMP
);


ALTER TABLE serwis.zlecenia OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 474 (class 1259 OID 1344675)
-- Name: zlecenia_id_seq; Type: SEQUENCE; Schema: serwis; Owner: 2024_kiermasz_daniel
--

CREATE SEQUENCE serwis.zlecenia_id_seq
    AS integer
    START WITH 1
    INCREMENT BY 1
    NO MINVALUE
    NO MAXVALUE
    CACHE 1;


ALTER SEQUENCE serwis.zlecenia_id_seq OWNER TO "2024_kiermasz_daniel";

--
-- TOC entry 5451 (class 0 OID 0)
-- Dependencies: 474
-- Name: zlecenia_id_seq; Type: SEQUENCE OWNED BY; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER SEQUENCE serwis.zlecenia_id_seq OWNED BY serwis.zlecenia.id;


--
-- TOC entry 5256 (class 2604 OID 1344699)
-- Name: cennik_czesci id; Type: DEFAULT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.cennik_czesci ALTER COLUMN id SET DEFAULT nextval('serwis.cennik_czesci_id_seq'::regclass);


--
-- TOC entry 5257 (class 2604 OID 1344706)
-- Name: magazyn id; Type: DEFAULT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.magazyn ALTER COLUMN id SET DEFAULT nextval('serwis.magazyn_id_seq'::regclass);


--
-- TOC entry 5250 (class 2604 OID 1344679)
-- Name: zlecenia id; Type: DEFAULT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.zlecenia ALTER COLUMN id SET DEFAULT nextval('serwis.zlecenia_id_seq'::regclass);


--
-- TOC entry 5441 (class 0 OID 1344696)
-- Dependencies: 477
-- Data for Name: cennik_czesci; Type: TABLE DATA; Schema: serwis; Owner: 2024_kiermasz_daniel
--

COPY serwis.cennik_czesci (id, typ_urzadzenia, komponent, cena_sugerowana) FROM stdin;
1	laptop	screen	450.00
2	laptop	keyboard	150.00
3	laptop	battery	220.00
4	pc	storage	300.00
5	pc	motherboard	600.00
6	pc	cooling	120.00
7	smartphone	screen	350.00
8	smartphone	battery	100.00
9	smartphone	camera	250.00
10	tablet	screen	400.00
11	tablet	charging_port	150.00
12	console	hdmi	200.00
13	console	drive	350.00
14	console	pad	250.00
15	printer	ink	180.00
16	printer	rollers	90.00
17	display	panel	500.00
18	display	lamp	300.00
19	laptop	motherboard	850.00
20	laptop	cooling	180.00
21	pc	ram	250.00
22	pc	psu	350.00
23	pc	gpu	1200.00
24	pc	mobo_cpu	950.00
25	smartphone	charging_port	120.00
26	printer	drum	220.00
27	printer	rollers	80.00
28	console	cleaning	150.00
29	display	power	200.00
\.


--
-- TOC entry 5443 (class 0 OID 1344703)
-- Dependencies: 479
-- Data for Name: magazyn; Type: TABLE DATA; Schema: serwis; Owner: 2024_kiermasz_daniel
--

COPY serwis.magazyn (id, typ_urzadzenia, klucz_czesci, nazwa_wyswietlana, czy_dostepna) FROM stdin;
1	laptop	screen	Matryca / Ekran	t
2	laptop	keyboard	Klawiatura	f
3	laptop	battery	Bateria	t
4	laptop	storage	Dysk SSD/HDD	t
5	pc	psu	Zasilacz	t
6	pc	gpu	Karta Graficzna	f
7	pc	mobo_cpu	Płyta Główna / CPU	t
8	smartphone	screen	Wyświetlacz	t
9	smartphone	battery	Bateria	f
10	tablet	charging_port	Złącze ładowania	t
11	console	hdmi	Port HDMI	t
12	console	drive	Napęd	f
13	printer	ink_toner	Tusz / Toner	t
14	display	panel	Matryca Monitora	t
15	laptop	motherboard	Płyta Główna	t
16	laptop	cooling	Układ chłodzenia	t
17	pc	ram	Pamięć RAM	t
18	pc	storage	Dysk	t
19	smartphone	charging_port	Złącze ładowania	t
20	smartphone	camera	Aparat	t
21	tablet	screen	Wyświetlacz	t
22	tablet	battery	Bateria	f
23	tablet	camera	Aparat	t
24	printer	drum	Bęben	t
25	printer	rollers	Rolki papieru	t
26	printer	printhead	Głowica	f
27	console	hdd	Dysk HDD	t
28	console	cleaning	Czyszczenie / Konserwacja	t
29	monitor	power	Zasilacz wewnętrzny	t
30	monitor	ports	Gniazda wejściowe	t
31	pc	ram	Pamięć RAM	t
32	pc	storage	Dysk	t
33	smartphone	charging_port	Złącze ładowania	t
34	tablet	camera	Aparat	t
35	printer	drum	Bęben	t
36	printer	rollers	Rolki papieru	t
37	console	cleaning	Czyszczenie / Konserwacja	t
\.


--
-- TOC entry 5439 (class 0 OID 1344676)
-- Dependencies: 475
-- Data for Name: zlecenia; Type: TABLE DATA; Schema: serwis; Owner: 2024_kiermasz_daniel
--

COPY serwis.zlecenia (id, order_id, status, klient_imie, klient_nazwisko, klient_email, klient_telefon, klient_adres, typ_urzadzenia, komponent, opis_usterki, notatki_technika, cena_finalna, czy_oferta_zaakceptowana, czy_czesci_zamowione, opis_naprawy, czy_naprawa_udana, numer_listu_przewozowego, czy_wyslano, data_utworzenia, data_aktualizacji) FROM stdin;
\.


--
-- TOC entry 5452 (class 0 OID 0)
-- Dependencies: 476
-- Name: cennik_czesci_id_seq; Type: SEQUENCE SET; Schema: serwis; Owner: 2024_kiermasz_daniel
--

SELECT pg_catalog.setval('serwis.cennik_czesci_id_seq', 29, true);


--
-- TOC entry 5453 (class 0 OID 0)
-- Dependencies: 478
-- Name: magazyn_id_seq; Type: SEQUENCE SET; Schema: serwis; Owner: 2024_kiermasz_daniel
--

SELECT pg_catalog.setval('serwis.magazyn_id_seq', 37, true);


--
-- TOC entry 5454 (class 0 OID 0)
-- Dependencies: 474
-- Name: zlecenia_id_seq; Type: SEQUENCE SET; Schema: serwis; Owner: 2024_kiermasz_daniel
--

SELECT pg_catalog.setval('serwis.zlecenia_id_seq', 1, false);


--
-- TOC entry 5264 (class 2606 OID 1344701)
-- Name: cennik_czesci cennik_czesci_pkey; Type: CONSTRAINT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.cennik_czesci
    ADD CONSTRAINT cennik_czesci_pkey PRIMARY KEY (id);


--
-- TOC entry 5266 (class 2606 OID 1344709)
-- Name: magazyn magazyn_pkey; Type: CONSTRAINT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.magazyn
    ADD CONSTRAINT magazyn_pkey PRIMARY KEY (id);


--
-- TOC entry 5260 (class 2606 OID 1344690)
-- Name: zlecenia zlecenia_order_id_key; Type: CONSTRAINT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.zlecenia
    ADD CONSTRAINT zlecenia_order_id_key UNIQUE (order_id);


--
-- TOC entry 5262 (class 2606 OID 1344688)
-- Name: zlecenia zlecenia_pkey; Type: CONSTRAINT; Schema: serwis; Owner: 2024_kiermasz_daniel
--

ALTER TABLE ONLY serwis.zlecenia
    ADD CONSTRAINT zlecenia_pkey PRIMARY KEY (id);


-- Completed on 2026-01-29 20:40:54

--
-- PostgreSQL database dump complete
--

\unrestrict hnK8YHroE9xR4qSpffZe6o4XcYExq48ZTyl0ze3lAu0RgKRYR3Xn1psXBeGn03E

