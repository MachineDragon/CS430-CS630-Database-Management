set echo on
SELECT b.name, b.stars, b.review_count 
FROM yelp_db.business b, yelp_db.category c 
WHERE b.id = c.business_id AND b.city = 'Las Vegas' AND b.state = 'NV' AND c.category = 'Restaurants' 
AND 200 > sdo_geom.sdo_distance ( 
   sdo_geometry (2001, 4326, null, sdo_elem_info_array(1, 1, 1), 
     sdo_ordinate_array( 
       (SELECT a.longitude FROM yelp_db.apartments a WHERE a.listing = 120), 
       (SELECT a.latitude FROM yelp_db.apartments a WHERE a.listing = 120) 
    )), 
   sdo_geometry (2001,4326, null, sdo_elem_info_array(1, 1, 1),
     sdo_ordinate_array( 
       b.longitude, b.latitude  )), 1, 'unit=M')
 GROUP BY b.id, b.name, b.stars, b.review_count
 HAVING b.review_count >= 10;
 
