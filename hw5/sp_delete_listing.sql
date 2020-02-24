create or replace
procedure delete_listing(listing_param integer) is
begin 
insert into rented_apartments select * from apartments where listing = listing_param;
delete from apartments where listing = listing_param;
commit;
exception
when others then 
rollback;
end;
/
