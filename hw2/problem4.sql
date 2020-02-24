    select c.cid, a.aid, p.pid from customers c, agents a, products p
       where c.city = a.city and a.city = p.city;

    select c.cid, a.aid, p.pid
     from customers c, agents a, products p
       where c.city != a.city and a.city != p.city
        and c.city != p.city;

  select pname from products where pid in
   (select pid from orders where
    cid in (select cid from customers where city = 'Dallas') and
    aid in (select aid from agents where city = 'Tokyo'));

  select distinct pname 
   from products p, orders o, customers c, agents a
    where p.pid = o.pid and o.cid = c.cid and c.city = 'Dallas'
      and o.aid = a.aid and a.city = 'Tokyo';

  select a1.aid, a2.aid from agents a1, agents a2
    where a1.city = a2.city and a1.aid < a2.aid;

  select cid from customers
    where discnt >=all (
    select discnt from customers );

  select cid from customers
    where discnt <=all (
    select discnt from customers );


  select distinct pid from orders where aid = 'a03'
    and pid not in ( select pid from orders where aid = 'a06');

  select a.aid, a.aname from agents a where a.aname like 'S%' 
    and a.aid not in 
      (select o.aid from orders o where o.pid in 
      (select pid from products where city = 'Newark'));

  select distinct aname from agents a where not exists
    (select * from orders x where x.cid = 'c002' and not exists 
    (select * from orders y where y.aid = a.aid
      and x.pid = y.pid ));

  select cid, aid, pid from customers c, agents a, products p
    where c.city != a.city or c.city != p.city 
      or a.city != p.city;

  select distinct aid from customers c, orders o
    where c.cid = o.cid
    and o.dollars > 500 and c.city = 'Kyoto';



    select distinct cid from orders o where not exists
       (select x.cid from orders x, orders y
            where x.aid <> y.aid and x.cid = o.cid and y.cid = o.cid);
 select aid from agents where percent >any
   (select percent from agents );

  select aid from agents where percent >any 
    (select percent from agents);

   select distinct cid from orders, agents
    where orders.aid = agents.aid
    and (agents.city = 'Duluth' or agents.city = 'Dallas');

  select cid from orders y where not exists
    (select o.cid from orders o, agents a 
      where city = 'New York' 
       and not exists
        (select * from orders x 
         where x.cid=o.cid and x.aid = a.aid) 
          and y.cid = o.cid);
