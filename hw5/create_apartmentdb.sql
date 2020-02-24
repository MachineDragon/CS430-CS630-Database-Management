create table apartments as (select * from yelp_db.apartments);
alter table apartments modify listing number PRIMARY KEY;
create table rented_apartments AS (
					select listing AS id, neighborhood, address, city, state, postal_code, latitude, longitude 
					from apartments Where listing is null and neighborhood is null and address is null 
					and city is null and state is null and postal_code is null and latitude is null and longitude is null);
alter table rented_apartments modify id PRIMARY KEY;