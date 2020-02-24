SET SERVEROUTPUT ON;

create or replace function geo_distance(
  lo1 float, lat1 float, lo2 float, lat2 float)
return float as dist float;

begin   
select sdo_geom.sdo_distance (
         sdo_geometry (
            -- this identifies the object as a two-dimensional point.
             2001,
             -- this identifies the object as using the GCS_WGS_1984 geographic coordinate system.
             4326, null, sdo_elem_info_array(1, 1, 1),
             -- this is the longitude and latitude of point 1.
             sdo_ordinate_array(lo1, lat1)
          ),
    sdo_geometry (
             2001,
             4326, null, sdo_elem_info_array(1, 1, 1),
             -- this is the longitude and latitude of point 2.
             sdo_ordinate_array(lo2, lat2)
          ),  1,  'unit=M'
       ) into dist from dual;
return dist;
end geo_distance;
/