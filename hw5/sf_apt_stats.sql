CREATE OR REPLACE 
function apartment_stats(listing_param int, category_param VARCHAR2) RETURN int AS stats int;
BEGIN
SELECT count(*)
INTO stats
FROM yelp_db.category c, yelp_db.business b, yelp_db.apartments a
WHERE a.listing=listing_param AND c.CATEGORY=category_param 
AND a.neighborhood=b.neighborhood AND b.city='Las Vegas' AND b.state='NV'AND b.id=c.business_id AND b.review_count>=10 and geo_distance(a.longitude, a.latitude,b.longitude, b.latitude) <200;
RETURN stats;
END;
/

show errors

select apartment_stats(25, 'Restaurants') from dual;
select apartment_stats(25, 'Grocery') from dual;
