create table public.places
(
    id                 bigserial
        constraint places_pk
            primary key,
    name               text not null,
    zone               text,
    address            text,
    phone              text,
    username           text,
    password           text,
    lunch_served_from  time,
    lunch_served_until time,
    lunch_delivery     boolean
);


create table public.offers
(
    id       bigserial
        constraint offers_pk
            primary key,
    offer    text,
    date     date,
    place_id bigint,
    price    numeric
);

create table if not exists rankings
(
    id              serial,
    name            text,
    restaurants_ids text
);

alter table places
    add description text;

create table public.lunch_pictures
(
    id       serial
        constraint lunch_pictures_pk
            primary key,
    url      text,
    place_id integer,
    date     date
);

create table public.place_opening_hours
(
    id       serial
        constraint place_opening_hours_pk
            primary key,
    weekday  integer,
    "from"   time,
    "to"     time,
    place_id integer
);




